import java.nio.file.Paths;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

class FXMLManager { // FXML周りを楽にする
  private static Stage stage;

  public static void setStage(Stage s) {
    // SceneをセットするStageを設定
    FXMLManager.stage = s;
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
