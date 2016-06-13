package cards.data;

import cards.bean.GameData;
import cards.bean.HostDetails;
import com.thoughtworks.xstream.XStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DataHandler {

    private static final String XML_FILE = "cards_data.xml";
    private GameData gameData = null;
    private XStream xstream = getxStream();

    private DataHandler() {

        try (InputStream eventStream = Files.newInputStream(Paths.get(XML_FILE))) {
            if (eventStream == null) {
                gameData = new GameData();
            } else {
                this.gameData = (GameData) xstream.fromXML(eventStream);
            }
        } catch (IOException e) {
            gameData = new GameData();
        }
    }

    private XStream getxStream() {
        XStream xstream = new XStream();
        xstream.processAnnotations(GameData.class);
        return xstream;
    }

    private static DataHandler instance = new DataHandler();

    public static DataHandler getInstance() {
        return instance;
    }

    public List<HostDetails> getHosts() {
        return gameData.getHosts();
    }

    public String getName() {
        return gameData.getPlayerName();
    }

    public void setName(String name) {
        gameData.setPlayerName(name);
        updateDataFile();
    }

    public void addQuestionCard(String questionCard) {
        if (!gameData.getQuestions().contains(questionCard)) {
            gameData.getQuestions().add(questionCard);
            updateDataFile();
        }
    }

    public void addAnswerCard(String answerCard) {
        if (!gameData.getAnswers().contains(answerCard)) {
            gameData.getAnswers().add(answerCard);

            updateDataFile();
        }
    }

    public void addOrUpdateHost(String hostName, String hostAddress) {
        HostDetails hostDetails = new HostDetails(hostName, hostAddress);
        if (gameData.getHosts().contains(hostDetails)) {
            gameData.getHosts().remove(hostDetails);
        }
        gameData.getHosts().add(hostDetails);
        updateDataFile();
    }

    private void updateDataFile() {
        try {
            Files.write(Paths.get(XML_FILE), xstream.toXML(gameData).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HostDetails getHostByAddress(String address) {
        HostDetails hostDetails = new HostDetails("", address);
        if (gameData.getHosts().contains(hostDetails)) {
            return gameData.getHosts().get(gameData.getHosts().indexOf(hostDetails));
        }
        return hostDetails;
    }

    public List<String> getQuestions() {
        return gameData.getQuestions();
    }

    public List<String> getAnswers() {
        return gameData.getAnswers();
    }

    public void editQuestionCard(String currentText, String newText) {
        int index = gameData.getQuestions().indexOf(currentText);
        gameData.getQuestions().remove(index);
        gameData.getQuestions().add(index, newText);
        updateDataFile();
    }

    public void editAnswerCard(String currentText, String newText) {
        int index = gameData.getAnswers().indexOf(currentText);
        gameData.getAnswers().remove(index);
        gameData.getAnswers().add(index, newText);
        updateDataFile();
    }

    public void deleteQuestionCard(String cardText) {
        gameData.getQuestions().remove(cardText);
        updateDataFile();
    }

    public void deleteAnswerCard(String cardText) {
        gameData.getAnswers().remove(cardText);
        updateDataFile();
    }
}
