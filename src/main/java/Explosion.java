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
  // 動画を透過表示する方法が見つからないので
  // 各フレームを画像として保持・切り替えることで動画を実現する
  // CPU負荷が大きいのが悩みどころ

  static final private Image[] expImages = IntStream // 爆発動画のフレーム
    .rangeClosed(1,56)
    .mapToObj(i -> Launcher.toUriString(String.format("assets/exp/%02d.png",i)))
    .map(Image::new)
    .toArray(Image[]::new);

  static final private AudioClip expSound = new AudioClip( // 爆発音
    Launcher.toUriString("assets/sounds/exp.wav")
  );

  private final Timeline tl = new Timeline(); // 動画のフレーム切り替え用
  private double rate = 1.0; // 再生速度

  public void play() {
      expSound.setRate(rate); // 音声再生速度を設定

      /* 爆発動画を設定 */
      ObservableList<KeyFrame> kf = tl.getKeyFrames();
      kf.clear();
      final double frameRate = 12*2*rate; // フレームレート
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
