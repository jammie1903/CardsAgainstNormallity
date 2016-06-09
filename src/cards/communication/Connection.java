package cards.communication;

import cards.bean.Player;
import cards.data.CardData;
import cards.data.DataHandler;
import cards.data.PlayerData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Connection implements MainMessageHandler {

    private static Connection instance = new Connection();
    private Communicator communicator;
    private List<MessageHandler> handlers = new ArrayList<>();
    private String connectedAddress;

    public static String initialiseAsHost() {
        try {
            String machineName = InetAddress.getLocalHost().getHostName();
            instance.communicator = new Host(instance);
            PlayerData.add(0, DataHandler.getInstance().getName());
            return machineName;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    public static boolean initialiseAsClient(String hostName) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            instance.communicator = new Client(address, instance);
            instance.connectedAddress = hostName;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static Communicator get() {
        return instance.communicator;
    }

    public static void addMessageHandler(MessageHandler handler) {
        instance.handlers.add(handler);
    }

    @Override
    public void handleMessage(int clientId, String messageCategory, String message) {
        switch (messageCategory) {
            case "ACCEPTED":
                int id = Connection.get().getId();
                Connection.get().sendMessage("NAME:" + DataHandler.getInstance().getName());
                PlayerData.add(id, DataHandler.getInstance().getName());
                Connection.get().sendMessage("QUESTIONDECK:" +
                        DataHandler.getInstance().getQuestions().stream().collect(Collectors.joining("|")));
                Connection.get().sendMessage("ANSWERDECK:" +
                        DataHandler.getInstance().getAnswers().stream().collect(Collectors.joining("|")));
                break;
            case "NAME":
                PlayerData.add(clientId, message);
                if (Connection.get().isHost()) {
                    Connection.get().forwardMessage(clientId, "NAME:" + message);
                } else if (clientId == 0) {
                    DataHandler.getInstance().addOrUpdateHost(message, connectedAddress);
                }
                break;
            case "POINTS":
                PlayerData.get(clientId).setPoints(Integer.valueOf(message));
                break;
            case "QUESTIONDECK":
                CardData.loadQuestionDeck(Arrays.asList(message.split("\\|")));
                break;
            case "ANSWERDECK":
                CardData.loadAnswerDeck(Arrays.asList(message.split("\\|")));
                break;
        }
        handlers.forEach((handler) -> handler.handleMessage(clientId, messageCategory, message));
    }

    @Override
    public void sendSessionInformation(int clientId) {
        for (Player player : PlayerData.getAll()) {
            Connection.get().sendMessageTo(clientId, "CLIENT" + player.getId() + ":NAME:" + player.getName());
            Connection.get().sendMessageTo(clientId, "CLIENT" + player.getId() + ":POINTS:" + player.getPoints());
        }
    }
}
