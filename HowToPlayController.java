import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HowToPlayController {
  @FXML
  private VBox desc;

  @FXML
  private void initialize() {
    ArrayList<TextFlow> ts = new ArrayList<>();

    ArrayList<Text> t1 = new ArrayList<>();
    t1.add(new Text("色んな思いを込めてあの大学を"));
    t1.add(new Text("クリックで爆破"));
    t1.add(new Text("しよう！"));
    t1.get(1).setFill(Color.RED);
    ts.add(new TextFlow(t1.toArray(new Text[t1.size()])));

    ArrayList<Text> t2 = new ArrayList<>();
    t2.add(new Text("大学を爆破すると"));
    t2.add(new Text("偏差値が得点"));
    t2.add(new Text("になります。"));
    t2.get(1).setFill(Color.RED);
    ts.add(new TextFlow(t2.toArray(new Text[t2.size()])));

    ArrayList<Text> t3 = new ArrayList<>();
    t3.add(new Text("ただし、訴えられてしまうので"));
    t3.add(new Text("私立大学は爆破しては\nいけません"));
    t3.add(new Text("。具体的には"));
    t3.add(new Text("偏差値×10の減点"));
    t3.add(new Text("です。"));
    t3.get(1).setFill(Color.RED);
    t3.get(3).setFill(Color.RED);
    ts.add(new TextFlow(t3.toArray(new Text[t3.size()])));

    ArrayList<Text> t4 = new ArrayList<>();
    t4.add(new Text("静○大学"));
    t4.add(new Text("か"));
    t4.add(new Text("筑○大学"));
    t4.add(new Text("を爆破すると、調子に乗るので\n私大も怖くなくなります。具体的には"));
    t4.add(new Text("一定時間"));
    t4.add(new Text("触れる\nだけ"));
    t4.add(new Text("で大学を爆破できるようになり、また"));
    t4.add(new Text("私大も爆破\nできる"));
    t4.add(new Text("ようになります。"));
    t4.get(0).setFill(Color.RED);
    t4.get(2).setFill(Color.RED);
    t4.get(5).setFill(Color.RED);
    t4.get(7).setFill(Color.RED);
    ts.add(new TextFlow(t4.toArray(new Text[t4.size()])));

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
