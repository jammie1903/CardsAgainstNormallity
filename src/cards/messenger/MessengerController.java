package cards.messenger;

import cards.communication.Connection;
import cards.communication.MessageHandler;
import cards.data.PlayerData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;

import java.net.URL;
import java.util.ResourceBundle;

public class MessengerController implements MessageHandler, Initializable {

    @FXML
    private ListView<Message> textPane;
    @FXML
    private ToolBar chatPaneHeader;
    @FXML
    private TextField input;

    private static class Message {
        private int senderId;
        private String text;

        public Message(int senderId, String text) {
            this.senderId = senderId;
            this.text = text;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Connection.addMessageHandler(this);
        textPane.setCellFactory((listView) -> {
            ListCell<Message> cell = new ListCell<Message>() {
                {
                    this.setWrapText(true);
                }

                @Override
                protected void updateItem(Message item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                    } else {
                        setText(PlayerData.get(item.senderId).getName() + ": " + item.text);
                    }
                }
            };
            cell.prefWidthProperty().bind(listView.widthProperty().subtract(19));
            return cell;
        });
        ChangeListener<Boolean> x = (observable, oldValue, newValue) -> {
            if (newValue) {
                chatPaneHeader.setStyle("");
            }
        };
        chatPaneHeader.focusedProperty().addListener(x);
        textPane.focusedProperty().addListener(x);
        input.focusedProperty().addListener(x);
    }

    public void sendMessage(ActionEvent actionEvent) {
        if (!input.getText().trim().isEmpty()) {
            textPane.getItems().add(new Message(Connection.get().getId(), input.getText()));
            textPane.scrollTo(textPane.getItems().size() - 1);
            Connection.get().sendMessage("MESSAGE:" + input.getText());
        }
        input.setText("");
    }


    @Override
    public void handleMessage(int clientId, String messageCategory, String message) {
        switch (messageCategory) {
            case "MESSAGE":
                Platform.runLater(() -> {
                    textPane.getItems().add(new Message(clientId, message));
                    textPane.scrollTo(textPane.getItems().size() - 1);
                    if (!textPane.isFocused() && !input.isFocused() && !chatPaneHeader.isFocused()) {
                        chatPaneHeader.setStyle("-fx-base: teal");
                    }
                });
                if (Connection.get().isHost()) {
                    Connection.get().forwardMessage(clientId, "MESSAGE:" + message);
                }
                break;
        }
    }


}
