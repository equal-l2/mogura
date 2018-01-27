import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class TitleController implements Initializable {
  @FXML
  VBox ranking;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    Text[] rankingText;
    try {
      rankingText = RankingManager.load().toTextArray();
      Text header = new Text("Score Ranking");
      header.setTextAlignment(TextAlignment.CENTER);
      header.setFont(Font.font(30));
      for(Text t : rankingText) {
        t.setFont(Font.font(20));
      }
      ranking.getChildren().add(header);
      ranking.getChildren().addAll(rankingText);
    } catch (Exception e) {
      e.printStackTrace();
      Platform.exit();
      System.exit(1);
    }
  }

  @FXML
  void onStartButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("fxml/Main.fxml");
  }

  @FXML
  void onHowToPlayButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("fxml/HowToPlay.fxml");
  }
}
