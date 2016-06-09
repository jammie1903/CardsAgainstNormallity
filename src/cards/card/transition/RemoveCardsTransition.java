package cards.card.transition;

import cards.card.HandControl;
import javafx.animation.Transition;
import javafx.util.Duration;

public class RemoveCardsTransition extends Transition {


    private final HandControl hand;

    public RemoveCardsTransition(HandControl hand) {
        setCycleDuration(Duration.seconds(1));
        this.hand = hand;
        this.setOnFinished((event) -> {
            hand.getChildren().removeAll(hand.getSelectedCards());
            hand.getSelectedCards().clear();
        });
    }

    @Override
    protected void interpolate(double fraction) {
        hand.getSelectedCards().forEach((card) -> card.setOpacity(1.0 - fraction));
    }
}