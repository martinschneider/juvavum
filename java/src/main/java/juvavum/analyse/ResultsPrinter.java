package juvavum.analyse;

import juvavum.util.Timer;

/** @author Martin Schneider, mart.schneider@gmail.com */
public abstract class ResultsPrinter {

  protected Game game;
  protected Board b;
  protected boolean misere;
  protected Timer timer;

  public ResultsPrinter(Game game, Board b, boolean misere) {
    this.game = game;
    this.b = b;
    this.misere = misere;
    timer = new Timer();
  }

  protected String getGameName() {
    String gameName = game.name();
    gameName += "[" + b.getHeight() + "x" + b.getWidth();
    if (misere) {
      gameName = gameName + ", misere";
    }
    gameName = gameName + "]";
    return gameName;
  }

  protected void printResults(int grundyValue) {
    System.out.print("The g-value of the starting position is " + grundyValue + ". The ");
    if (grundyValue > 0) {
      System.out.print("first ");
    } else {
      System.out.print("second ");
    }
    System.out.println("player can always win.");
    System.out.print("Number of positions");
    System.out.println(": " + numberOfPositions());

    timer.stop();
    String time = timer.getElapsedTimeString();
    System.out.println("Duration: " + time);
  }

  protected abstract int numberOfPositions();
}
