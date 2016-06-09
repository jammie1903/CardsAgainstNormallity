package cards.communication;

public interface MessageHandler {
    void handleMessage(int clientId, String messageCategory, String message);
}
