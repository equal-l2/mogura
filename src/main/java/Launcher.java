import java.nio.file.Paths;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Launcher extends Application {
  private static Stage stage;

  @Override
  public void start(Stage stage) throws Exception {
    // 重いリソースを起動時に読み込んでおく
    // 読み込み自体は各クラスのstaticコンストラクタで行われる
    new Explosion();
    Enemy.getRandomEnemy();

    // フォント読み込み
    Font.loadFont("assets/Inconsolata-Regular.ttf",10);

    // タイトル画面の表示
    stage.setResizable(false);
    Launcher.stage = stage;
    setSceneFromFXML("assets/fxml/Title.fxml");
  }

  public static void main(String[] args) {
    launch(args);
  }

  public static void abort(Exception e) {
    // スタックトレースを出してアプリを終了
    e.printStackTrace();
    Platform.exit();
  }

  public static void setSceneFromFXML(String name) {
    // FXMLを読み込んでSceneを切り替える
    try {
      setScene(new Scene(getFXMLLoader(name).load()));
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }

  public static void setScene(Scene s) {
    // Sceneを切り替える
    stage.setScene(s);
    stage.show();
  }

  public static Scene getCurrentScene() {
    // 現在のSceneをgetする
    // Sceneにイベントを付けたい時に使う
    return stage.getScene();
  }

  public static FXMLLoader getFXMLLoader(String name) {
    // FXMLからFXMLLoaderを生成する
    try {
      return new FXMLLoader(Paths.get(name).toUri().toURL());
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null;
  }

  public static String toUriString(String path) {
    return Paths.get(path).toUri().toString();
  }
}
