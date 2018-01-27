import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    FXMLChanger.setStage(stage);
    Scene s = new Scene(FXMLLoader.load(getClass().getResource("fxml/Title.fxml")));
    stage.setScene(s);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
