import javafx.animation.Transition;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeInOut {
  private SequentialTransition t;

  public FadeInOut(Duration dIn, Duration dPause, Duration dOut, Node n) {
    FadeTransition in = new FadeTransition(dIn, n);
    in.setFromValue(0.0);
    in.setToValue(1.0);

    PauseTransition p = new PauseTransition(dPause);

    FadeTransition out = new FadeTransition(dOut, n);
    out.setFromValue(1.0);
    out.setToValue(0.0);

    t = new SequentialTransition(in,p,out);
  }

  public void setOnFinished(EventHandler<ActionEvent> e) {
    t.setOnFinished(e);
  }

  public void play() {
    t.play();
  }

  public Transition toTransition() {
    return t;
  }
}
