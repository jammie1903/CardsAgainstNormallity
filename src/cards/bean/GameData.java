package cards.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("gamedata")
public class GameData {
    private String playerName;

    @XStreamImplicit(itemFieldName = "host")
    private List<HostDetails> hosts = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "question")
    private List<String> questions = new ArrayList<>();

    @XStreamImplicit(itemFieldName = "answer")
    private List<String> answers = new ArrayList<>();

    public String getPlayerName() {
        return playerName;
    }

    public List<String> getQuestions() {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public List<String> getAnswers() {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        return answers;
    }

    public List<HostDetails> getHosts() {if (hosts == null) {
        hosts = new ArrayList<>();
    }
        return hosts;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
