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
  final private Timeline tl;
  static final private Image[] expImages;
  static final private AudioClip expSound;

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
    ObservableList<KeyFrame> kf = tl.getKeyFrames();
    final int frameRate = 24;
    Duration frameTime = Duration.ZERO;
    final Duration frameGap = Duration.millis(1000/frameRate);
    for(final Image i : expImages) {
      kf.add(new KeyFrame(
            frameTime,
            ActionEvent -> setImage(i)
            ));
      frameTime = frameTime.add(frameGap);
    }
  }

  void play() {
      expSound.play();
      tl.play();
  }

  void setOnFinished(EventHandler<ActionEvent> e) {
    tl.setOnFinished(e);
  }
}
