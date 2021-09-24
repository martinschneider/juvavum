package juvavum.analyse;

import juvavum.graph.SimpleEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

/**
 * Representation of a board as a bitset.
 *
 * <p>The value of a board is stored in a long. Each bit is set if the field is filled and 0 if
 * empty. This supports boards with up to 63 fields.
 *
 * @author Martin Schneider, mart.schneider@gmail.com
 */
public class Board {

  long val;

  int h;

  int w;

  public Board() {}

  public Board(int h, int w) {
    if (h * w > 63) {
      throw new IllegalArgumentException("Boards cannot have more than 63 fields.");
    }
    this.h = h;
    this.w = w;
  }

  Board(int h, int w, long val) {
    this.h = h;
    this.w = w;
    this.val = val;
  }

  public Board(Board b) {
    this.h = b.h;
    this.w = b.w;
    this.val = b.val;
  }

  private int get(long val, int x, int y) {
    return (int) (val >> (y * w + x) & 1);
  }

  private int get(int x, int y) {
    return get(val, x, y);
  }

  private long set(long val, int y, int x, long newVal) {
    long mask = 1L << (y * w + x);
    return (val & ~mask) | ((newVal << (y * w + x)) & mask);
  }

  public void set(int y, int x, long newVal) {
    val = set(val, y, x, newVal);
  }

  public boolean isFree(int x, int y) {
    return get(x, y) == 0;
  }

  public boolean isSet(int x, int y) {
    return get(x, y) == 1;
  }

  public Board set(int x, int y) {
    val |= 1L << y * w + x;
    return this;
  }

  public Board putShape(long shape, int x, int y) {
    val |= shape << y * w + x;
    return this;
  }

  public Board clear(int x, int y) {
    val &= ~(1L << y * w + x);
    return this;
  }

  public void swap(int x1, int y1, int x2, int y2) {
    swap(y1 * w + x1, y2 * w + x2);
  }

  private void swap(int p1, int p2) {
    long xor = ((val >> p1) ^ (val >> p2)) & 1;
    val ^= ((xor << p1) | (xor << p2));
  }

  void fill() {
    val = (long) Math.pow(2, h * w) - 1;
  }

  public boolean isEmpty() {
    return val == 0;
  }

  /** @return the board converted to normal form (Juvavum only) */
  public Board normalize() {
    sortRows(rowCounts());
    sortColumns(colCounts());
    return this;
  }

  int[] rowCounts() {
    int rows[] = new int[h];
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        if (get(i, j) == 0) {
          rows[j]++;
        }
      }
    }
    return rows;
  }

  int[] colCounts() {
    int cols[] = new int[w];
    for (int i = 0; i < w; i++) {
      for (int j = 0; j < h; j++) {
        if (get(i, j) == 0) {
          cols[i]++;
        }
      }
    }
    return cols;
  }

  private void sortRows(int[] row) {
    int help = 0;
    int n = row.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = n - 1; j > i; j--) {
        if (row[j - 1] > row[j]) {
          help = row[j - 1];
          row[j - 1] = row[j];
          row[j] = help;
          for (int k = 0; k < w; k++) {
            swap(k, j - 1, k, j);
          }
        }
      }
    }
  }

  /** sort columns by number of empty fields */
  private void sortColumns(int[] col) {
    int help = 0;
    int n = col.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = n - 1; j > i; j--) {
        if (col[j - 1] > col[j]) {
          help = col[j - 1];
          col[j - 1] = col[j];
          col[j] = help;
          for (int k = 0; k < h; k++) {
            swap(j - 1, k, j, k);
          }
        }
      }
    }
  }

  public Board fliplr() {
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w / 2; i++) {
        swap(i, j, w - 1 - i, j);
      }
    }
    return this;
  }

  public Board flipud() {
    for (int j = 0; j < h / 2; j++) {
      for (int i = 0; i < w; i++) {
        swap(i, j, i, h - 1 - j);
      }
    }
    return this;
  }

  public Board rotate180() {
    flipud();
    fliplr();
    return this;
  }

  public Board rotate270() {
    long tmp = 0;
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        tmp = set(tmp, w - i - 1, j, get(i, j));
      }
    }
    val = tmp;
    return this;
  }

  public Board rotate90() {
    long tmp = 0;
    for (int j = 0; j < h; j++) {
      for (int i = 0; i < w; i++) {
        tmp = set(tmp, i, h - j - 1, get(i, j));
      }
    }
    val = tmp;
    return this;
  }

  public Board flipd1() {
    for (int i = 1; i < h; i++) {
      for (int j = 0; j < i; j++) {
        swap(i, j, j, i);
      }
    }
    return this;
  }

  public Board flipd2() {
    for (int i = 0; i < h; i++) {
      for (int j = 0; j < w - 1 - i; j++) {
        swap(i, j, h - 1 - j, w - 1 - i);
      }
    }
    return this;
  }

  public Graph<Integer, SimpleEdge> toGraph() {
    Graph<Integer, SimpleEdge> graph = new SimpleGraph<>(SimpleEdge.class);
    int length = h * w;
    int x, y;
    for (int i = 0; i < length; i++) {
      x = i / w;
      y = i % w;
      if (i % w != w - 1 && isFree(y, x) && isFree(y + 1, x)) {
        graph.addVertex(i);
        graph.addVertex(i + 1);
        graph.addEdge(i, i + 1);
      }
      if (i < w * (h - 1) && isFree(y, x) && isFree(y, x + 1)) {
        graph.addVertex(i);
        graph.addVertex(i + w);
        graph.addEdge(i, i + w);
      }
    }
    return graph;
  }

  @Override
  public String toString() {
    String ret = "";
    for (int i = 0; i < h * w; i++) {
      if (((val >> i) & 1) == 1) {
        ret += ("X ");
      } else {
        ret += ("- ");
      }
      if (i % w == w - 1) {
        ret += "\n";
      }
    }
    return ret;
  }

  // hash code and equals are optimized for speed, so we only include the binary
  // representation (and
  // ignore height and width because we do not usually compare different-sized
  // boards)
  @Override
  public int hashCode() {
    return Long.valueOf(val).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Board other = (Board) obj;
    return other.val == val;
  }
}
