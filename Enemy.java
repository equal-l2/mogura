import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.image.Image;

class Enemy extends Image {
  enum TYPE {
    ENEMY, // 通常敵
    SPECIAL, // スペシャル敵
    DONTTOUCH // 触れてはいけない敵
  }
  final int score; // 爆破時のスコア
  final TYPE type; // 敵の属性
  final static Enemy[] enemies;

  static {
    // 設定ファイルを読み込み
    ArrayList<Enemy> eList = new ArrayList<>();
    try {
      BufferedReader bfr = Files.newBufferedReader(Paths.get("enemies.conf"));
      String line;
      while ((line = bfr.readLine()) != null) {
        line.trim(); // 行頭の空白を取り除く
        if (line.charAt(0) == '#') continue; // コメント行は無視
        final String[] ss = line.split(" "); // スペースで区切って配列へ
        try {
          eList.add(new Enemy(ss[0], ss[1])); // 得られた設定から敵を生成
        } catch (Exception e) {
          // 読めない設定があったらエラーを出す
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
    super("pic/"+fileName); // 画像読み込み
    final String[] ss = effect.split(":"); // 敵の情報を配列へ
    score = Integer.parseInt(ss[0]);

    // 敵の属性に合わせて適切な設定をする
    if (ss.length > 1) {
      switch (ss[1]) {
        case "special":
          type = TYPE.SPECIAL;
          break;
        case "donttouch":
          type = TYPE.DONTTOUCH;
          break;
        default:
          // 該当がない場合は通常敵
          type = TYPE.ENEMY;
      }
    } else {
      // 属性設定がなければ通常敵
      type = TYPE.ENEMY;
    }
  }

  static Enemy getRandomEnemy() { // ランダムに敵を選んで返す
    return enemies[(int)(enemies.length*Math.random())];
  }
}
