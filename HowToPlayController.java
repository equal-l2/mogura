import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HowToPlayController implements Initializable {
  @FXML
  VBox desc;

  @Override
  public void initialize(URL url, ResourceBundle rb) {
    TextFlow[] ts = new TextFlow[6];

    Text[] t1 = new Text[3];
    t1[0] = new Text("憎きあの大学を");
    t1[1] = new Text("爆破");
    t1[2] = new Text("しよう！");
    t1[1].setFill(Color.RED);
    ts[0] = new TextFlow(t1);

    Text[] t2 = new Text[3];
    t2[0] = new Text("大学を爆破すると");
    t2[1] = new Text("偏差値が得点");
    t2[2] = new Text("になります。");
    t2[1].setFill(Color.RED);
    ts[1] = new TextFlow(t2);

    Text[] t3 = new Text[4];
    t3[0] = new Text("ただし、訴えられてしまうので");
    t3[1] = new Text("私立大学は爆破しては\n");
    t3[2] = new Text("いけません");
    t3[3] = new Text("。");
    t3[1].setFill(Color.RED);
    t3[2].setFill(Color.RED);
    ts[2] = new TextFlow(t3);

    Text[] t4 = new Text[3];
    t4[0] = new Text("具体的には");
    t4[1] = new Text("偏差値×10の減点");
    t4[2] = new Text("です。");
    t4[1].setFill(Color.RED);
    ts[3] = new TextFlow(t4);

    Text[] t5 = new Text[5];
    t5[0] = new Text("静○大学");
    t5[1] = new Text("か");
    t5[2] = new Text("筑○大学");
    t5[3] = new Text("を爆破すると、テンションが\n");
    t5[4] = new Text("上がって私大も怖くなくなります");
    t5[0].setFill(Color.RED);
    t5[2].setFill(Color.RED);
    ts[4] = new TextFlow(t5);

    Text[] t6 = new Text[4];
    t6[0] = new Text("具体的には、一定時間");
    t6[1] = new Text("触れるだけで爆破できるように\n");
    t6[2] = new Text("なり、私大も爆破できる");
    t6[3] = new Text("ようになります。");
    t6[1].setFill(Color.RED);
    t6[2].setFill(Color.RED);
    ts[5] = new TextFlow(t6);

    for(TextFlow tf : ts) {
      for(Node t : tf.getChildren()) {
        ((Text)t).setFont(Font.font(30));
      }
    }

    desc.getChildren().addAll(ts);

  }

  @FXML
  private void onReturnButtonAction(ActionEvent e) {
    FXMLManager.setSceneFromFXML("assets/fxml/Title.fxml");
  }

}
