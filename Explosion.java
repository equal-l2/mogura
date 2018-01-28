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

public class Explosion extends ImageView { // 爆発表示クラス
  static final private Image[] expImages; // 爆発動画のフレーム
  static final private AudioClip expSound; // 爆発音
  private final Timeline tl; // 動画のフレーム切り替え用
  private double rate; // 再生速度

  static {
    // 音声と各フレームは事前に読み込む
    expSound = new AudioClip(Paths.get("assets/sounds/exp.wav").toUri().toString());
    expImages = IntStream
      .rangeClosed(1,108)
      .mapToObj(i -> String.format("assets/exp/%03d.png",i))
      .map(Image::new)
      .toArray(Image[]::new);
  }

  public Explosion() {
    tl = new Timeline();
    rate = 1.0;
  }

  public void play() {
      expSound.setRate(rate); // 音声再生速度を設定

      /* 爆発動画を設定 */
      ObservableList<KeyFrame> kf = tl.getKeyFrames();
      kf.clear();
      final double frameRate = 24*rate; // フレームレート
      Duration frameTime = Duration.ZERO;
      final Duration frameGap = Duration.millis(1000/frameRate);
      for(final Image i : expImages) {
        kf.add(new KeyFrame(
              frameTime,
              ActionEvent -> setImage(i)
              ));
        frameTime = frameTime.add(frameGap);
      }

      /* 再生 */
      expSound.play();
      tl.play();
  }

  public void setOnFinished(EventHandler<ActionEvent> e) {
    tl.setOnFinished(e);
  }

  public void setRate(double rate) { // 再生速度を設定
    this.rate = rate;
  }
}
