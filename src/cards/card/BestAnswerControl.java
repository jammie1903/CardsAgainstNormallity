package cards.card;

import cards.bean.Player;
import cards.communication.Connection;
import cards.game.GameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class BestAnswerControl extends HBox {

    private final Player player;
    public VBox cardHolder;
    @FXML
    private Button continueButton;
    @FXML
    private Label nameLabel;

    public BestAnswerControl(Player player, CardControl winningCard) {
        this.player = player;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/bestAnswerView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        nameLabel.textProperty().bind(player.nameProperty());
        cardHolder.getChildren().add(winningCard);
        continueButton.setVisible(player.getId() == Connection.get().getId());
    }

    public void continueGame(ActionEvent event) {
        GameController.getInstance().continueGame(player.getId());
    }
}
