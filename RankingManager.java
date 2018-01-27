import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

class RankingManager {
  static final Path filePath = Paths.get("rankers.dat");

  static Ranking load() throws IOException {
    if ( !Files.exists(filePath) ) Files.createFile(filePath);
    int[] rankers = Files.lines(filePath).mapToInt(Integer::parseInt).toArray();
    return new Ranking(rankers);
  }

  static void write(Ranking r) throws IOException {
    PrintWriter pw = new PrintWriter(Files.newBufferedWriter(filePath));
    Arrays.stream(r.toIntArray()).forEach(pw::println);
  }
}
