package cards.data;

import cards.bean.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    private Map<Integer, Player> players = new HashMap<>();
    private ObservableList<Player> playerList = FXCollections.observableArrayList();
    private String name;
    private int questionMasterId = -1;

    private static PlayerData instance = new PlayerData();
    private PlayerData(){}

    public static Player get(int id){
        return instance.players.get(id);
    }

    public static void add(int id, String name) {
        Player player = new Player(id, name);
        instance.players.put(id, player);
        Platform.runLater(()-> {
            instance.playerList.add(player);
        });
    }

    public static ObservableList<Player> getAll() {
        return instance.playerList;
    }

    public static void setQuestionMaster(int clientId) {
        if(instance.questionMasterId > -1) {
            get(instance.questionMasterId).setQuestionMaster(false);
        }
        instance.questionMasterId = clientId;
        get(clientId).setQuestionMaster(true);
    }

    public static boolean isQuestionMaster(int clientId) {
        return instance.questionMasterId == clientId;
    }

    public static boolean allCardsPlayed() {
        for (Player player : instance.playerList) {
            if(!player.isQuestionMaster()&& !player.cardsChosenProperty().get()) {
                return false;
            }
        }
        return true;
    }

    public static void clearCardsPlayed() {
        for (Player player : instance.playerList) {
            player.setCardsPlayed(null);
        }
    }
}
