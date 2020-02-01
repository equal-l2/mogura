import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public class Explosion extends javafx.scene.image.ImageView { // 爆発表示クラス
  // 動画を透過表示する方法が見つからないので
  // 各フレームを画像として保持・切り替えることで動画を実現する
  // CPU負荷が大きいのが悩みどころ

  // 爆発動画のフレーム
  static final private Image[] expImages = java.util.stream.IntStream
    .rangeClosed(1,56)
    .mapToObj(i ->
      Launcher.getResourceUri(String.format("exp/%02d.png",i))
    )
    .map(Image::new)
    .toArray(Image[]::new);

  static final private AudioClip expSound = new AudioClip( // 爆発音
    Launcher.getResourceUri("sounds/exp.wav")
  );

  private final Timeline tl = new Timeline(); // 動画のフレーム切り替え用
  private double rate = 1.0; // 再生速度

  public void play() {
      expSound.setRate(rate); // 音声再生速度を設定

      /* 爆発動画を設定 */
      java.util.List<KeyFrame> kf = tl.getKeyFrames();
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
