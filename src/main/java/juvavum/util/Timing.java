package juvavum.util;

/**
 * Klasse für Zeitmessungen
 * 
 * @author Martin Schneider
 */
public class Timing {
	private long beginTime, endTime, elapsedTime;

	public void begin() {
		elapsedTime = 0;
		beginTime = System.currentTimeMillis();
	}

	public void end() {
		endTime = System.currentTimeMillis();
		elapsedTime = elapsedTime + (endTime - beginTime);
	}

	public void resume() {
		beginTime = System.currentTimeMillis();
	}

	public double getElapsedTime() {
		return (double) elapsedTime / 1000.0;
	}

	public String getElapsedTimeString() {
		double time = elapsedTime / 1000;
		int seconds = (int) time % 60;
		time /= 60;
		int minutes = (int) time % 60;
		time /= 60;
		int hours = (int) time;
		return hours + " hours " + minutes + " minutes " + seconds
				+ " seconds (" + elapsedTime + " ms)";
	}
}