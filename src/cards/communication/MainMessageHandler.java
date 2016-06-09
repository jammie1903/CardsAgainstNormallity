package cards.communication;

public interface MainMessageHandler extends MessageHandler {
    void sendSessionInformation(int clientId);
}
