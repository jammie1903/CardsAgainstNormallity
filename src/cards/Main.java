package cards;

import cards.data.DataHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DataHandler.getInstance();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/initialView.fxml"));
        Parent root = loader.load();
        InitialController controller = loader.getController();
        controller.setStage(primaryStage);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/card.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/card_small.png")));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/card_smallest.png")));


        scene.getStylesheets().add("/cards.css");
        primaryStage.setTitle("E-Cards Against Normality");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
