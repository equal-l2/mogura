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
    Font.loadFont(Paths.get("assets/Inconsolata-Regular.ttf").toUri().toURL().toString(),10);

    // タイトル画面の表示
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
    setScene(getSceneFromFXML(name));
  }

  public static void setScene(Scene s) {
    // Sceneを切り替える
    stage.setScene(s);
    stage.show();
  }

  public static Scene getSceneFromFXML(String name) {
    // FXMLからSceneを生成する
    try {
      return new Scene(FXMLLoader.load(Paths.get(name).toUri().toURL()));
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null;
  }

  public static FXMLLoader getFXMLLoader(String name) {
    // FXMLからFXMLLoaderを生成する
    // 外部からコントローラにアクセスするにはこれを使うしかない
    try {
      return new FXMLLoader(Paths.get(name).toUri().toURL());
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null;
  }
}
