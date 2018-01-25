import javafx.scene.image.ImageView;

class EnemyView extends ImageView {
  boolean underDestruction;
  Enemy enemy;
  EnemyView(Enemy e) {
    super(e);
    this.enemy = e;
    underDestruction = false;
  }
}
