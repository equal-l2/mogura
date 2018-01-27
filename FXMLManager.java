import java.nio.file.Paths;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

class FXMLManager {
  private static Stage stage;

  static void setStage(Stage s) {
    FXMLManager.stage = s;
  }

  static void setSceneFromFXML(String name) {
    setScene(getSceneFromFXML(name));
  }

  static void setScene(Scene s) {
      stage.setScene(s);
      stage.show();
  }

  static Scene getSceneFromFXML(String name) {
    try {
      return new Scene(FXMLLoader.load(Paths.get(name).toUri().toURL()));
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null;
  }

  static FXMLLoader getFXMLLoader(String name) {
    try {
      return new FXMLLoader(Paths.get(name).toUri().toURL());
    } catch (Exception e) {
      e.printStackTrace();
      Platform.exit();
      System.exit(1);
    }
    return null;
  }
}
