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

public class Controller implements Initializable {
  @FXML
  private Label scoreLabel;
  @FXML
  private Pane field;

  private int score;
  private Timeline spawner;

  static class Enemy extends Image {
    enum TYPE {
      ENEMY,
      SPECIAL,
      DONTTOUCH
    }
    final int score;
    final TYPE type;

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
  }

  Enemy[] enemies;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    // Never used but needed to invoke static constructor
    // and load images and the sound beforehand
    Explosion e = new Explosion();

    setScore(0);
    spawner = new Timeline(new KeyFrame(
      Duration.seconds(2),
      ActionEvent -> spawnEnemy()
    ));
    spawner.setCycleCount(Animation.INDEFINITE);
    spawner.play();
    loadConf();
  }

  private void loadConf() {
    try {
      BufferedReader bfr = Files.newBufferedReader(Paths.get("enemies.conf"));
      ArrayList<Enemy> eList = new ArrayList<>();
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
      enemies = eList.toArray(new Enemy[eList.size()]);
      bfr.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private Enemy getRandomEnemy() {
    return enemies[(int)(enemies.length*Math.random())];
  }

  private void addScore(int i) {
    setScore(score+i);
  }

  private void setScore(int i) {
    score = i;
    scoreLabel.setText(String.format("Score: %d",score));
  }

  private void spawnEnemy() {
    Enemy e = getRandomEnemy();
    ImageView enemyView = new ImageView(e);
    enemyView.setFitWidth(100);
    enemyView.setPreserveRatio(true);

    enemyView.relocate(
      Math.random()*(field.getWidth()-enemyView.getBoundsInParent().getWidth()),
      Math.random()*(field.getHeight()-enemyView.getBoundsInParent().getHeight())
    );

    enemyView.setOnMouseClicked( (MouseEvent) -> {
      enemyView.setMouseTransparent(true);
      addScore(e.score);
      Explosion expl = new Explosion();
      expl.relocate(enemyView.getLayoutX(),enemyView.getLayoutY());
      expl.setFitWidth(enemyView.getFitWidth());
      expl.setPreserveRatio(true);
      expl.setOnFinished( (ActionEvent) -> {
        field.getChildren().remove(expl);
        field.getChildren().remove(enemyView);
      });
      field.getChildren().add(expl);
      expl.play();
    });
    enemyView.setOpacity(0.0);
    enemyView.setSmooth(true);

    FadeTransition in = new FadeTransition(Duration.millis(500), enemyView);
    in.setFromValue(0.0);
    in.setToValue(1.0);

    FadeTransition out = new FadeTransition(Duration.seconds(1), enemyView);
    out.setFromValue(1.0);
    out.setToValue(0.0);

    SequentialTransition trans = new SequentialTransition(
        in,
        new PauseTransition(Duration.seconds(2)),
        out
    );
    field.getChildren().add(enemyView);
    trans.play();
  }
}
