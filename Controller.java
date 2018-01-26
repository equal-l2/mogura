import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Controller implements Initializable {
  @FXML
  private Label scoreLabel; // スコア表示用ラベル
  @FXML
  private Pane field; // 敵表示用ペイン（「フィールド」）

  private int score;
  private Timeline spawner; // 敵表示用タイムライン
  private MediaPlayer specialMusic; // スペシャル状態用音楽
  private boolean stateSpecial; // スペシャル状態かを示すブーリアン
  private LinkedList<Enemy> enemyHistory; // 過去にスポーンした敵を記録するキュー

  private final Duration defaultSpawnRate = Duration.millis(1000);
  private final Duration specialSpawnRate = Duration.millis(500);

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    /* staticコンストラクタをあらかじめ呼ぶための処理 */
    new Explosion();
    Enemy.getRandomEnemy();

    /* 各種初期化 */
    enemyHistory = new LinkedList<Enemy>();
    specialMusic = new MediaPlayer(new Media(Paths.get("special.wav").toUri().toString()));
    specialMusic.setCycleCount(1);
    specialMusic.setVolume(0.2);
    stateSpecial = false;
    setScore(0);
    spawner = new Timeline();
    spawner.setCycleCount(Animation.INDEFINITE);
    setSpawnTimeToDefault();
    spawner.play();
  }

  private EnemyView[] getEnemyViewsOnField() {
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

    specialMusic.setOnEndOfMedia(() -> { // 音楽がループし終わったらスペシャル状態を抜ける
      if (specialMusic.getCurrentCount() == specialMusic.getCycleCount()) {
        specialMusic.stop();
        setSpawnTimeToDefault();
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

  private void setSpawnTimeToDefault() { // スポーン間隔をデフォルトに戻す
    setSpawnTime(defaultSpawnRate); // デフォルトのスポーン間隔をいじるときはここ
  }

  private void addScore(int i) { // スコア加算
    setScore(score+i);
  }

  private void setScore(int i) { // スコア設定　基本的に直接呼んではいけない
    score = i;
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
    ImageView cross = new ImageView(new Image("cross.png"));
    destroyCommons(e, cross,-10*e.enemy.score);
    AudioClip a = new AudioClip(Paths.get("bubbu.wav").toUri().toString());
    PauseTransition t = new PauseTransition(Duration.millis(1680));
    t.setOnFinished(ActionEvent -> {
      field.getChildren().remove(e);
      field.getChildren().remove(cross);
    });
    a.play();
    t.play();
  }

  private void destroyCommons(EnemyView enemy, ImageView effect, int scoreDelta) { // 敵破壊時の共通処理
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
    t.setFont(Font.font(40));
    t.relocate(enemy.getLayoutX(),enemy.getLayoutY()); // 点数の位置を敵に合わせる

    PathTransition move = new PathTransition( // 点数は表示後上に移動していく
      Duration.millis(1000),
      new Path(new MoveTo(0.0,0.0), new LineTo(0.0,-50.0)),
      t
    );

    // 点数のフェードアウト
    FadeTransition out = new FadeTransition(Duration.millis(500), t);
    out.setFromValue(1.0);
    out.setToValue(0.0);

    SequentialTransition trans = new SequentialTransition(
      new PauseTransition(Duration.millis(1200)), // 敵の表示時間
      out
    );

    ParallelTransition p = new ParallelTransition(trans, move);

    field.getChildren().add(effect); // フィールドにエフェクトを表示
    field.getChildren().add(t); // フィールドに点数を表示
    p.play();
  }

  private void spawnEnemy() { // 敵をスポーンする
    /* 過去5回でスポーンさせた敵はスポーンさせない */
    Enemy e;
    do {
      e = Enemy.getRandomEnemy();
    } while (enemyHistory.contains(e));

    if (enemyHistory.size() >= 5) {
      enemyHistory.remove();
    }
    enemyHistory.add(e);

    /* 敵の表示を設定 */
    EnemyView eView = new EnemyView(e);
    eView.setFitWidth(100); // 敵の幅設定
    eView.setPreserveRatio(true);
    eView.setSmooth(true);

    // 敵をランダムな位置へ移動
    // フィールド上の敵と位置が被ったら配置し直す
    // フィールドが埋まっていて適切な位置が見つからなければあきらめる
    {
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
      System.out.println(i);
    }
    // 敵の種類によって適切な処理を指定
    if (stateSpecial) {
      eView.setOnMouseEntered(MouseEvent -> destroyEnemy(eView) );
    } else {
      switch (e.type) {
        case ENEMY: {
          eView.setOnMouseClicked(MouseEvent -> destroyEnemy(eView));
          break;
        }
        case SPECIAL: {
          eView.setOnMouseClicked(MouseEvent -> destroySpecial(eView));
          break;
        }
        case DONTTOUCH: {
          eView.setOnMouseClicked(MouseEvent -> destroyDonttouch(eView));
          break;
        }
      };
    }

    eView.setOpacity(0.0); // フェードインに備えて透明にしておく

    // 敵のフェードイン
    FadeTransition in = new FadeTransition(Duration.millis(500), eView);
    in.setFromValue(0.0);
    in.setToValue(1.0);

    // 敵のフェードアウト
    FadeTransition out = new FadeTransition(Duration.seconds(1), eView);
    out.setFromValue(1.0);
    out.setToValue(0.0);

    SequentialTransition trans = new SequentialTransition(
      in,
      new PauseTransition(Duration.seconds(2)), // 敵の表示時間
      out
    );

    trans.setOnFinished(ActionEvent -> {
      // フェードアウトしたらフィールドから削除する
      field.getChildren().remove(eView);
    });

    field.getChildren().add(eView);
    trans.play();
  }
}
