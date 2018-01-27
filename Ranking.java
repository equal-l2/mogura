import java.util.ArrayList;
import javafx.scene.text.Text;

class Ranking {
  static final int numRankers = 5;
  private ArrayList<Integer> rankers;

  Ranking(int[] rankers) {
    this.rankers = new ArrayList<>();
    for(int i = 0; i < rankers.length && i < numRankers; ++i) {
      this.rankers.add(rankers[i]);
    }
  }

  int add(int i) {
    if(rankers.size() < numRankers || rankers.get(rankers.size()-1) <= i) {
      int pos = rankers.size() - 1;
      while (pos >= 0 && rankers.get(pos) <= i) {
        --pos;
      };
      ++pos;
      if(pos < 0) {
        pos = 0;
      }
      rankers.add(pos,i);
      if (rankers.size() > numRankers) {
        rankers.remove(rankers.size()-1);
      }
      assert pos < numRankers;
      return pos;
    } else {
      return -1;
    }
  }

  int[] toIntArray() {
    return rankers.stream().mapToInt(i -> i).toArray();
  }

  Text[] toTextArray() {
    Text[] t = new Text[5];
    for(int i = 0; i < numRankers; ++i) {
      if(i < rankers.size()) {
        t[i] = new Text(String.format("%d : %d", i+1, rankers.get(i)));
      } else {
        t[i] = new Text(String.format("%d : ", i+1));
      }
    }
    return t;
  }
}
