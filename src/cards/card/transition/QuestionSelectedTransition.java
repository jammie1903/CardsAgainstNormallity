package cards.card.transition;

import javafx.animation.Transition;
import javafx.scene.effect.Glow;
import javafx.util.Duration;

public class QuestionSelectedTransition extends Transition {

    private Glow glow;

    public QuestionSelectedTransition(Glow glow) {
        setCycleDuration(Duration.seconds(0.5));
        this.glow = glow;
    }

    @Override
    protected void interpolate(double fraction) {
        glow.setLevel(0.5 * fraction);
    }
}