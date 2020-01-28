import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javafx.scene.image.Image;

public class Enemy extends Image { // 敵クラス
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
    try (
        BufferedReader br = Files.newBufferedReader(Paths.get("assets/enemies.conf"))
    ) {
      String line;
      while ((line = br.readLine()) != null) {
        line.trim(); // 行頭の空白を取り除く

        /* コメント削除 */
        final int comment = line.indexOf('#');
        if(comment != -1) {
          line = line.substring(0,comment);
          line.trim();
        }

        final String[] ss = line.split(" "); // スペースで区切って配列へ

        if (ss.length < 2) { // 行に十分な情報がないとき
          System.err.println("Too few elems in line : "+line);
          continue;
        }

        try {
          // 得られた設定から敵を生成
          eList.add(new Enemy(ss[0], ss[1]));
        } catch (IllegalArgumentException e) {
          // ファイル名に問題がある場合
          System.err.println("Invalid URL : "+ss[0]);
        } catch (Exception e) {
          // その他の理由で読めない設定があったらエラーを出す
          System.err.println("Invalid conf line: "+line);
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      Launcher.abort(e);
    }
    enemies = eList.toArray(new Enemy[eList.size()]);
  }

  private Enemy(String fileName, String effect) {
    super(Launcher.toUriString("assets/pic/"+fileName)); // 画像読み込み
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
