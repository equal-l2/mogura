import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.image.Image;

class Enemy extends Image {
  enum TYPE {
    ENEMY,
    SPECIAL,
    DONTTOUCH
  }
  final int score;
  final TYPE type;
  final static Enemy[] enemies;

  static {
    ArrayList<Enemy> eList = new ArrayList<>();
    try {
      BufferedReader bfr = Files.newBufferedReader(Paths.get("enemies.conf"));
      String line;
      while ((line = bfr.readLine()) != null) {
        line.trim();
        if (line.charAt(0) == '#') continue;
        final String[] ss = line.split(" ");
        try {
          eList.add(new Enemy(ss[0], ss[1]));
        } catch (Exception e) {
          System.err.println("Invalid conf line: "+line);
        }
      }
      bfr.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    enemies = eList.toArray(new Enemy[eList.size()]);
  }

  Enemy(String fileName, String effect) {
    super("pic/"+fileName);
    final String[] ss = effect.split(":");
    score = Integer.parseInt(ss[0]);
    if (ss.length > 1) {
      switch (ss[1]) {
        case "special":
          type = TYPE.SPECIAL;
          break;
        case "donttouch":
          type = TYPE.DONTTOUCH;
          break;
        default:
          type = TYPE.ENEMY;
      }
    } else {
      type = TYPE.ENEMY;
    }
  }

  static Enemy getRandomEnemy() {
    return enemies[(int)(enemies.length*Math.random())];
  }
}
