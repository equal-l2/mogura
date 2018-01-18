import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
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
      ActionEvent -> spawnEnemy()
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

    enemy.relocate(
      Math.random()*(field.getWidth()-enemy.getBoundsInParent().getWidth()),
      Math.random()*(field.getHeight()-enemy.getBoundsInParent().getHeight())
    );

    enemy.setOnMouseClicked( (MouseEvent) -> {
      addScore(10);
      Timeline tl = new Timeline(/*24.0*/);
      ImageView expl = new ImageView();
      expl.relocate(enemy.getLayoutX(),enemy.getLayoutY());
      expl.setFitWidth(enemy.getFitWidth());
      expl.setPreserveRatio(true);
      tl.setOnFinished( (ActionEvent) -> {
        field.getChildren().remove(expl);
        field.getChildren().remove(enemy);
      });

      ObservableList<KeyFrame> kf = tl.getKeyFrames();
      Duration frameTime = Duration.ZERO;
      final Duration frameGap = Duration.millis(42);
      for(int i = 1; i <= 108; ++i) {
        final int n = i;
        kf.add(new KeyFrame(
              frameTime,
              ActionEvent -> expl.setImage(new Image(String.format("exp/%03d.png",n)))
              ));
        frameTime = frameTime.add(frameGap);
      }

      field.getChildren().add(expl);

      new AudioClip(Paths.get("exp.wav").toUri().toString()).play();
      tl.play();
    });
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
