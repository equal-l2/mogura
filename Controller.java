import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.Random;

public class Controller implements Initializable {
  @FXML
  private Label scoreLabel;
  @FXML
  private Pane field;

  private int score;
  private Timeline spawner;
  private Random r;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    r = new Random();
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
      r.nextDouble()*(field.getWidth()-enemy.getBoundsInParent().getWidth()),
      r.nextDouble()*(field.getHeight()-enemy.getBoundsInParent().getHeight())
    );
    enemy.setOpacity(0.0);
    enemy.setSmooth(true);

    FadeTransition f = new FadeTransition(Duration.millis(500), enemy);
    f.setFromValue(0.0);
    f.setToValue(1.0);

    field.getChildren().add(enemy);
    f.play();
  }
}
