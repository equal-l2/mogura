import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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
      int[] rankers = Files.lines(filePath).mapToInt(Integer::parseInt).toArray();
      return new Ranking(rankers);
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null;
  }

  public static void write(Ranking r) {
    // ランキングをファイルへ書き込む
    try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(filePath))){
      Arrays.stream(r.toIntArray()).forEach(pw::println);
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }
}
