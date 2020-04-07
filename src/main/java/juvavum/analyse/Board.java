package juvavum.analyse;

import java.util.BitSet;
import juvavum.graph.SimpleEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

/**
 * Representation of a game board
 * 
 * The game board is represented using a boolean array to optimize for execution speed. Using a
 * {@link BitSet} has been considered but while this would reduce memory consumption it would also
 * slow down the execution and add complexity to the code.
 * (https://stackoverflow.com/questions/605226/boolean-vs-bitset-which-is-more-efficient).
 *
 * @author Martin Schneider, mart.schneider@gmail.com
 */
public class Board {

  private boolean[][] board;

  private int h;

  private int w;

  /**
   * Create an empty board
   *
   * @param w width
   * @param h height
   */
  public Board(int h, int w) {
    if (h * w > 63) {
      throw new IllegalArgumentException("Boards may not have more than 63 fields.");
    }
    this.board = new boolean[w][h];
    this.h = h;
    this.w = w;
    empty();
  }

  /**
   * Creates a board from a a binary representation.
   *
   * <p>
   * e.g: 39 --> 1*2^0 + 1*2^1 + 1*2^2 + 1*2^5--> 1 1 1 0 0 1 0 0 0.
   *
   * @param binary binary representation (as decimal value)
   * @param w width
   * @param h height
   */
  public Board(long binary, int h, int w) {
    this.h = h;
    this.w = w;
    int x = 0;
    int y = 0;
    this.board = new boolean[w][h];
    int j = 0;
    for (j = 0; j < h * w - 1; j++) {
      x = j / w;
      y = j % w;
      if ((binary & (1L << x)) == 0) {
        board[y][x] = false;
      } else {
        board[y][x] = true;
      }
      j++;
    }
  }

  public Board(Graph<Integer, SimpleEdge> graph, int h, int w) {
    this.h = h;
    this.w = w;
    int x = 0;
    int y = 0;
    this.board = new boolean[w][h];
    fill();
    int j = 0;
    for (int i = 0; i < h * w; i++) {
      x = j / w;
      y = j % w;
      if (graph.containsEdge(i, i + 1)) {
        board[y][x] = false;
        board[y + 1][x] = false;
      }
      if (graph.containsEdge(i, i + w)) {
        board[y][x] = false;
        board[y][x + 1] = false;
      }
      j++;
    }
  }

  /** @param b existing Board */
  public Board(Board b) {
    w = b.getWidth();
    h = b.getHeight();
    board = new boolean[w][h];
    for (int i = 1; i <= w; i++) {
      for (int j = 1; j <= h; j++) {
        board[i - 1][j - 1] = b.board[i - 1][j - 1];
      }
    }
  }

  /** Prints board to the console. X ... field set - ... field empty */
  public void output() {
    System.out.println(toString());
  }

  /**
   * z.B.: 1 1 1 0 0 0 0 0 1 --> 1*2^0 + 1*2^1 + 1*2^2 + 1*2^8 = 263.
   *
   * @return binary representation of the board
   */
  public long flatten() {
    long value = 0;
    int x, y = 0;
    for (int i = 0; i < w * h; i++) {
      x = i / w;
      y = i % w;
      if (board[y][x]) {
        value += (1 << i);
      }
    }
    return value;
  }

  /**
   * @param x horizontal coordinate
   * @param y vertical coordinate
   * @return true, if (x,y) is filled false, if free
   */
  public boolean isSet(int x, int y) {
    boolean ret = false;
    try {
      ret = board[x - 1][y - 1];
    } catch (ArrayIndexOutOfBoundsException e) {
      return true;
    }
    return ret;
  }

  /**
   * @param x horizontal coordinate
   * @param y vertical coordinate
   * @return true, if (x,y) is free false, if filled
   */
  public boolean isFree(int x, int y) {
    return !isSet(x, y);
  }

  /**
   * Set the field (x,y).
   *
   * @param x horizontal component
   * @param y vertical component
   */
  public void set(int x, int y) {
    board[x - 1][y - 1] = true;
  }

  /**
   * Empty the field (x,y).
   *
   * @param x horizontal component
   * @param y vertical component
   */
  public void clear(int x, int y) {
    board[x - 1][y - 1] = false;
  }

  /**
   * Set the field (x,y) and convert to normal form.
   *
   * @param x horizontal component
   * @param y vertical component
   */
  public void set2(int x, int y) {
    board[x - 1][y - 1] = true;
    toNormalForm();
  }

  /**
   * Empty the field (x,y) and convert to normal form.
   *
   * @param x horizontal component
   * @param y vertical component
   */
  public void clear2(int x, int y) {
    board[x - 1][y - 1] = false;
    toNormalForm();
  }

  /** @return width */
  public int getWidth() {
    return w;
  }

  /** @return height */
  public int getHeight() {
    return h;
  }

  /** @return true, if the board is empty */
  public boolean isEmpty() {
    for (int i = 1; i <= w; i++) {
      for (int j = 1; j <= h; j++) {
        if (isSet(i, j)) {
          return false;
        }
      }
    }
    return true;
  }

  /** initialize the board (all fields are empty) */
  public void empty() {
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        board[i][j] = false;
      }
    }
  }

  /** initialize the board (all fields are empty) */
  public void fill() {
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        board[i][j] = true;
      }
    }
  }

  /** Convert the board into a normal form (simple Juvavum only) */
  public Board toNormalForm() {
    int z[], s[], bz[], bs[];
    z = new int[h];
    s = new int[w];
    bz = new int[h];
    bs = new int[w];
    for (int i = 1; i <= h; i++) {
      z[i - 1] = 0;
      bz[i - 1] = 0;
      for (int j = 1; j <= w; j++) {
        if (isFree(j, i)) {
          z[i - 1]++;
        } else {
          bz[i - 1] += (1 << (j - 1));
        }
      }
    }
    for (int j = 1; j <= w; j++) {
      s[j - 1] = 0;
      bs[j - 1] = 0;
      for (int i = 1; i <= h; i++) {
        if (isFree(j, i)) {
          s[j - 1]++;
        } else {
          bs[j - 1] += (1 << (i - 1));
        }
      }
    }
    sortRows(z, bz);
    sortColumns(s, bs);
    return this;
  }

  /** sort rows by number of filled fields */
  private void sortRows(int[] z, int[] bz) {
    int tausch = 0;
    boolean help = false;
    int n = z.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = n - 1; j > i; j--) {
        if (z[j - 1] > z[j]) {
          tausch = z[j - 1];
          z[j - 1] = z[j];
          z[j] = tausch;
          for (int k = 0; k < w; k++) {
            help = board[k][j - 1];
            board[k][j - 1] = board[k][j];
            board[k][j] = help;
          }
        }
      }
    }
  }

  /** sort columns by number of filled fields */
  private void sortColumns(int[] s, int[] bs) {
    int tausch = 0;
    boolean help = false;
    int n = s.length;
    for (int i = 0; i < n - 1; i++) {
      for (int j = n - 1; j > i; j--) {
        if (s[j - 1] > s[j]) {
          tausch = s[j - 1];
          s[j - 1] = s[j];
          s[j] = tausch;
          for (int k = 0; k < h; k++) {
            help = board[j - 1][k];
            board[j - 1][k] = board[j][k];
            board[j][k] = help;
          }
        }
      }
    }
  }

  /** flip board vertically */
  public Board fliplr() {
    int dy = board.length;
    int dx = board[0].length;
    boolean help;
    for (int xi = 0; xi < dx; xi++) {
      for (int yi = 0; yi < dy / 2; yi++) {
        help = board[yi][xi];
        board[yi][xi] = board[dy - 1 - yi][xi];
        board[dy - 1 - yi][xi] = help;
      }
    }
    return this;
  }

  /** flip board horizontally */
  public Board flipud() {
    int dy = board.length;
    int dx = board[0].length;
    boolean help;
    for (int xi = 0; xi < dx / 2; xi++) {
      for (int yi = 0; yi < dy; yi++) {
        help = board[yi][xi];
        board[yi][xi] = board[yi][dx - 1 - xi];
        board[yi][dx - 1 - xi] = help;
      }
    }
    return this;
  }

  /** rotate board by 180 degrees */
  public Board rotate180() {
    flipud();
    fliplr();
    return this;
  }

  /** rotate board by 270 degrees (clockwise) */
  public Board rotate270() {
    boolean[][] help = new boolean[w][h];
    for (int i = 1; i <= h; i++) {
      for (int j = 1; j <= w; j++) {
        help[j - 1][w - i] = isSet(i, j);
      }
    }
    board = help;
    return this;
  }

  /** rotate board by 90 degrees (clockwise) */
  public Board rotate90() {
    boolean[][] help = new boolean[w][h];
    for (int i = 1; i <= h; i++) {
      for (int j = 1; j <= w; j++) {
        help[h - j][i - 1] = isSet(i, j);
      }
    }
    board = help;
    return this;
  }

  /** flip diagonally (primary diagonal) */
  public Board flipd1() {
    boolean help;
    for (int i = 1; i < board.length; i++) {
      for (int j = 0; j < i; j++) {
        help = board[i][j];
        board[i][j] = board[j][i];
        board[j][i] = help;
      }
    }
    return this;
  }

  /** flip diagonally (secondary diagonal) */
  public Board flipd2() {
    boolean help;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < w - 1 - i; j++) {
        help = board[i][j];
        board[i][j] = board[h - 1 - j][w - 1 - i];
        board[h - 1 - j][w - 1 - i] = help;
      }
    }
    return this;
  }

  public SimpleGraph<Integer, SimpleEdge> toGraph() {
    SimpleGraph<Integer, SimpleEdge> graph = new SimpleGraph<>(SimpleEdge.class);
    int length = h * w;
    int x, y;
    for (int i = 0; i < length; i++) {
      x = i / w + 1;
      y = i % w + 1;
      if (isFree(y, x) && isFree(y, x + 1)) {
        graph.addVertex(i);
        graph.addVertex(i + w);
        graph.addEdge(i, i + w);
      }
      if (isFree(y, x) && isFree(y + 1, x)) {
        graph.addVertex(i);
        graph.addVertex(i + 1);
        graph.addEdge(i, i + 1);
      }
    }
    return graph;
  }

  /** @return string representation */
  @Override
  public String toString() {
    String ret = "";
    for (int i = 0; i < board[0].length; i++) {
      for (int j = 0; j < board.length; j++) {
        if (board[j][i] == true) {
          ret += ("X ");
        } else {
          ret += ("- ");
        }
      }
      ret += "\n";
    }
    return ret;
  }

  // hash code and equals are optimized for speed, so we only include the binary representation (and
  // ignore height and width because we do not usually compare different-sized boards)
  @Override
  public int hashCode() {
    return Long.valueOf(flatten()).hashCode();
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
    return other.flatten() == this.flatten();
  }
}
