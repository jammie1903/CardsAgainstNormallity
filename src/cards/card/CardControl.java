package cards.card;

import cards.card.transition.AnswerSelectedTransition;
import cards.card.transition.CardFlippedTransition;
import cards.card.transition.QuestionSelectedTransition;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CardControl extends StackPane {

    private boolean questionCard;
    public Label selectionOrderLabel;
    private IntegerProperty selectionOrder = new SimpleIntegerProperty();
    @FXML
    private TextFlow cardText;
    private AnswerSelectedTransition answerSelectedTransition = new AnswerSelectedTransition(this);
    private QuestionSelectedTransition questionSelectedTransition;
    private CardFlippedTransition flippedTransition = new CardFlippedTransition(this);
    private int playerId;
    private DoubleProperty maxTextHeight = new SimpleDoubleProperty(-1);
    private static final int START_FONT_WIDTH = 15;
    private int fontSize = START_FONT_WIDTH;

    public int getSelectionOrder() {
        return selectionOrder.get();
    }

    public void setSelectionOrder(int selectionOrder) {
        this.selectionOrder.set(selectionOrder);
    }

    public IntegerProperty selectionOrderProperty() {
        return selectionOrder;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    private BooleanProperty selected =
            new BooleanPropertyBase(false) {

                @Override
                protected void invalidated() {
                    pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, get());
                    if (questionCard) {
                        questionSelectedTransition.setRate(get() ? 1 : -1);
                        questionSelectedTransition.play();
                    } else {
                        answerSelectedTransition.setRate(get() ? 1 : -1);
                        answerSelectedTransition.play();
                    }
                }

                @Override
                public Object getBean() {
                    return CardControl.this;
                }

                @Override
                public String getName() {
                    return "selected";
                }
            };

    private static final PseudoClass
            SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    public CardControl() {
        load();
        this.setStyle(this.getStyle() + " -fx-background-color: teal;");
    }

    public CardControl(String cardText, boolean questionCard) {
        load();
        this.questionCard = questionCard;
        setText(cardText);
        this.setStyle(this.getStyle() + " -fx-background-color: " + (questionCard ? "orange;" : "teal;"));
    }

    public CardControl(String cardText, String[] answerSegments) {
        load();
        this.questionCard = true;
        setText(cardText, answerSegments);
        this.setStyle(this.getStyle() + " -fx-background-color: orange;");
    }

    private void load() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/cardView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        getStyleClass().add("card");
        this.setRotationAxis(new Point3D(0, 1, 0));
        this.cardText.visibleProperty().bind(rotateProperty().lessThan(90.0));
        selectionOrderLabel.textProperty().bind(selectionOrder.asString());
        selectionOrderLabel.visibleProperty().bind(selected.and(selectionOrder.greaterThan(0)));
        maxTextHeight.bind(minHeightProperty().subtract(this.getPadding().getTop() + this.getPadding().getBottom() + START_FONT_WIDTH));

        ChangeListener<Number> x = (observable, oldValue, newValue) -> {
            if (maxTextHeight.get() > 0 && newValue.doubleValue() > maxTextHeight.get()) {
                fontSize--;
                this.cardText.getChildren().stream().filter((c) -> c instanceof Text).map((c) -> (Text) c).forEach(
                        (text) -> text.setFont(getFont()));
            }
        };
        this.cardText.heightProperty().addListener(x);

        Glow glow = new Glow(0.0);
        setEffect(glow);
        questionSelectedTransition = new QuestionSelectedTransition(glow);
    }

    public String getText() {
        return this.cardText.getChildren().stream().map(c -> (Text) c).map(Text::getText).collect(Collectors.joining(" "));
    }

    public void setText(String cardText) {
        Text text = new Text(cardText);
        text.setFont(getFont());
        text.setFill(Color.WHITE);
        this.cardText.getChildren().setAll(text);
    }

    public void setText(String cardText, String[] answers) {
        String[] textSegments = cardText.split("_+");
        this.cardText.getChildren().clear();

        for (int c = 0; c < textSegments.length || c < answers.length; c++) {
            if (c < textSegments.length) {
                Text text = new Text(textSegments[c]);
                text.setFont(getFont());
                text.setFill(Color.WHITE);
                this.cardText.getChildren().add(text);
            }

            if (c < answers.length) {
                Text text = new Text((textSegments.length == 1 ? "\n" : "") + (c < answers.length ? answers[c] : "???"));
                text.setFont(getFont());
                text.setFill(Color.WHITE);
                text.setUnderline(true);
                this.cardText.getChildren().add(text);
            }
        }
    }

    private Font getFont() {
        return Font.font("System", FontWeight.BOLD, fontSize);
    }


    public void setFaceDown() {
        setRotate(180.0);
    }

    public void flip() {
        flippedTransition.setRate(getRotate() < 90 ? -1 : 1);
        flippedTransition.play();
    }

    public int getRequiredAnswerCardCount() {
        Matcher matcher = Pattern.compile("_+").matcher(getText());
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return Math.max(1, count);
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
