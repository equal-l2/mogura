import java.util.Arrays;
import java.util.LinkedList;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MainController {
  @FXML
  private Text scoreText; // スコア表示
  @FXML
  private Pane field; // 敵表示用ペイン（「フィールド」）
  @FXML
  private Text timerText; // タイマー表示
  @FXML
  private ProgressBar spBar; // スペシャルタイムの残り時間を示す

  private static final Duration defaultSpawnRate = Duration.millis(1000); // デフォルトのスポーン間隔
  private static final Duration specialSpawnRate = Duration.millis(500); // スペシャルタイムのスポーン間隔
  private static final Duration playTime = Duration.seconds(60); // プレイ時間

  private boolean stateSpecial = false; // スペシャル状態かを示す

  private final Timeline spawner = new Timeline(); // 敵表示用

  // スペシャル状態用音楽
  private final MediaPlayer spMusic = new MediaPlayer(new Media(
    getClass().getResource("assets/sounds/special.wav").toString()
  ));

  // 過去にスポーンした敵を記録するキュー
  private final LinkedList<Enemy> enemyHistory = new LinkedList<>();

  // 触れてはいけない敵をクリックしたときの音
  private final AudioClip donttouchSound = new AudioClip(
    getClass().getResource("assets/sounds/bubbu.wav").toString()
  );

  private final Image cross = new Image("assets/cross.png"); // 触れてはいけない敵をクリックした時の画像

  private final Timeline playTimer = new Timeline(); // プレイ時間タイマ
  private final Timeline spTimer = new Timeline(); // spBarの長さを変化させる

  private final SimpleIntegerProperty seconds = new SimpleIntegerProperty(); // 残りプレイ時間
  private final SimpleIntegerProperty score = new SimpleIntegerProperty(0); // スコア

  @FXML
  private void initialize() {
    // TODO: 敵スポーンを別クラスに切り出す?
    /* 各種設定 */
    spMusic.setCycleCount(1); // 音楽のループ回数
    spMusic.setVolume(0.2); // 音楽の音量
    spawner.setCycleCount(Timeline.INDEFINITE); // スポナーは繰り返し動作させる
    setSpawnTime(defaultSpawnRate); // スポーン間隔をデフォルトに設定

    /* スコア表示設定 */
    scoreText.textProperty().bind(Bindings.createStringBinding(
      () -> String.format("Score: %d",score.getValue()),
      score
    ));

    /* プレイ時間タイマの設定 */
    playTimer.setOnFinished((ActionEvent) -> proceedToResult());
    playTimer.getKeyFrames().setAll(
        new KeyFrame(Duration.ZERO, new KeyValue(seconds, (int) playTime.toSeconds())),
        new KeyFrame(playTime, new KeyValue(seconds, 0))
    );

    timerText.textProperty().bind(Bindings.createStringBinding(
      () -> String.format("%02d:%02d", seconds.get()/60, seconds.get()%60),
      seconds
    ));

    /* spTimerの設定 */
    spTimer.setOnFinished((ActionEvent) -> disableSpecial());

    // spMusicがREADYになったらspTimerを設定してもらう
    // READYになる前はgetTotalDurationはUNKNOWNを返す
    spMusic.setOnReady(() ->
      spTimer.getKeyFrames().setAll(
        new KeyFrame(Duration.ZERO, new KeyValue(spBar.progressProperty(), 1)),
        new KeyFrame(spMusic.getTotalDuration(), new KeyValue(spBar.progressProperty(),0))
      )
    );

    /* Timeline開始 */
    playTimer.play();
    spawner.play();
  }

  private void proceedToResult() { // リザルト画面へ進む
    // タイムライン等は止めないと止まらないので止める
    spawner.stop();
    playTimer.stop();
    spTimer.stop();
    spMusic.stop();

    // リザルト画面をロード
    FXMLLoader loader = Launcher.getFXMLLoader("assets/fxml/Result.fxml");
    try {
      Scene s = new Scene(loader.load());
      ResultController c = loader.getController();
      c.setScore(score.getValue()); // スコアをResultControllerに渡しておく
      Launcher.setScene(s);
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }

  private EnemyView[] getEnemyViewsOnField() {
    // フィールド上の敵を配列に入れて返す
    return field.getChildren()
      .filtered((node) -> {
        return node instanceof EnemyView;
      })
      .stream()
      .map((e) -> (EnemyView)e)
      .toArray(EnemyView[]::new);
  }

  private void enableSpecial() { // スペシャル状態に入る
    stateSpecial = true;

    /* フィールド上の敵を全爆破 */
    EnemyView[] eList = getEnemyViewsOnField();
    for(EnemyView e : eList) {
      if (!e.isUnderDestruction()) {
        destroyEnemy(e);
      }
    }

    setSpawnTime(specialSpawnRate); // スポーン間隔を早める

    spBar.setVisible(true); // spBarを表示する
    spTimer.play();
    spMusic.play();
  }

  private void disableSpecial() { // スペシャル状態を抜ける
    stateSpecial = false;

    /* + Ritual + */
    spMusic.pause();
    spMusic.seek(Duration.ZERO);
    /* これがないとたまに音楽が再生されない */

    spMusic.stop(); // 停止しないと再生状態がリセットされない
    setSpawnTime(defaultSpawnRate);
    spBar.setVisible(false);
  }

  private void setSpawnTime(Duration d) { // スポーン間隔を変更する
    spawner.stop();
    spawner.getKeyFrames().setAll(new KeyFrame(d, (ActionEvent) -> spawnEnemy()));
    spawner.play();
  }

  private void addScore(int i) {
    score.set(score.getValue() + i);
  }

  private void destroyEnemy(EnemyView e) { // 敵破壊時の処理
    Explosion expl = new Explosion();
    destroyCommons(e, expl, e.enemy.score);
    expl.setOnFinished((ActionEvent) -> {
      field.getChildren().remove(expl);
      field.getChildren().remove(e);
    });
    expl.play();
  }

  private void destroySpecial(EnemyView e) { // スペシャル敵破壊時の処理
    Explosion expl = new Explosion();
    expl.setRate(2.0); // 2倍速にする
    ColorAdjust c = new ColorAdjust();
    c.setHue(-0.5); // 色相を調整(赤->紫)
    expl.setEffect(c);
    destroyCommons(e, expl, e.enemy.score);
    expl.setOnFinished((ActionEvent) -> {
      field.getChildren().remove(expl);
      field.getChildren().remove(e);
    });
    expl.play();

    if (!stateSpecial) {
      enableSpecial();
    }
  }

  private void destroyDonttouch(EnemyView e) { // 触ってはいけない敵破壊時の処理
    ImageView crossView = new ImageView(cross); // バツ印
    destroyCommons(e, crossView,-10*e.enemy.score);
    PauseTransition t = new PauseTransition(Duration.millis(1680));
    t.setOnFinished((ActionEvent) -> {
      field.getChildren().remove(e);
      field.getChildren().remove(crossView);
    });
    donttouchSound.play();
    t.play();
  }

  private void destroyCommons(EnemyView enemy, ImageView effect, int scoreDelta) {
    // 敵破壊時の共通処理

    addScore(scoreDelta);

    /* 敵の処理 */
    enemy.detonate(); // 敵に破壊を通知
    enemy.setMouseTransparent(true); // 敵がマウスに反応しないようにする

    /* エフェクトの処理 */
    effect.relocate(enemy.getLayoutX(),enemy.getLayoutY()); // エフェクトの位置を敵に合わせる
    effect.setFitWidth(enemy.getFitWidth()); // エフェクトの大きさを敵に合わせる
    effect.setPreserveRatio(true); // アスペクト比を維持

    /* 点数表示の処理 */
    Text t = new Text(Integer.toString(scoreDelta));
    if (scoreDelta < 0) t.setFill(Color.RED); // 減点のときは赤にする
    t.setFont(Font.font(40)); // フォントサイズ
    t.relocate(enemy.getLayoutX(),enemy.getLayoutY()); // 点数の位置を敵に合わせる

    PathTransition move = new PathTransition( // 点数は表示後上に移動していく
      Duration.millis(500),
      new Path(new MoveTo(0.0,0.0), new LineTo(0.0,-50.0)),
      t
    );

    // 点数のフェードイン・アウト
    FadeInOut trans = new FadeInOut(Duration.ZERO, Duration.millis(1200), Duration.millis(500), t);
    // 点数はフェードアウト後削除する
    trans.setOnFinished((ActionEvent) -> field.getChildren().remove(t));
    // フェードイン・アウトと上への移動を同時に行う
    ParallelTransition p = new ParallelTransition(trans.toTransition(), move);

    field.getChildren().add(effect); // フィールドにエフェクトを表示
    field.getChildren().add(t); // フィールドに点数を表示

    p.play(); // 点数エフェクトを開始
  }

  private Enemy getEnemy() {
    // ランダムな敵を返す
    // ただし過去の出現履歴を覚えておいて出現敵が被らないようにする

    final int historyLength = 3; // 敵の出現履歴を覚える個数
    Enemy e;

    // 出現履歴と被らなくなるまで新たな敵を生成する
    do {
      e = Enemy.getRandomEnemy();
    } while (enemyHistory.contains(e));

    // 出現履歴のリストの長さを保つ
    if (enemyHistory.size() >= historyLength) {
      enemyHistory.remove();
    }
    enemyHistory.add(e);

    return e;
  }

  private void relocateEnemy(EnemyView eView) {
    // 敵をランダムな位置へ移動
    // フィールド上の敵と位置が被ったら配置し直す
    // 適当な回数やっても適切な位置が見つからなければあきらめてその位置に配置する
    int i = 0;
    do {
      eView.relocate(
        Math.random()*(field.getWidth()-eView.getRealWidth()),
        Math.random()*(field.getHeight()-eView.getRealHeight())
        );
      i++;
    } while (
      i < 100 &&
      Arrays.stream(getEnemyViewsOnField())
      .anyMatch((eOnField) -> eOnField.collideWith(eView))
    );
  }

  private void setHandler(EnemyView eView) {
    // 敵の種類によって適切なマウスハンドラを指定する

    if (stateSpecial) {
      // スペシャル状態では全敵が破壊可能になる
      eView.setOnMouseEntered((MouseEvent) -> destroyEnemy(eView) );
    } else {
      switch (eView.enemy.type) {
        case ENEMY:     // 通常敵
          eView.setOnMouseClicked((MouseEvent) -> destroyEnemy(eView));
          break;
        case SPECIAL:   // スペシャル敵
          eView.setOnMouseClicked((MouseEvent) -> destroySpecial(eView));
          break;
        case DONTTOUCH: // 触ってはいけない敵
          eView.setOnMouseClicked((MouseEvent) -> destroyDonttouch(eView));
          break;
      };
    }
  }

  private void spawnEnemy() { // 敵をスポーンする
    /* 敵の表示を設定 */
    EnemyView eView = new EnemyView(getEnemy());
    eView.setFitWidth(100); // 敵の幅設定
    eView.setPreserveRatio(true);
    eView.setSmooth(true);

    relocateEnemy(eView);

    setHandler(eView);

    eView.setOpacity(0.0); // フェードインに備えて透明にしておく

    FadeInOut trans = new FadeInOut(
      Duration.millis(500),
      Duration.seconds(2),
      Duration.seconds(1),
      eView
    );

    trans.setOnFinished((ActionEvent) -> {
      // フェードアウトしたらフィールドから削除する
      field.getChildren().remove(eView);
    });

    field.getChildren().add(eView);
    trans.play();
  }
}
