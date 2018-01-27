import java.nio.file.Paths;
import java.util.EventObject;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

class FXMLChanger {
  static void changeTo(EventObject event, String name) {
    try {
    Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    stage.setScene(new Scene(FXMLLoader.load(Paths.get(name).toUri().toURL())));
    stage.show();
    } catch (Exception e) {
      e.printStackTrace();
      Platform.exit();
      System.exit(1);
    }
  }
}
