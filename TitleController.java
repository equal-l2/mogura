import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TitleController {
  @FXML
  private VBox ranking;

  @FXML
  private void initialize() {
    // ランキングを表示する
    final Ranking.Ranker[] rankers = RankingManager.load().toRankerArray();
    HBox[] content = new HBox[5];

    for(int i = 0; i < 5; ++i) {
      if(i < rankers.length) {
        Text nums = new Text(String.format("%2d : %-5d ",i+1, rankers[i].score));
        nums.setFont(Font.font("Inconsolata",40));

        // Textだと長過ぎる名前で表示が狂うのでLabelを使う
        // Labelは長過ぎるときに省略してくれる
        Label name = new Label(rankers[i].name);
        name.setFont(Font.font(40));

        content[i] = new HBox(nums,name);
      } else {
        Text nums = new Text(String.format("%2d :       ",i+1));
        nums.setFont(Font.font("Inconsolata",40));

        content[i] = new HBox(nums);
      }
    }
    ranking.getChildren().setAll(content);
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
