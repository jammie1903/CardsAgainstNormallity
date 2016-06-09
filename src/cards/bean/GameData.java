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
        return questions;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<HostDetails> getHosts() {
        return hosts;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
