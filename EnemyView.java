import javafx.scene.image.ImageView;

class EnemyView extends ImageView { // 敵表示用クラス
  private boolean underDestruction; // 破壊中
  Enemy enemy;
  EnemyView(Enemy e) {
    super(e);
    this.enemy = e;
    underDestruction = false;
  }

  void detonate() { // 破壊中状態に移行する
    underDestruction = true;
  }

  boolean isUnderDestruction() {
    return underDestruction;
  }
}
