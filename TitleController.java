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

  private final double rankingFontSize = 40;

  @FXML
  private void onStartButtonAction(ActionEvent e) {
    Launcher.setSceneFromFXML("assets/fxml/Main.fxml");
  }

  @FXML
  private void onHowToPlayButtonAction(ActionEvent e) {
    Launcher.setSceneFromFXML("assets/fxml/HowToPlay.fxml");
  }

  @FXML
  private void initialize() {
    // ランキングを表示する
    final Ranking.Ranker[] rankers = Ranking.loadFromFile().toRankerArray();
    HBox[] content = new HBox[Ranking.numRankers];

    for(int i = 0; i < Ranking.numRankers; ++i) {
      if(i < rankers.length) {
        Text nums = new Text(String.format("%2d : %-5d ",i+1, rankers[i].score));
        nums.setFont(Font.font("Inconsolata",rankingFontSize));

        // Textだと長過ぎる名前で表示が狂うのでLabelを使う
        // Labelは長過ぎるときに省略してくれる
        Label name = new Label(rankers[i].name);
        name.setFont(Font.font(rankingFontSize));

        content[i] = new HBox(nums,name);
      } else {
        Text nums = new Text(String.format("%2d :       ",i+1));
        nums.setFont(Font.font("Inconsolata",rankingFontSize));

        content[i] = new HBox(nums);
      }
    }
    ranking.getChildren().setAll(content);
  }
}
