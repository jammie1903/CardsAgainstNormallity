package cards.card;

import cards.card.transition.CardMarginTransition;
import cards.card.transition.RemoveCardsTransition;
import javafx.animation.Animation;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HandControl extends HBox {
    private static final double CARD_WIDTH = 135.0;

    private IntegerProperty cardCount = new SimpleIntegerProperty(0);
    private Map<Node, Animation> animationMap = new HashMap<>();
    private int cardSelectionAmount;
    private List<CardControl> selectedCards = new ArrayList<>();
    private BooleanProperty allCardsSelected = new SimpleBooleanProperty(false);

    public HandControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/handView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        cardCount.set(this.getChildren().size());

        Consumer<Node> hoverAlterer = (card) -> {
            card.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (HandControl.this.getChildren().indexOf(card) != HandControl.this.getChildren().size() - 1) {
                    animationMap.get(card).setRate(newValue ? 1 : -1);
                    animationMap.get(card).play();
                }
            });
            card.setOnMouseClicked((event) -> {
                if (((CardControl) card).isSelected()) {
                    ((CardControl) card).setSelected(false);
                    ((CardControl) card).setSelectionOrder(0);
                    selectedCards.remove(card);
                    for (int c = 0; c < selectedCards.size(); c++) {
                        selectedCards.get(c).setSelectionOrder(c + 1);
                    }
                } else if (cardSelectionAmount == 1) {
                    ((CardControl) card).setSelected(true);
                    if (selectedCards.size() > 0) {
                        selectedCards.get(0).setSelected(false);
                        selectedCards.clear();
                    }
                    selectedCards.add((CardControl) card);
                } else if (cardSelectionAmount > selectedCards.size()) {
                    selectedCards.add((CardControl) card);
                    ((CardControl) card).setSelected(true);
                    ((CardControl) card).setSelectionOrder(selectedCards.size());
                }
                allCardsSelected.set(selectedCards.size() > 0 && selectedCards.size() == cardSelectionAmount);
            });
        };


        this.getChildren().addListener((ListChangeListener<Node>) c -> {
            cardCount.set(c.getList().size());
            while (c.next()) {
                if (c.getAddedSize() > 0) {
                    c.getAddedSubList().forEach(hoverAlterer);
                    c.getAddedSubList().forEach((card) -> animationMap.put(card, new CardMarginTransition(card, spacingProperty())));
                }
            }
        });
        this.getChildren().forEach(hoverAlterer);
        this.getChildren().forEach((card) -> animationMap.put(card, new CardMarginTransition(card, spacingProperty())));

        spacingProperty().bind(Bindings.min((this.widthProperty().subtract(CARD_WIDTH * 1.5)).divide(cardCount.subtract(1)).subtract(CARD_WIDTH), 100.0));
    }

    public List<CardControl> getSelectedCards() {
        return selectedCards;
    }

    public void setCardSelectionAmount(int cardSelectionAmount) {
        this.cardSelectionAmount = cardSelectionAmount;
    }

    public void removeSelectedCards() {
        RemoveCardsTransition removeCards = new RemoveCardsTransition(this);
        removeCards.play();
    }

    public BooleanProperty allCardsSelectedProperty() {
        return allCardsSelected;
    }
}
