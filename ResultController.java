import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ResultController {
  @FXML
  Pane rankingPane;

  private int score;

  @FXML
  private void onReturnButtonAction(ActionEvent e) {
    Launcher.setSceneFromFXML("assets/fxml/Title.fxml");
  }

  @FXML
  private void initialize() {
    // ランキング処理内でノードにアクセスするので初期化後まで遅延させる
    javafx.application.Platform.runLater(() -> processRanking());
  }

  public void setScore(int score) {
    this.score = score;
  }

  public void processRanking() { // ランキング表示
    final double fontSize = 50;

    // 一度ランキングに加えてみて、ランクインしているかを調べる
    Ranking r = Ranking.loadFromFile();
    final int pos = r.add(new Ranking.Ranker("", score));
    r = Ranking.loadFromFile();

    RankingDisplay disp;
    if(pos != -1) { // ランクインしているとき
      final String name = getName(pos);
      r.add(new Ranking.Ranker(name, score));
      disp = new RankingDisplay(r, fontSize);

      // 追加したスコアを色付けする
      RankingDisplay.Element e = (RankingDisplay.Element) disp.getChildren().get(pos);
      e.setFill(Color.RED);

      Ranking.writeToFile(r);
    } else { // ランクインしなかったらランキングの下に点数を出す
      disp = new RankingDisplay(r, fontSize);

      Text yours = new Text(String.format("Yours : %d", score));
      yours.setFont(Font.font("Inconsolata", fontSize));
      yours.setFill(Color.BLUE);
      disp.getChildren().add(yours);
    }
    rankingPane.getChildren().add(disp);
  }

  public String getName(int pos) {
    // ダイアログを出して名前を聞く
    TextInputDialog d = new TextInputDialog();
    d.setHeaderText(String.format("%d位おめでとう！", pos+1));
    d.setContentText("名前を入力してください");
    d.setGraphic(null);

    // 名前を受け取り、空白なら"No Name"を
    // さもなくばその名前を返す
    Optional<String> result = d.showAndWait();
    if (result.isPresent()) {
      String s = result.get().trim();
      if(s.equals("")) {
        return "No Name";
      } else {
        return s;
      }
    } else {
      return "No Name";
    }
  }
}
