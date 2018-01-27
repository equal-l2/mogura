import java.nio.file.Paths;
import java.util.EventObject;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

class FXMLChanger {
  private static Stage s;

  static void setStage(Stage s) {
    FXMLChanger.s = s;
  }

  static void changeTo(String name) {
    try {
      s.setScene(new Scene(FXMLLoader.load(Paths.get(name).toUri().toURL())));
      s.show();
    } catch (Exception e) {
      e.printStackTrace();
      Platform.exit();
      System.exit(1);
    }
  }
}
