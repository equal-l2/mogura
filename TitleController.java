import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TitleController {
  @FXML
  private VBox vb;

  @FXML
  private void onStartButtonAction(ActionEvent e) {
    // メイン画面へ移行
    Launcher.setSceneFromFXML("assets/fxml/Main.fxml");
  }

  @FXML
  private void onHowToPlayButtonAction(ActionEvent e) {
    // 遊び方画面へ移行
    Launcher.setSceneFromFXML("assets/fxml/HowToPlay.fxml");
  }

  @FXML
  private void initialize() {
    // ランキングを表示
    vb.getChildren().add(new RankingDisplay(Ranking.loadFromFile(), 40));
  }
}
