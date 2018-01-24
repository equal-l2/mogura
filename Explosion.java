import java.nio.file.Paths;
import java.util.stream.IntStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

class Explosion extends ImageView {
  static final private Image[] expImages;
  static final private AudioClip expSound;
  private final Timeline tl;
  private double rate;

  static {
    expSound = new AudioClip(Paths.get("exp.wav").toUri().toString());
    expImages = IntStream
      .rangeClosed(1,108)
      .mapToObj(i -> String.format("exp/%03d.png",i))
      .map(Image::new)
      .toArray(Image[]::new);
  }

  Explosion() {
    tl = new Timeline();
    rate = 1.0;
  }

  void play() {
      expSound.setRate(rate);
      ObservableList<KeyFrame> kf = tl.getKeyFrames();
      kf.clear();
      final double frameRate = 24*rate;
      Duration frameTime = Duration.ZERO;
      final Duration frameGap = Duration.millis(1000/frameRate);
      for(final Image i : expImages) {
        kf.add(new KeyFrame(
              frameTime,
              ActionEvent -> setImage(i)
              ));
        frameTime = frameTime.add(frameGap);
      }
      expSound.play();
      tl.play();
  }

  void setOnFinished(EventHandler<ActionEvent> e) {
    tl.setOnFinished(e);
  }

  void setRate(double rate) {
    this.rate = rate;
  }
}
