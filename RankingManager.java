import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

public class RankingManager { // ランキングとファイルの間を取り持つ
  private static final Path filePath = Paths.get("rankers.dat");

  public static Ranking load() {
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

  public static void write(Ranking r) {
    // ランキングをファイルへ書き込む
    // ランカーの名前はBase64で変換して保存する
    // (そのまま保存するとスペース周りの扱いがめんどいので)
    Base64.Encoder enc = Base64.getEncoder();
    try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(filePath))){
      Arrays.stream(r.toRankerArray())
        .map(e -> String.format("%s %d", enc.encodeToString(e.name.getBytes()), e.score))
        .forEach(pw::println);
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }
}
