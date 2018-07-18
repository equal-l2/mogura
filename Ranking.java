import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Ranking { // ランキング
  public static final int numRankers = 5; // ランキングの最大人数
  private static final Path filePath = Paths.get("rankers.dat");
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

  public static Ranking loadFromFile() {
    // ファイルを読み込んでランキングを返す
    try {
      if (!Files.exists(filePath)) {
        // ファイルがない状態で読み込むとエラーになる
        // ので新規生成する
        Files.createFile(filePath);
      }
      // ランカーの名前はBase64で保存されているのでデコードする
      Base64.Decoder dec = Base64.getDecoder();
      Ranking.Ranker[] rankers = Files.lines(filePath)
        .map(s -> s.split(" "))
        .map(ss -> new Ranking.Ranker(new String(dec.decode(ss[0].getBytes())), Integer.parseInt(ss[1])))
        .toArray(Ranking.Ranker[]::new);
      return new Ranking(rankers);
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null;
  }

  public static void writeToFile(Ranking r) {
    // ランキングをファイルへ書き込む
    // ランカーの名前はBase64で変換して保存する
    // (そのまま保存するとスペース周りの扱いが面倒なので)
    Base64.Encoder enc = Base64.getEncoder();
    try (
        BufferedWriter bw = Files.newBufferedWriter(filePath);
        PrintWriter pw = new PrintWriter(bw)
    ){
      Arrays.stream(r.toRankerArray())
        .map(e -> String.format("%s %d", enc.encodeToString(e.name.getBytes()), e.score))
        .forEach(pw::println);
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }
}
