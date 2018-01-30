import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TitleController {
  @FXML
  private VBox ranking;

  private boolean inResult = false;
  private int score;

  private final int rankingFontSize = 35;

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
    // initializeはFXMLをロードした時点で呼び
    // 出されるので普通に書くとscoreは使えない
    // runLaterを使うと処理を画面表示まで先送り
    // できるので、設定されたscoreを使える
    Platform.runLater(() -> {
      if (inResult) {
        processRanking();
      } else {
        printRanking(Ranking.loadFromFile());
      }
    });
  }

  public void setScore(int score) {
    inResult = true;
    this.score = score;
  }

  private void processRanking() { // 点数に応じてランキングを改変する
    // 一度ランキングに加えてみて、ランクインしているかを調べる
    Ranking r = Ranking.loadFromFile();
    final int pos = r.add(new Ranking.Ranker("",score));
    r = Ranking.loadFromFile();

    if (pos != -1) { // ランクインしているとき
      final String name = getName(pos);
      r.add(new Ranking.Ranker(name, score));
      printRanking(r);

      // 追加したスコアを色付けする
      HBox tf = (HBox) ranking.getChildren().get(pos);
      ((Text)(tf.getChildren().get(0))).setFill(Color.RED);
      ((Label)(tf.getChildren().get(1))).setTextFill(Color.RED);

      Ranking.writeToFile(r);
    } else { // ランクインしなかったらランキングの下に点数を出す
      printRanking(r);

      Text yours = new Text(String.format("Yours : %d", score));
      yours.setFont(Font.font("Inconsolata",rankingFontSize));
      yours.setFill(Color.BLUE);
      HBox content = new HBox(yours);

      ranking.getChildren().add(content);
    }
  }

  private String getName(int pos) {
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

  private void printRanking(Ranking r) {
    // ランキングを表示する
    final Ranking.Ranker[] rankers = r.toRankerArray();
    HBox[] content = new HBox[Ranking.numRankers];

    for (int i = 0; i < Ranking.numRankers; ++i) {
      if (i < rankers.length) { // 該当順位に中身があるとき
        Text nums = new Text(String.format("%2d : %-5d ",i+1, rankers[i].score));
        nums.setFont(Font.font("Inconsolata",rankingFontSize));

        // Textだと長過ぎる名前で表示が狂うのでLabelを使う
        // Labelは長過ぎるときに省略してくれる
        Label name = new Label(rankers[i].name);
        name.setFont(Font.font(rankingFontSize));

        content[i] = new HBox(nums,name);
      } else { // 該当順位が空のとき
        Text nums = new Text(String.format("%2d :       ",i+1));
        nums.setFont(Font.font("Inconsolata",rankingFontSize));

        content[i] = new HBox(nums);
      }
    }
    ranking.getChildren().setAll(content);
  }
}
