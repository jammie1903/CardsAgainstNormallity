package cards.game;

import cards.bean.Player;
import cards.card.BestAnswerControl;
import cards.card.CardControl;
import cards.card.HandControl;
import cards.communication.Connection;
import cards.communication.MessageHandler;
import cards.data.CardData;
import cards.data.PlayerData;
import cards.player.PlayerControl;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GameController implements Initializable, MessageHandler {

    private static final double MESSAGE_PANE_MIN_WIDTH = 200;
    private static final double HAND_DISABLED_OPACITY = 0.55;
    @FXML
    private Button confirmButton;
    @FXML
    private Label hostLabel;
    @FXML
    private BorderPane gamePane;
    @FXML
    private HandControl hand;

    private HandControl answerHand = new HandControl();

    @FXML
    private GridView<Player> playerDisplay;
    @FXML
    private SplitPane mainPane;
    private Button startGameButton;
    private String question = null;

    private static GameController instance = null;

    public static GameController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        Connection.addMessageHandler(this);
        playerDisplay.setItems(PlayerData.getAll());
        playerDisplay.setCellWidth(140);
        playerDisplay.setCellFactory(new Callback<GridView<Player>, GridCell<Player>>() {
            @Override
            public GridCell<Player> call(GridView<Player> param) {
                return new GridCell<Player>() {
                    @Override
                    protected void updateItem(Player player, boolean empty) {
                        super.updateItem(player, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new PlayerControl(player));
                        }
                    }
                };
            }
        });

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/messengerView.fxml"));
            Parent root = loader.load();
            mainPane.getItems().add(root);
            mainPane.setDividerPosition(0, 1.0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InvalidationListener widthModifier = observable -> {
            double width = mainPane.getScene().getWidth() * mainPane.getDividers().get(0).getPosition();
            double maxWidth = mainPane.getScene().getWidth() - MESSAGE_PANE_MIN_WIDTH - 20;
            hand.setMinWidth(Math.min(width, maxWidth));
            hand.setMaxWidth(Math.min(width, maxWidth));
            answerHand.setMinWidth(Math.min(width, maxWidth));
            answerHand.setMaxWidth(Math.min(width, maxWidth));
        };

        confirmButton.disableProperty().bind(hand.allCardsSelectedProperty().not());

        mainPane.getDividers().get(0).positionProperty().addListener(widthModifier);

        mainPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                oldValue.widthProperty().removeListener(widthModifier);
            } else if (newValue != null) {
                newValue.widthProperty().addListener(widthModifier);
            }
        });
    }

    private void confirmBestAnswer(ActionEvent actionEvent) {
        if (answerHand.getSelectedCards().size() > 0) {
            Player player = PlayerData.get(answerHand.getSelectedCards().get(0).getPlayerId());
            gamePane.setCenter(new BestAnswerControl(player, answerHand.getSelectedCards().get(0)));
            Connection.get().sendMessage("WINNER:" + player.getId());
            player.addPoint();
            answerHand.getChildren().clear();
        }
    }

    @Override
    public void handleMessage(int clientId, String messageCategory, String message) {
        switch (messageCategory) {
            case "QUESTIONMASTER":
                Platform.runLater(() -> {
                    PlayerData.setQuestionMaster(Integer.valueOf(message));
                    gamePane.setCenter(null);
                    hand.setOpacity(Connection.get().getId() == Integer.valueOf(message) ? HAND_DISABLED_OPACITY : 1);
                    if (hand.getChildren().size() < 10) {
                        Connection.get().sendMessage("REQUESTANSWERCARD:" + (10 - hand.getChildren().size()));
                    }
                    PlayerData.clearCardsPlayed();
                });
                break;
            case "QUESTION":
                Platform.runLater(() -> {
                    question = message;
                    CardControl card = new CardControl(message, true);
                    card.setFaceDown();
                    gamePane.setCenter(card);
                    confirmButton.setVisible(true);
                    card.flip();
                    hand.setCardSelectionAmount(card.getRequiredAnswerCardCount());
                });
                if (Connection.get().isHost()) {
                    Connection.get().forwardMessage(clientId, "QUESTION:" + message);
                }
                break;
            case "ANSWER":
                Platform.runLater(() ->
                        hand.getChildren().add(new CardControl(message, false)));
                break;
            case "CARDSPLAYED":
                Platform.runLater(() -> {
                    PlayerData.get(clientId).setCardsPlayed(message.split("\\|"));
                    if (Connection.get().isHost()) {
                        Connection.get().forwardMessage(clientId, "CARDSPLAYED:" + message);
                    }
                    checkAllCardsArePlayedAndShowAnswers();
                });
                break;
            case "WINNER":
                Platform.runLater(() -> {
                    Player player = PlayerData.get(Integer.valueOf(message));
                    answerHand.getChildren().stream().map((c) -> (CardControl) c)
                            .filter((c) -> c.getPlayerId() == player.getId()).findFirst()
                            .ifPresent((selectedAnswer) -> gamePane.setCenter(new BestAnswerControl(player, selectedAnswer)));
                    player.addPoint();
                    answerHand.getChildren().clear();
                    if (Connection.get().isHost()) {
                        Connection.get().forwardMessage(clientId, "WINNER:" + message);
                    }
                });
                break;
            case "CONTINUE":
                continueGame(Integer.valueOf(message));
                break;
            case "NEXTQUESTION":
                Platform.runLater(() -> {
                    question = message;
                    placeNextQuestionFaceDown();
                });
                break;
            case "REQUESTANSWERCARD":
                for (int c = 0; c < Integer.parseInt(message); c++) {
                    Connection.get().sendMessageTo(clientId, "ANSWER:" + CardData.drawAnswerCard());
                }
                break;
        }
    }

    private void checkAllCardsArePlayedAndShowAnswers() {
        if (PlayerData.allCardsPlayed()) {
            PlayerData.getAll().stream().filter(player -> !player.isQuestionMaster()).forEach(player -> {
                CardControl answerCard = new CardControl(question, player.getCardsPlayed());
                answerCard.setPlayerId(player.getId());
                answerCard.setFaceDown();
                answerCard.flip();
                answerHand.getChildren().add(answerCard);
            });
            gamePane.setCenter(getAnswerDisplay());
            answerHand.setCardSelectionAmount(PlayerData.isQuestionMaster(Connection.get().getId()) ? 1 : 0);
        }
    }

    private Node getAnswerDisplay() {
        if (PlayerData.isQuestionMaster(Connection.get().getId())) {
            VBox answerHandContainer = new VBox(10);
            answerHandContainer.setAlignment(Pos.CENTER);
            Button confirmBestAnswerButton = new Button("Confirm");
            confirmBestAnswerButton.setOnAction(this::confirmBestAnswer);
            answerHandContainer.getChildren().addAll(answerHand, confirmBestAnswerButton);
            return answerHandContainer;
        }
        return answerHand;
    }

    public void setHostName(String hostName) {
        hostLabel.setText("Host name: " + hostName);
    }

    private void startGame(ActionEvent event) {
        for (int c = 0; c < 10; c++) {
            Connection.get().sendMessage("ANSWER", CardData::drawAnswerCard);
            hand.getChildren().add(new CardControl(CardData.drawAnswerCard(), false));
        }
        Connection.get().sendMessage("QUESTIONMASTER:0");
        PlayerData.setQuestionMaster(0);
        question = CardData.drawQuestionCard();
        placeNextQuestionFaceDown();
    }

    private void placeNextQuestionFaceDown() {
        CardControl card = new CardControl(question, true);
        card.setFaceDown();
        gamePane.setCenter(card);
        hand.setOpacity(HAND_DISABLED_OPACITY);
        card.setOnMouseClicked((clickEvent) -> {
            card.flip();
            Connection.get().sendMessage("QUESTION:" + question);
            card.setOnMouseClicked(null);
        });
    }

    public void initialiseAsHost() {
        startGameButton = new Button("Start Game");
        PlayerData.getAll().addListener((ListChangeListener<Player>) c -> startGameButton.setDisable(c.getList().size() < 3));
        startGameButton.setOnAction(this::startGame);
        gamePane.setCenter(startGameButton);
    }

    public void sendAnswerCards(ActionEvent actionEvent) {
        confirmButton.setVisible(false);
        String answerString = hand.getSelectedCards().stream().map(CardControl::getText).collect(Collectors.joining("|"));
        Connection.get().sendMessage("CARDSPLAYED:" + answerString);
        hand.removeSelectedCards();
        hand.setCardSelectionAmount(0);
        PlayerData.get(Connection.get().getId()).setCardsPlayed(answerString.split("\\|"));
        checkAllCardsArePlayedAndShowAnswers();
    }

    public void continueGame(int newQuestionMaster) {
        if (Connection.get().isHost()) {
            Platform.runLater(() -> {
                Connection.get().sendMessage("QUESTIONMASTER:" + newQuestionMaster);
                PlayerData.setQuestionMaster(newQuestionMaster);
                while (hand.getChildren().size() < 10) {
                    hand.getChildren().add(new CardControl(CardData.drawAnswerCard(), false));
                }
                PlayerData.clearCardsPlayed();
                question = CardData.drawQuestionCard();
                if (newQuestionMaster != 0) {
                    gamePane.setCenter(null);
                    hand.setOpacity(1);
                    Connection.get().sendMessageTo(newQuestionMaster, "NEXTQUESTION:" + question);
                } else {
                    placeNextQuestionFaceDown();
                }
            });
        } else {
            Connection.get().sendMessage("CONTINUE:" + Connection.get().getId());
        }
    }
}
