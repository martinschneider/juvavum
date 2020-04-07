package juvavum.analyse;

import java.util.Random;

public class BoardEqualityTest {
  public static void main(String[] args) {

    int numberOfChecks = 1000000;
    Board[][] boardsToCompare = new Board[numberOfChecks][2];
    int h, w, b1, b2;

    for (int i = 0; i < numberOfChecks; i++) {
      h = new Random().nextInt(6) + 1;
      w = new Random().nextInt(6) + 1;
      b1 = new Random().nextInt(1 << (h * w));
      b2 = new Random().nextInt(1 << (h * w));
      boardsToCompare[i][0] = new Board(b1, h, w);
      boardsToCompare[i][1] = new Board(b2, h, w);

      if ((boardsToCompare[i][0].equals(boardsToCompare[i][1])) != (boardsToCompare[i][0]
          .flatten() == boardsToCompare[i][1].flatten())) {
        boardsToCompare[i][0].output();
        boardsToCompare[i][1].output();
      }
    }
  }
}
