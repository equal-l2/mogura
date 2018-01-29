import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class CountDownTimer {
  private static final long tick = 100; // 1tickあたりのミリ秒
  private SimpleLongProperty millis = new SimpleLongProperty(); // 残り秒数
  private Runnable onTimeUp;
  private Runnable onTick;

  private final Timeline timer = new Timeline( // タイマー用
    new KeyFrame(Duration.millis(tick), (ActionEvent) -> {
      long m = millis.getValue();
      if(m <= 0) {
        onTimeUpImpl();
      } else {
        millis.set(m-tick);
        if(onTick != null) onTick.run();
      }
    })
  );

  CountDownTimer(Duration d) {
    setTime(d);
    timer.setCycleCount(Animation.INDEFINITE);
  }

  public void setTime(Duration d) {
    millis.set((long) d.toMillis());
  }

  public Duration getTime() {
    return Duration.millis(millis.getValue());
  }

  public LongProperty millisProperty() {
    return millis;
  }

  public void setOnTimeUp(Runnable r) {
    onTimeUp = r;
  }

  public void setOnTick(Runnable r) {
    onTick = r;
  }

  public void play() {
    timer.play();
  }

  public void stop() {
    timer.stop();
  }

  private void onTimeUpImpl() {
    timer.stop();
    if(onTimeUp != null) onTimeUp.run();
  }
}
