import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    FXMLManager.setStage(stage);
    Scene s = new Scene(FXMLLoader.load(getClass().getResource("fxml/Title.fxml")));
    stage.setScene(s);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  static void abort(Exception e) {
    e.printStackTrace();
    Platform.exit();
  }
}
