import java.util.ArrayList;
import javafx.scene.text.Text;

public class Ranking { // ランキング用オブジェクト
  public static final int numRankers = 5; // ランキングの最大人数
  private ArrayList<Integer> rankers; // ランキングを入れるリスト

  public Ranking(int[] rankers) {
    this.rankers = new ArrayList<>();
    // 配列が最大人数より長いときは余剰分を切り落とす
    for(int i = 0; i < rankers.length && i < numRankers; ++i) {
      this.rankers.add(rankers[i]);
    }
  }

  public int add(int i) { // ランキングへの追加処理
    if(rankers.size() < numRankers || rankers.get(rankers.size()-1) <= i) {
      // 新スコアの挿入先を探す
      // 同じスコアの場合は新スコアが上に来るようにする
      int pos = rankers.size() - 1;
      while (pos >= 0 && rankers.get(pos) <= i) {
        --pos;
      };
      ++pos; // 挿入のために調整
      if(pos < 0) { // リストが空のときの調整
        pos = 0;
      }
      rankers.add(pos,i);

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

  public int[] toIntArray() {
    // ランキングをint配列で返す
    return rankers.stream().mapToInt(i -> i).toArray();
  }

  public Text[] toTextArray() {
    // ランキングをText配列で返す
    // スコアが少ない場合も最大人数分を返す
    Text[] t = new Text[5];
    for(int i = 0; i < numRankers; ++i) {
      if(i < rankers.size()) {
        t[i] = new Text(String.format("%d : %d", i+1, rankers.get(i)));
      } else {
        t[i] = new Text(String.format("%d : ", i+1));
      }
    }
    return t;
  }
}
