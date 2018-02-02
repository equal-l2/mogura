# Mogura
もぐらたたきゲーム

## ルール
- 敵をクリックで倒す
- スペシャル敵を倒すと、その場の敵を全て破壊し、その後一定時間触れただけで敵が倒せる
- 触れてはいけない敵を倒すと減点(スペシャル状態のときは普通に倒せる)

## 画像
画像は`assets/pic`に入れる。  
画像の点数・属性は`assets/enemies.conf` で設定できる。  
各エントリは次の形式。  
  
`画像名 スコア`  
または  
`画像名 スコア:属性`

## 参考
- http://procongame.hatenablog.com/entry/2017/08/11/134912 (動画をクロマキー加工して連番透過PNGを出力する方法)
- http://java-buddy.blogspot.jp/2013/01/get-width-and-height-of-resized-image.html (リサイズしたノードの大きさ取得)
- https://stackoverflow.com/questions/46570494 (Timelineで画像を切り替える方法)
- https://stackoverflow.com/questions/15013913 (ノードの衝突判定)
- https://stackoverflow.com/a/31519051 (JavaFXアプリケーションの終了方法)
- https://stackoverflow.com/a/41571710 (StringBinding)
- https://teratail.com/questions/90320 (Platform.runLater)
