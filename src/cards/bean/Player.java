package cards.bean;

import javafx.beans.property.*;

public class Player {
    private int id;
    private StringProperty name = new SimpleStringProperty();
    private IntegerProperty points = new SimpleIntegerProperty();
    private String[] cardsPlayed;
    private BooleanProperty cardsChosen = new SimpleBooleanProperty();
    private BooleanProperty questionMaster = new SimpleBooleanProperty();

    public Player(int id, String name) {
        this.id = id;
        this.name.set(name);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getPoints() {
        return points.get();
    }

    public void addPoint() {
        points.set(points.get() + 1);
    }

    public void setPoints(int points) {
        this.points.set(points);
    }

    public IntegerProperty pointsProperty() {
        return points;
    }

    public String[] getCardsPlayed() {
        return cardsPlayed;
    }

    public void setCardsPlayed(String[] cardsPlayed) {
        this.cardsPlayed = cardsPlayed;
        cardsChosen.set(cardsPlayed != null && cardsPlayed.length > 0);
    }

    public boolean getCardsChosen() {
        return cardsChosen.get();
    }

    public BooleanProperty cardsChosenProperty() {
        return cardsChosen;
    }

    public boolean    isQuestionMaster() {
        return questionMaster.get();
    }

    public void setQuestionMaster(boolean questionMaster) {
        this.questionMaster.set(questionMaster);
    }

    public BooleanProperty questionMasterProperty() {
        return questionMaster;
    }
}
