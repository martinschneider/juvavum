package juvavum.analyse;

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author Martin Schneider
 */
public class LinearCramAnalyse {

	private static int[] gValues;
	
	private static int getGrundy(int n) {
		if (gValues[n] != -1)
			return gValues[n];
		else {
			Vector<Integer> values = new Vector<Integer>();
			for (int i = 0; i <= n - 2; i++) {
				int tmp = getGrundy(i) ^ getGrundy(n - 2 - i);
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
			for (Enumeration<Integer> e1 = values.elements(); e1.hasMoreElements();) {
				if (((Integer) e1.nextElement()).intValue() == i) {
					found = true;
					break;
				}
			}
			if (!found)
				mex = i;
			i++;
		}
		return mex;
	}

	public static void main(String args[]) {
		gValues = new int[1001];
		gValues[0] = 0;
		gValues[1] = 0;
		for (int i = 2; i <= 1000; i++)
			gValues[i] = -1;
		for (int i = 0; i <= 1000; i++)
			System.out.println("g(CRAM(1," + i + "))=" + getGrundy(i));
	}
}