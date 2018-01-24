import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.util.stream.Stream;
import java.util.Objects;
import java.util.ArrayList;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;

public class Controller implements Initializable {
  @FXML
  private Label scoreLabel;
  @FXML
  private Pane field;

  private int score;
  private Timeline spawner;
  private MediaPlayer specialMusic;
  private boolean stateSpecial;

  static class Enemy extends Image {
    enum TYPE {
      ENEMY,
      SPECIAL,
      DONTTOUCH
    }
    final int score;
    final TYPE type;
    final static Enemy[] enemies;

    static {
      ArrayList<Enemy> eList = new ArrayList<>();
      try {
        BufferedReader bfr = Files.newBufferedReader(Paths.get("enemies.conf"));
        String line;
        while ((line = bfr.readLine()) != null) {
          line.trim();
          if (line.charAt(0) == '#') continue;
          final String[] ss = line.split(" ");
          try {
            eList.add(new Enemy(ss[0], ss[1]));
          } catch (Exception e) {
            System.err.println("Invalid conf line: "+line);
          }
        }
        bfr.close();
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      enemies = eList.toArray(new Enemy[eList.size()]);
    }

    Enemy(String fileName, String effect) {
      super("pic/"+fileName);
      final String[] ss = effect.split(":");
      score = Integer.parseInt(ss[0]);
      if (ss.length > 1) {
        switch (ss[1]) {
          case "special":
            type = TYPE.SPECIAL;
            break;
          case "donttouch":
            type = TYPE.DONTTOUCH;
            break;
          default:
            type = TYPE.ENEMY;
        }
      } else {
        type = TYPE.ENEMY;
      }
    }

    static Enemy getRandomEnemy() {
      return enemies[(int)(enemies.length*Math.random())];
    }
  }

  class EnemyView extends ImageView {
    boolean underDestruction;
    Enemy enemy;
    EnemyView(Enemy e) {
      super(e);
      this.enemy = e;
      underDestruction = false;
    }
  }

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
    setSpawnTime(Duration.millis(1500));
    spawner.play();
  }

  private void enableSpecial() {
    if (!stateSpecial) {
          FilteredList<Node> iList = field.getChildren().filtered( node -> {
            return node instanceof EnemyView;
          });
          for(Node n : iList) {
            EnemyView eView = (EnemyView)n;
            if (!eView.underDestruction) {
              eView.setMouseTransparent(true);
              addScore(eView.enemy.score);
              Explosion expl = new Explosion();
              expl.relocate(eView.getLayoutX(),eView.getLayoutY());
              expl.setFitWidth(eView.getFitWidth());
              expl.setPreserveRatio(true);
              expl.setOnFinished( ActionEvent -> {
                field.getChildren().remove(expl);
                field.getChildren().remove(eView);
              });
              field.getChildren().add(expl);
              expl.play();
            }
          }
          stateSpecial = true;
          setSpawnTime(Duration.millis(400));
          specialMusic.setOnEndOfMedia( () -> {
            if (specialMusic.getCurrentCount() == specialMusic.getCycleCount()) {
              specialMusic.stop();
              setSpawnTime(Duration.millis(1500));
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

  private void addScore(int i) {
    setScore(score+i);
  }

  private void setScore(int i) {
    score = i;
    scoreLabel.setText(String.format("Score: %d",score));
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
      eView.setOnMouseEntered( MouseEvent -> {
        eView.underDestruction = true;
        eView.setMouseTransparent(true);
        addScore(e.score);
        Explosion expl = new Explosion();
        expl.relocate(eView.getLayoutX(),eView.getLayoutY());
        expl.setFitWidth(eView.getFitWidth());
        expl.setPreserveRatio(true);
        expl.setOnFinished( ActionEvent -> {
          field.getChildren().remove(expl);
          field.getChildren().remove(eView);
        });
        field.getChildren().add(expl);
        expl.play();
      });
    } else {
      eView.setOnMouseClicked( MouseEvent -> {
        eView.setMouseTransparent(true);
        eView.underDestruction = true;
        switch (e.type) {
          case ENEMY: {
            addScore(e.score);
            Explosion expl = new Explosion();
            expl.relocate(eView.getLayoutX(),eView.getLayoutY());
            expl.setFitWidth(eView.getFitWidth());
            expl.setPreserveRatio(true);
            expl.setOnFinished( ActionEvent -> {
              field.getChildren().remove(expl);
              field.getChildren().remove(eView);
            });
            field.getChildren().add(expl);
            expl.play();
            break;
          }
          case SPECIAL: {
            addScore(e.score);
            Explosion expl = new Explosion();
            expl.relocate(eView.getLayoutX(),eView.getLayoutY());
            expl.setFitWidth(eView.getFitWidth());
            expl.setPreserveRatio(true);
            expl.setOnFinished( ActionEvent -> {
              field.getChildren().remove(expl);
              field.getChildren().remove(eView);
            });
            expl.setRate(2.0);
            field.getChildren().add(expl);
            expl.play();
            enableSpecial();
            break;
          }
          case DONTTOUCH: {
            addScore(-10*e.score);
            ImageView cross = new ImageView(new Image("cross.png"));
            cross.relocate(eView.getLayoutX(),eView.getLayoutY());
            cross.setFitWidth(eView.getFitWidth());
            cross.setPreserveRatio(true);
            field.getChildren().add(cross);
            AudioClip a = new AudioClip(Paths.get("bubbu.wav").toUri().toString());
            PauseTransition t = new PauseTransition(Duration.millis(1690));
            t.setOnFinished( ActionEvent -> {
              field.getChildren().remove(eView);
              field.getChildren().remove(cross);
            });
            a.play();
            t.play();
            break;
          }
        }
      });
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
