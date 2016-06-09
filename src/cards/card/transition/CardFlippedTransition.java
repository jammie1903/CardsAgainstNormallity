package cards.card.transition;

import cards.card.CardControl;
import javafx.animation.Transition;
import javafx.util.Duration;

public class CardFlippedTransition extends Transition {

    private CardControl card;

    public CardFlippedTransition(CardControl card) {
        setCycleDuration(Duration.seconds(0.65));
        this.card = card;
    }

    @Override
    protected void interpolate(double fraction) {
        card.setRotate((1-fraction) * 180.0);
    }
}