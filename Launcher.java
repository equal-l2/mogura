import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Launcher extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    // 重いリソースを起動時に読み込んでおく
    // 読み込み自体は各クラスのstaticコンストラクタで行われる
    new Explosion();
    Enemy.getRandomEnemy();

    // タイトル画面の表示
    FXMLManager.setStage(stage);
    FXMLManager.setSceneFromFXML("assets/fxml/Title.fxml");
  }

  public static void main(String[] args) {
    launch(args);
  }

  public static void abort(Exception e) {
    // スタックトレースを出してアプリを終了
    e.printStackTrace();
    Platform.exit();
  }
}
