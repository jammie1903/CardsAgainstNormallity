package cards.communication;

import java.util.function.Supplier;

public interface Communicator {
    void sendMessage(String message);
    void sendMessage(String messageType, Supplier<String> message);
    void sendMessageTo(int clientId, String message);
    void forwardMessage(int clientId, String message);
    boolean isHost();
    int getId();
}
