package juvavum.util;

/**
 * Klasse für Zeitmessungen
 * 
 * @author Martin Schneider
 */
public class Timing {
	private long beginTime, endTime, elapsedTime;

	/**
	 * Startet den Timer
	 */
	public void begin() {
		elapsedTime = 0;
		beginTime = System.currentTimeMillis();
	}

	/**
	 * Stoppt den Timer
	 */
	public void end() {
		endTime = System.currentTimeMillis();
		elapsedTime = elapsedTime + (endTime - beginTime);
	}

	/**
	 * Startet den Timer neu
	 */
	public void resume() {
		beginTime = System.currentTimeMillis();
	}

	/**
	 * @return
	 * 			vergangene Zeit
	 */
	public double getElapsedTime() {
		return (double) elapsedTime / 1000.0;
	}

	/**
	 * @return
	 * 			vergangene Zeit als String in Sekunden, Minuten, Stunden usw.
	 */
	public String getElapsedTimeString() {
		double time = elapsedTime / 1000;
		int seconds = (int) time % 60;
		time /= 60;
		int minutes = (int) time % 60;
		time /= 60;
		int hours = (int) time;
		return hours + " Stunden " + minutes + " Minuten " + seconds
				+ " Sekunden (" + elapsedTime + " ms)";
	}
}