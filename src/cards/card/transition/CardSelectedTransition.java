package cards.card.transition;

import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class CardSelectedTransition extends Transition {

        private Node card;

        public CardSelectedTransition(Node card) {
            setCycleDuration(Duration.seconds(0.5));
            this.card = card;
        }

        @Override
        protected void interpolate(double fraction) {
            Insets currentMargin = HBox.getMargin(card);
            HBox.setMargin(card, new Insets(0, currentMargin == null ? 0 : currentMargin.getRight(), 20.0 * fraction, 0));
        }
    }