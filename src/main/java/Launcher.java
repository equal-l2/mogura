import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends javafx.application.Application {
  private static Stage stage;

  @Override
  public void start(Stage stage) throws Exception {
    // 重いリソースを起動時に読み込んでおく
    // 読み込み自体は各クラスのstaticコンストラクタで行われる
    new Explosion();
    Enemy.getRandomEnemy();

    // フォント読み込み
    javafx.scene.text.Font.loadFont(
        Launcher.getResourceStream("Inconsolata-Regular.ttf"),
        10
    );

    // タイトル画面の表示
    stage.setResizable(false);
    Launcher.stage = stage;
    setSceneFromFXML("Title.fxml");
  }

  public static void main(String[] args) {
    launch(args);
  }

  // スタックトレースを出してアプリを終了
  public static void abort(Exception e) {
    e.printStackTrace();
    javafx.application.Platform.exit();
  }

  // FXMLを読み込んでSceneを切り替える
  public static void setSceneFromFXML(String name) {
    try {
      setScene(new Scene(getFXMLLoader(name).load()));
    } catch (Exception e) {
      Launcher.abort(e);
    }
  }

  // Sceneをsetする
  public static void setScene(Scene s) {
    stage.setScene(s);
    stage.show();
  }

  // 現在のSceneをgetする
  // Sceneにイベントを付けたい時に使う
  public static Scene getCurrentScene() {
    return stage.getScene();
  }

  // FXMLからFXMLLoaderを生成する
  // FXMLファイルは src/main/resources/fxml/ に入っているものとする
  public static FXMLLoader getFXMLLoader(String name) {
    java.net.URL FXMLURL = Launcher.getResourceURL("fxml/"+name);
    try {
      return new FXMLLoader(FXMLURL);
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null; // unreachable
  }

  // src/main/resources 内のファイルのURLを返す
  public static java.net.URL getResourceURL(String path) {
    java.net.URL url = null;
    try {
      url = Launcher.class.getResource(path);
      if (url == null) throw new java.io.FileNotFoundException(path);
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return url;
  }

  // src/main/resources 内のファイルのURIをStringで返す
  public static String getResourceUri(String path) {
    try {
      return Launcher.getResourceURL(path).toURI().toString();
    } catch (Exception e) {
      Launcher.abort(e);
    }
    return null; // unreachable
  }

  // src/main/resources 内のファイルをInputStreamとして返す
  // jarはZIP圧縮されているので、アプリをjarに固めた場合は
  // ファイルを読むにはこれを使って読まないといけない
  public static java.io.InputStream getResourceStream(String path) {
    return Launcher.class.getResourceAsStream(path);
  }
}
