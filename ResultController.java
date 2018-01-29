import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ResultController {
  @FXML
  private VBox ranking;
  private int score;

  @FXML
  private void onReturnButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("assets/fxml/Title.fxml");
  }

  @FXML
  private void initialize() {
    javafx.application.Platform.runLater(() -> processRanking());
  }

  public void setScore(int score) {
    this.score = score;
  }

  public void processRanking() { // 点数に応じてランキングを改変する
    // 一度ランキングに加えてみて、ランクインしているかを調べる
    Ranking r = RankingManager.load();
    final int pos = r.add(new Ranking.Ranker("",score));
    r = RankingManager.load();

    if(pos != -1) { // ランクインしているとき
      final String name = getName(pos);
      r.add(new Ranking.Ranker(name, score));
      printRanking(r);

      // 追加したスコアを色付けする
      HBox tf = (HBox) ranking.getChildren().get(pos);
      ((Text)(tf.getChildren().get(0))).setFill(Color.RED);
      ((Label)(tf.getChildren().get(1))).setTextFill(Color.RED);

      RankingManager.write(r);
    } else { // ランクインしなかったらランキングの下に点数を出す
      printRanking(r);

      Text yours = new Text(String.format("Yours : %d", score));
      yours.setFont(Font.font("Inconsolata",50));
      yours.setFill(Color.BLUE);

      ranking.getChildren().addAll(new HBox(), new HBox(yours));
    }
  }

  public String getName(int pos) {
    // ダイアログを出して名前を聞く
    TextInputDialog d = new TextInputDialog();
    d.setHeaderText(String.format("%d位おめでとう！",pos+1));
    d.setContentText("名前を入力してください");
    d.setGraphic(null);

    // 名前をもらって、空白なら"No Name"を
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

  public void printRanking(Ranking r) {
    // ランキングを表示する
    final Ranking.Ranker[] rankers = r.toRankerArray();
    HBox[] content = new HBox[5];

    for(int i = 0; i < 5; ++i) {
      if(i < rankers.length) {
        Text nums = new Text(String.format("%2d : %-5d ",i+1, rankers[i].score));
        nums.setFont(Font.font("Inconsolata",50));

        // Textだと長過ぎる名前で表示が狂うのでLabelを使う
        // Labelは長過ぎるときに省略してくれる
        Label name = new Label(rankers[i].name);
        name.setFont(Font.font(50));

        content[i] = new HBox(nums,name);
      } else {
        Text nums = new Text(String.format("%2d :       ",i+1));
        nums.setFont(Font.font("Inconsolata",50));

        content[i] = new HBox(nums);
      }
    }
    ranking.getChildren().setAll(content);
  }
}
