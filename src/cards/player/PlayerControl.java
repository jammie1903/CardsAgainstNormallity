package cards.player;

import cards.bean.Player;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class PlayerControl extends HBox {

    public Label nameLabel;
    public Label pointsLabel;
    public ImageView cardPlayedDisplay;

    public PlayerControl(Player player) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/playerView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        nameLabel.textProperty().bind(player.nameProperty());
        pointsLabel.textProperty().bind(player.pointsProperty().asString());
        cardPlayedDisplay.visibleProperty().bind(player.cardsChosenProperty());
        nameLabel.underlineProperty().bind(player.questionMasterProperty());
    }
}
