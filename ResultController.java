import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ResultController implements Initializable {
  @FXML
  VBox ranking;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
  }

  @FXML
  private void onReturnButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("assets/fxml/Title.fxml");
  }

  public void prepareRanking(int score) { // ランキング表示の準備
    Ranking r;
    try {
      r = RankingManager.load();
    } catch (Exception e) {
      r = null;
      Launcher.abort(e);
    }
    final int pos = r.add(score);

    /* ランキング表示生成 */
    Text[] rankingText = r.toTextArray();
    Text header = new Text("Score Ranking");
    header.setTextAlignment(TextAlignment.CENTER);
    header.setFont(Font.font(60));
    for(Text t : rankingText) {
      t.setFont(Font.font(50));
    }
    if(pos != -1) { // ランクインしたら点数を赤で塗る
      rankingText[pos].setFill(Color.RED);
      ranking.getChildren().add(header);
      ranking.getChildren().addAll(rankingText);
    } else { // ランクインしなかったらランキングの下に点数を出す
      Text yours = new Text(String.format("Yours : %d", score));
      yours.setFont(Font.font(50));
      yours.setFill(Color.BLUE);
      ranking.getChildren().add(header);
      ranking.getChildren().addAll(rankingText);
      ranking.getChildren().add(new Text());
      ranking.getChildren().add(yours);
    }
    RankingManager.write(r);
  }
}
