import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
  private Label scoreLabel; // スコア表示用ラベル
  @FXML
  private Pane field; // 敵表示用ペイン（「フィールド」）
  @FXML
  private Label timerLabel; // タイマー表示用ラベル

  private int score; // スコア
  private Timeline spawner; // 敵表示用タイムライン
  private MediaPlayer specialMusic; // スペシャル状態用音楽
  private boolean stateSpecial; // スペシャル状態かを示すブーリアン
  private LinkedList<Enemy> enemyHistory; // 過去にスポーンした敵を記録するキュー
  private AudioClip donttouchSound; // 触れてはいけない敵をクリックしたときの音
  private Image cross; // 触れてはいけない敵をクリックした時の画像
  private int seconds; // 残り秒数
  private Timeline timer;

  private static final Duration defaultSpawnRate = Duration.millis(800); // デフォルトのスポーン間隔
  private static final Duration specialSpawnRate = Duration.millis(400); // スペシャルタイムのスポーン間隔
  private static final Duration playTime = Duration.seconds(10); // プレイ時間

  @FXML
  private void initialize() {
    /* 各種変数初期化 */
    enemyHistory = new LinkedList<Enemy>();
    specialMusic = new MediaPlayer(new Media(Paths.get("assets/sounds/special.wav").toUri().toString()));
    specialMusic.setCycleCount(1);
    specialMusic.setVolume(0.2);
    donttouchSound = new AudioClip(Paths.get("assets/sounds/bubbu.wav").toUri().toString());
    cross = new Image("assets/cross.png");
    stateSpecial = false;
    score = 0;
    updateScore();

    /* タイマー初期化 */
    timer = new Timeline();
    timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (ActionEvent) -> {
      if(seconds <= 0) {
        proceedToResult();
      } else {
        --seconds;
        updateTimer();
      }
    }));
    setTimer(playTime);
    timer.setCycleCount(Animation.INDEFINITE);
    timer.play();

    spawner = new Timeline();
    spawner.setCycleCount(Animation.INDEFINITE);
    setSpawnTime(defaultSpawnRate);
    spawner.play();
  }

  public void setTimer(Duration d) {
    seconds = (int) d.toSeconds();
    updateTimer();
  }

  private void updateTimer() { // タイマーの表示を更新する
    timerLabel.setText(String.format("%02d:%02d", seconds/60, seconds%60));
  }

  private void proceedToResult() { // リザルト画面へ進む
    // タイムライン等は止めないと止まらないので止める
    timer.stop();
    spawner.stop();
    specialMusic.stop();

    // リザルト画面をロード
    FXMLLoader loader = FXMLManager.getFXMLLoader("assets/fxml/Result.fxml");
    try {
      Scene s = new Scene(loader.load());
      ResultController c = loader.getController();
      c.prepareRanking(score); // スコアを渡してランキングを表示させる
      FXMLManager.setScene(s);
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }

  private EnemyView[] getEnemyViewsOnField() {
    // フィールド上の敵を配列に入れて返す
    return field.getChildren()
      .filtered(node -> {
        return node instanceof EnemyView;
      })
      .stream()
      .map(e -> (EnemyView)e)
      .toArray(EnemyView[]::new);
  }

  private void enableSpecial() { // スペシャル状態に入る
    if (stateSpecial) { return; } // すでにスペシャル状態なら何もしない
    stateSpecial = true;

    /* フィールド上の敵を全爆破 */
    EnemyView[] eList = getEnemyViewsOnField();
    for(EnemyView e : eList) {
      if (!e.isUnderDestruction()) {
        destroyEnemy(e);
      }
    }

    setSpawnTime(specialSpawnRate); // スポーン間隔を早める

    specialMusic.setOnEndOfMedia(() -> {
      // 音楽がループし終わったらスペシャル状態を抜ける
      if (specialMusic.getCurrentCount() == specialMusic.getCycleCount()) {
        specialMusic.stop();
        setSpawnTime(defaultSpawnRate);
        stateSpecial = false;
      }
    });
    specialMusic.play();
  }

  private void setSpawnTime(Duration d) { // スポーン間隔を変更する
    spawner.stop();
    spawner.getKeyFrames().setAll(new KeyFrame(d, ActionEvent -> spawnEnemy()));
    spawner.play();
  }

  private void addScore(int i) {
    score += i;
    updateScore();
  }

  private void updateScore() {
    scoreLabel.setText(String.format("Score: %d",score));
  }

  private void destroyEnemy(EnemyView e) { // 敵破壊時の処理
    Explosion expl = new Explosion();
    destroyCommons(e, expl, e.enemy.score);
    expl.setOnFinished(ActionEvent -> {
      field.getChildren().remove(expl);
      field.getChildren().remove(e);
    });
    expl.play();
  }

  private void destroySpecial(EnemyView e) { // スペシャル敵破壊時の処理
    Explosion expl = new Explosion();
    expl.setRate(2.0);
    destroyCommons(e, expl, e.enemy.score);
    expl.setOnFinished(ActionEvent -> {
      field.getChildren().remove(expl);
      field.getChildren().remove(e);
    });
    expl.play();
    enableSpecial();
  }

  private void destroyDonttouch(EnemyView e) { // 触ってはいけない敵破壊時の処理
    ImageView crossView = new ImageView(cross);
    destroyCommons(e, crossView,-10*e.enemy.score);
    PauseTransition t = new PauseTransition(Duration.millis(1680));
    t.setOnFinished(ActionEvent -> {
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
      Duration.millis(1000),
      new Path(new MoveTo(0.0,0.0), new LineTo(0.0,-50.0)),
      t
    );

    FadeInOut trans = new FadeInOut(Duration.ZERO, Duration.millis(1200), Duration.millis(500), t);
    trans.setOnFinished((ActionEvent) -> field.getChildren().remove(t));
    ParallelTransition p = new ParallelTransition(trans.toTransition(), move);

    field.getChildren().add(effect); // フィールドにエフェクトを表示
    field.getChildren().add(t); // フィールドに点数を表示
    p.play();
  }

  private Enemy getEnemy() {
    // ランダムな敵を返す
    // ただし過去5回の敵とかぶらないようにする*/
    Enemy e;
    do {
      e = Enemy.getRandomEnemy();
    } while (enemyHistory.contains(e));

    if (enemyHistory.size() >= 5) {
      enemyHistory.remove();
    }
    enemyHistory.add(e);
    return e;
  }

  private void relocateEnemy(EnemyView eView) {
    // 敵をランダムな位置へ移動
    // フィールド上の敵と位置が被ったら配置し直す
    // フィールドが埋まっていて適切な位置が見つからなければあきらめる
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
      .anyMatch(eOnField -> eOnField.collideWith(eView))
    );
  }

  private void setHandler(EnemyView eView) {
    // 敵の種類によって適切な処理を指定
    if (stateSpecial) {
      eView.setOnMouseEntered(MouseEvent -> destroyEnemy(eView) );
    } else {
      switch (eView.enemy.type) {
        case ENEMY:
          eView.setOnMouseClicked(MouseEvent -> destroyEnemy(eView));
          break;
        case SPECIAL:
          eView.setOnMouseClicked(MouseEvent -> destroySpecial(eView));
          break;
        case DONTTOUCH:
          eView.setOnMouseClicked(MouseEvent -> destroyDonttouch(eView));
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

    trans.setOnFinished(ActionEvent -> {
      // フェードアウトしたらフィールドから削除する
      field.getChildren().remove(eView);
    });

    field.getChildren().add(eView);
    trans.play();
  }
}
