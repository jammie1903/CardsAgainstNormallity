package cards;

import cards.bean.HostDetails;
import cards.communication.Connection;
import cards.data.CardData;
import cards.data.DataHandler;
import cards.game.GameController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InitialController implements Initializable {
    public TextField nameField;
    public ComboBox<HostDetails> hostNameField;
    private Stage stage;
    private Parent gameView;
    private GameController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameField.setText(DataHandler.getInstance().getName());
        hostNameField.setConverter(new StringConverter<HostDetails>() {
            @Override
            public String toString(HostDetails host) {
                return host == null ? "" : host.getAddress();
            }

            @Override
            public HostDetails fromString(String address) {
                try {
                    return address.isEmpty() ? null : DataHandler.getInstance().getHostByAddress(address);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        });
        hostNameField.setCellFactory((p) ->
                new ListCell<HostDetails>() {
                    @Override
                    protected void updateItem(HostDetails item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText("");
                        } else {
                            setText(item.getName());
                        }
                    }
                });
        hostNameField.setItems(FXCollections.observableArrayList(DataHandler.getInstance().getHosts()));
    }

    public void hostDetails(ActionEvent actionEvent) throws IOException {
        if (setName()) {
            loadGameView();
            String hostName = Connection.initialiseAsHost();
            controller.setHostName(hostName);
            controller.initialiseAsHost();
            CardData.loadAnswerDeck(DataHandler.getInstance().getAnswers());
            CardData.loadQuestionDeck(DataHandler.getInstance().getQuestions());
            Scene scene = new Scene(gameView, 800, 600);
            scene.getStylesheets().add("/cards.css");
            stage.setScene(scene);
        }
    }

    public boolean setName() {
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            return false;
        }
        DataHandler.getInstance().setName(nameField.getText());
        return true;
    }

    private void loadGameView() throws IOException {
        if (gameView == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameView.fxml"));
            gameView = loader.load();
            controller = loader.getController();
        }
    }

    public void join(ActionEvent actionEvent) throws IOException {
        if (setName() && hostNameField.getSelectionModel().getSelectedItem() != null) {
            loadGameView();
            if (Connection.initialiseAsClient(hostNameField.getSelectionModel().getSelectedItem().getAddress())) {
                controller.setHostName(hostNameField.getSelectionModel().getSelectedItem().getAddress());
                stage.setScene(new Scene(gameView, 800, 600));
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void editDeck(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/deckView.fxml"));
        Parent deckView = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Deck Editor");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/card.png")));
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/card_small.png")));
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/card_smallest.png")));

        stage.setScene(new Scene(deckView, 800, 600));
        stage.initOwner(this.stage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.show();
    }
}
