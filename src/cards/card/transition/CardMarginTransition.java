package cards.card.transition;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class CardMarginTransition extends Transition {

    private final DoubleProperty spacingProperty;
   // private final boolean clearMargin;
    private Node card;
    
    public CardMarginTransition(Node card, DoubleProperty spacingProperty ) {
        setCycleDuration(Duration.seconds(0.5));
        this.card = card;
        this.spacingProperty = spacingProperty;
       // this.clearMargin = clearMargin;
    }
    
    @Override
    protected void interpolate(double fraction) {
        Insets currentMargin = HBox.getMargin(card);
        HBox.setMargin(card, new Insets(0, Math.max(0.0, -spacingProperty.get()*fraction), currentMargin == null ? 0 : currentMargin.getBottom(), 0));
    }
}