package cards.card.deck;

import cards.card.CardControl;
import cards.data.DataHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class DeckController implements Initializable {
    @FXML
    private GridView<String> questionsPane;
    @FXML
    private GridView<String> answersPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        questionsPane.setItems(FXCollections.observableArrayList(DataHandler.getInstance().getQuestions()));
        answersPane.setItems(FXCollections.observableArrayList(DataHandler.getInstance().getAnswers()));

        questionsPane.setCellFactory((view) -> new GridCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setGraphic(new CardControl(item, true));
                    setContextMenu(new CardContextMenu(item, true));
                }
            }
        });

        answersPane.setCellFactory((view) -> new GridCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    setGraphic(new CardControl(item, false));
                    setContextMenu(new CardContextMenu(item, false));
                }
            }
        });
    }

    public void addQuestion(ActionEvent actionEvent) {
        showNewCardDialog(true);
    }

    public void addAnswer(ActionEvent actionEvent) {
        showNewCardDialog(false);
    }

    private void showNewCardDialog(boolean question) {
        Optional<String> result = showCardDialog(question, null);
        if (result.isPresent() && !result.get().isEmpty()) {
            if (question) {
                DataHandler.getInstance().addQuestionCard(result.get());
                questionsPane.getItems().add(result.get());
            } else {
                DataHandler.getInstance().addAnswerCard(result.get());
                answersPane.getItems().add(result.get());
            }
        }
    }

    private void showEditCardDialog(String currentText, boolean question) {
        Optional<String> result = showCardDialog(question, currentText);
        if (result.isPresent() && !result.get().isEmpty()) {
            if (question) {
                DataHandler.getInstance().editQuestionCard(currentText, result.get());
                int index = questionsPane.getItems().indexOf(currentText);
                questionsPane.getItems().remove(index);
                questionsPane.getItems().add(index, result.get());

            } else {
                DataHandler.getInstance().editAnswerCard(currentText, result.get());
                int index = answersPane.getItems().indexOf(currentText);
                answersPane.getItems().remove(index);
                answersPane.getItems().add(index, result.get());
            }
        }
    }

    private Optional<String> showCardDialog(boolean question, String editItem) {
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("New Card");
        dialog.setHeaderText((editItem == null ? "Create your new " : "Edit your ") + (question ? "question" : "answer") + " card:");

        TextArea textArea = new TextArea(editItem == null ? "" : editItem);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPadding(new Insets(0));
        HBox wrapper = new HBox();
        wrapper.setPadding(new Insets(10));
        wrapper.getChildren().add(textArea);
        dialog.getDialogPane().setContent(wrapper);

        Platform.runLater(textArea::requestFocus);

        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(
                textArea.textProperty().length().isEqualTo(0));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return textArea.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private class CardContextMenu extends ContextMenu {
        private CardContextMenu(String cardText, boolean questionCard) {
            MenuItem editItem = new MenuItem("Edit");
            editItem.setOnAction((event) -> showEditCardDialog(cardText, questionCard));
            this.getItems().add(editItem);

            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction((event) -> {
                if (questionCard) {
                    questionsPane.getItems().remove(cardText);
                    DataHandler.getInstance().deleteQuestionCard(cardText);
                } else {
                    answersPane.getItems().remove(cardText);
                    DataHandler.getInstance().deleteAnswerCard(cardText);
                }
            });
            this.getItems().add(deleteItem);
        }
    }
}
