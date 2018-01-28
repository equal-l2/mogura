import javafx.scene.image.ImageView;

class EnemyView extends ImageView { // 敵表示用クラス
  private boolean underDestruction; // 破壊中
  public Enemy enemy;

  public EnemyView(Enemy e) {
    super(e);
    this.enemy = e;
    underDestruction = false;
  }

  public void detonate() { // 破壊中状態に移行する
    underDestruction = true;
  }

  public boolean isUnderDestruction() {
    return underDestruction;
  }

  public double getRealWidth() { // リサイズ後の幅
    return getBoundsInParent().getWidth();
  }

  public double getRealHeight() { // リサイズ後の高さ
    return getBoundsInParent().getHeight();
  }

  public boolean collideWith(EnemyView e) { // 相手と衝突しているか
    return e.getBoundsInParent().intersects(getBoundsInParent());
  }
}
