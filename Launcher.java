import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    FXMLManager.setStage(stage);
    FXMLManager.setSceneFromFXML("assets/fxml/Title.fxml");
  }

  public static void main(String[] args) {
    launch(args);
  }

  static void abort(Exception e) {
    e.printStackTrace();
    Platform.exit();
  }
}
