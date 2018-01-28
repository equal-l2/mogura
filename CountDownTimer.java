import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CountDownTimer {
  private SimpleIntegerProperty seconds = new SimpleIntegerProperty(); // 残り秒数
  private Runnable onTimeUp;
  private final Timeline timer = new Timeline( // タイマー用
    new KeyFrame(Duration.seconds(1), (ActionEvent) -> {
      int sec = seconds.getValue();
      if(sec <= 0) {
        onTimeUpImpl();
      } else {
        seconds.set(sec-1);
      }
    })
  );

  public Label label;

  CountDownTimer(Duration d) {
    setTime(d);
    timer.setCycleCount(Animation.INDEFINITE);
  }

  public void setTime(Duration d) {
    seconds.set((int) d.toSeconds());
  }

  public Duration getTime() {
    return Duration.seconds(seconds.getValue());
  }

  public StringBinding getBinding() {
    return Bindings.createStringBinding(
      () -> {
        int sec = seconds.getValue();
        return String.format("%02d:%02d", sec/60, sec%60);
      },
      seconds
    );
  }

  public void setOnTimeUp(Runnable r) {
    onTimeUp = r;
  }

  public void play() {
    timer.play();
  }

  private void onTimeUpImpl() {
    timer.stop();
    if(onTimeUp != null) onTimeUp.run();
  }
}
