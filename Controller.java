import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
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
import javafx.util.Duration;

public class Controller implements Initializable {
  @FXML
  private Label scoreLabel;
  @FXML
  private Pane field;

  private int score;
  private Timeline spawner;
  private MediaPlayer specialMusic;
  private boolean stateSpecial;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Never used but needed to invoke static constructors
    new Explosion();
    Enemy.getRandomEnemy();

    specialMusic = new MediaPlayer(new Media(Paths.get("special.wav").toUri().toString()));
    specialMusic.setCycleCount(3);
    specialMusic.setVolume(0.5);
    stateSpecial = false;
    setScore(0);
    spawner = new Timeline();
    spawner.setCycleCount(Animation.INDEFINITE);
    setSpawnTimeToDefault();
    spawner.play();
  }

  private void enableSpecial() {
    if (!stateSpecial) {
          FilteredList<Node> iList = field.getChildren().filtered(node -> {
            return node instanceof EnemyView;
          });
          for(Node n : iList) {
            EnemyView eView = (EnemyView)n;
            if (!eView.underDestruction) {
              destroyEnemy(eView);
            }
          }
          stateSpecial = true;
          setSpawnTime(Duration.millis(400));
          specialMusic.setOnEndOfMedia( () -> {
            if (specialMusic.getCurrentCount() == specialMusic.getCycleCount()) {
              specialMusic.stop();
              setSpawnTimeToDefault();
              stateSpecial = false;
            }
          });
          specialMusic.play();
    }
  }

  private void setSpawnTime(Duration d) {
    spawner.stop();
    spawner.getKeyFrames().setAll(new KeyFrame(
      d,
      ActionEvent -> spawnEnemy()
    ));
    spawner.play();
  }

  private void setSpawnTimeToDefault() {
    setSpawnTime(Duration.millis(1000));
  }

  private void addScore(int i) {
    setScore(score+i);
  }

  private void setScore(int i) {
    score = i;
    scoreLabel.setText(String.format("Score: %d",score));
  }

  private void destroyEnemy(EnemyView e) {
        Explosion expl = new Explosion();
        destroyCommons(e, expl);
        addScore(e.enemy.score);
        expl.setOnFinished( ActionEvent -> {
          field.getChildren().remove(expl);
          field.getChildren().remove(e);
        });
        field.getChildren().add(expl);
        expl.play();
  }

  private void destroySpecial(EnemyView e) {
        Explosion expl = new Explosion();
        destroyCommons(e, expl);
        addScore(e.enemy.score);
        expl.setOnFinished( ActionEvent -> {
          field.getChildren().remove(expl);
          field.getChildren().remove(e);
        });
        expl.setRate(2.0);
        field.getChildren().add(expl);
        expl.play();
        enableSpecial();
  }

  private void destroyDonttouch(EnemyView e) {
        ImageView cross = new ImageView(new Image("cross.png"));
        destroyCommons(e, cross);
        addScore(-10*e.enemy.score);
        field.getChildren().add(cross);
        AudioClip a = new AudioClip(Paths.get("bubbu.wav").toUri().toString());
        PauseTransition t = new PauseTransition(Duration.millis(1690));
        t.setOnFinished( ActionEvent -> {
          field.getChildren().remove(e);
          field.getChildren().remove(cross);
        });
        a.play();
        t.play();
  }

  private void destroyCommons(EnemyView enemy, ImageView effect) {
        enemy.underDestruction = true;
        enemy.setMouseTransparent(true);
        effect.relocate(enemy.getLayoutX(),enemy.getLayoutY());
        effect.setFitWidth(enemy.getFitWidth());
        effect.setPreserveRatio(true);
  }

  private void spawnEnemy() {
    Enemy e = Enemy.getRandomEnemy();
    EnemyView eView = new EnemyView(e);
    eView.setFitWidth(100);
    eView.setPreserveRatio(true);

    eView.relocate(
      Math.random()*(field.getWidth()-eView.getBoundsInParent().getWidth()),
      Math.random()*(field.getHeight()-eView.getBoundsInParent().getHeight())
    );

    if (stateSpecial) {
      eView.setOnMouseEntered( MouseEvent -> destroyEnemy(eView) );
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
    eView.setOpacity(0.0);
    eView.setSmooth(true);

    FadeTransition in = new FadeTransition(Duration.millis(500), eView);
    in.setFromValue(0.0);
    in.setToValue(1.0);

    FadeTransition out = new FadeTransition(Duration.seconds(1), eView);
    out.setFromValue(1.0);
    out.setToValue(0.0);

    SequentialTransition trans = new SequentialTransition(
        in,
        new PauseTransition(Duration.seconds(2)),
        out
    );

    trans.setOnFinished( ActionEvent -> {
      field.getChildren().remove(eView);
    });

    field.getChildren().add(eView);
    trans.play();
  }
}
