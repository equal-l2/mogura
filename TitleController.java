import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TitleController {
  @FXML
  private VBox ranking;

  @FXML
  private void initialize() {
    // ランキングを表示する
    Text[] rankingText;
    try {
      rankingText = RankingManager.load().toTextArray();
      Text header = new Text("Score Ranking");
      header.setTextAlignment(TextAlignment.CENTER);
      header.setFont(Font.font(50));
      for(Text t : rankingText) {
        t.setFont(Font.font(40));
      }
      ranking.getChildren().add(header);
      ranking.getChildren().addAll(rankingText);
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }

  @FXML
  private void onStartButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("assets/fxml/Main.fxml");
  }

  @FXML
  private void onHowToPlayButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("assets/fxml/HowToPlay.fxml");
  }
}
