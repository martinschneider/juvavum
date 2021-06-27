package juvavum.analyse;

import java.util.Map;
import java.util.Set;
import java.util.Vector;
import juvavum.util.Timer;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class LCRAMAnalysis implements Analysis {

  private int[] gValues;
  private int h;
  private int w;
  private int n;
  private Timer timer;

  public static void main(String args[]) {
    new LCRAMAnalysis(10, 1);
  }

  public LCRAMAnalysis(int h, int w) {
    this.h = h;
    this.w = w;
    this.n = Math.max(h, w);
    System.out.println("\n" + getGameName());
    timer = new Timer();
    timer.start();
    gValues = new int[n + 1];
    gValues[0] = 0;
    gValues[1] = 0;
    for (int i = 2; i <= n; i++) {
      gValues[i] = -1;
    }
  }

  public void analyse() {
    printResults(grundy(n));
  }

  private int grundy(int n) {
    if (gValues[n] != -1) {
      return gValues[n];
    } else {
      Vector<Integer> values = new Vector<Integer>();
      for (int i = 0; i <= n / 2; i++) {
        int tmp = grundy(i) ^ grundy(n - 2 - i);
        values.add(tmp);
      }
      gValues[n] = mex(values);
      return gValues[n];
    }
  }

  private static int mex(Vector<Integer> values) {
    int i = 0;
    int mex = -1;
    while (mex == -1) {
      boolean found = false;
      for (int j : values) {
        if (j == i) {
          found = true;
          break;
        }
      }
      if (!found) {
        mex = i;
      }
      i++;
    }
    return mex;
  }

  private void printResults(int grundyValue) {
    System.out.print("The g-value of the starting position is " + grundyValue + ". The ");
    if (grundyValue > 0) {
      System.out.print("first ");
    } else {
      System.out.print("second ");
    }
    System.out.println("player can always win.");
    System.out.print("Number of positions");
    System.out.println(": " + fibonacci(n + 1));

    timer.stop();
    String time = timer.getElapsedTimeString();
    System.out.println("Duration: " + time);
  }

  private long fibonacci(int n) {
    final double sqrt5 = Math.sqrt(5);
    final double phi = (1 + sqrt5) / 2;
    return (long) ((Math.pow(phi, n) - Math.pow(-phi, -n)) / sqrt5);
  }

  private String getGameName() {
    return "CRAM[" + h + "x" + w + "]";
  }

  @Override
  public Map<Long, Set<Long>> winningMoves() {
    return null;
  }
}
