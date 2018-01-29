import java.util.ArrayList;
import javafx.scene.text.Text;

public class Ranking { // ランキング用オブジェクト
  public static final int numRankers = 5; // ランキングの最大人数
  private ArrayList<Ranker> rankers = new ArrayList<>(); // ランキングを入れるリスト

  public static class Ranker {
    public String name;
    public int score;

    Ranker(String name, int score) {
      this.name = name;
      this.score = score;
    }

    @Override
    public String toString() {
      return String.format("%5d\t%s",score,name);
    }
  }

  public Ranking(Ranker[] rankers) {
    // 配列が最大人数より長いときは余剰分を切り落とす
    for(int i = 0; i < rankers.length && i < numRankers; ++i) {
      this.rankers.add(rankers[i]);
    }
  }

  public int add(Ranker e) { // ランキングへの追加処理
    if(rankers.size() < numRankers || rankers.get(rankers.size()-1).score <= e.score) {
      // 新スコアの挿入先を探す
      // 同じスコアの場合は新スコアが上に来るようにする
      int pos = rankers.size() - 1;
      while (pos >= 0 && rankers.get(pos).score <= e.score) {
        --pos;
      };
      ++pos; // 挿入のために調整
      if(pos < 0) { // リストが空のときの調整
        pos = 0;
      }
      rankers.add(pos,e);

      if (rankers.size() > numRankers) {
        // 超過分のスコアは削除
        rankers.remove(rankers.size()-1);
      }
      assert pos < numRankers;
      return pos;
    } else {
      // ランキングにランクインしなかったとき
      return -1;
    }
  }

  public Ranker[] toRankerArray() {
    return rankers.stream().toArray(Ranker[]::new);
  }
}
