package juvavum.util;

/** @author Martin Schneider, mart.schneider@gmail.com */
public class Timer {
  private long beginTime, endTime, elapsedTime;

  public void start() {
    elapsedTime = 0;
    beginTime = System.currentTimeMillis();
  }

  public void stop() {
    endTime = System.currentTimeMillis();
    elapsedTime = elapsedTime + (endTime - beginTime);
  }

  public double getElapsedTime() {
    return elapsedTime / 1000.0;
  }

  public String getElapsedTimeString() {
    double time = elapsedTime / 1000;
    int seconds = (int) time % 60;
    time /= 60;
    int minutes = (int) time % 60;
    time /= 60;
    int hours = (int) time;
    return hours
        + " hours "
        + minutes
        + " minutes "
        + seconds
        + " seconds ("
        + elapsedTime
        + " ms)";
  }
}
