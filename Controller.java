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

public class Controller implements Initializable {
  @FXML
  private Label scoreLabel;
  @FXML
  private Pane field;

  private int score;
  private Timeline spawner;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    setScore(0);
    spawner = new Timeline(new KeyFrame(
      Duration.seconds(2),
      (ActionEvent) -> { spawnEnemy(); }
    ));
    spawner.setCycleCount(Animation.INDEFINITE);
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
    ImageView enemy = new ImageView(new Image("honsha.jpeg"));
    enemy.setFitWidth(100);
    enemy.setPreserveRatio(true);
    enemy.setOnMouseClicked( (MouseEvent) -> {
      addScore(10);
      field.getChildren().remove(enemy);
    });
    enemy.relocate(
      Math.random()*(field.getWidth()-enemy.getBoundsInParent().getWidth()),
      Math.random()*(field.getHeight()-enemy.getBoundsInParent().getHeight())
    );
    enemy.setOpacity(0.0);
    enemy.setSmooth(true);

    FadeTransition in = new FadeTransition(Duration.millis(500), enemy);
    in.setFromValue(0.0);
    in.setToValue(1.0);

    FadeTransition out = new FadeTransition(Duration.seconds(1), enemy);
    out.setFromValue(1.0);
    out.setToValue(0.0);

    SequentialTransition trans = new SequentialTransition(
        in,
        new PauseTransition(Duration.seconds(2)),
        out
    );
    field.getChildren().add(enemy);
    trans.play();
  }
}
