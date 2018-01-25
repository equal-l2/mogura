import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    Scene s = new Scene(FXMLLoader.load(getClass().getResource("Mogura.fxml")));
    primaryStage.setScene(s);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
