import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class RankingDisplay extends VBox { // ランキング表示
  static public class Element extends HBox {
    // ランキングの一行

    final boolean hasRanker;

    Element(int rank, double fontSize, Ranking.Ranker r) {
      hasRanker = true;
      Text nums = new Text(String.format("%2d : %-5d ", rank, r.score));
      nums.setFont(Font.font("Inconsolata", fontSize));

      // Textだと長過ぎる名前で表示が狂うのでLabelを使う
      // Labelは長過ぎるときに省略してくれる
      Label name = new Label(r.name);
      name.setFont(Font.font(fontSize));

      getChildren().setAll(nums,name);
    }

    Element(int rank, double fontSize) {
      hasRanker = false;
      Text nums = new Text(String.format("%2d :       ", rank));
      nums.setFont(Font.font("Inconsolata", fontSize));

      getChildren().setAll(nums);
    }

    public void setFill(Paint p) {
      ((Text)(getChildren().get(0))).setFill(p);
      if (hasRanker) {
        ((Label)(getChildren().get(1))).setTextFill(p);
      }
    }
  }

  RankingDisplay(Ranking r, double fontSize) {
    // ランキングに合わせて内容を生成
    final Ranking.Ranker[] rankers = r.toRankerArray();

    for(int i = 0; i < Ranking.numRankers; ++i) {
      if(i < rankers.length) {
        getChildren().add(new Element(i+1, fontSize, rankers[i]));
      } else {
        getChildren().add(new Element(i+1, fontSize));
      }
    }
  }
}
