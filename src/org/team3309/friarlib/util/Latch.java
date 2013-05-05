package org.team3309.friarlib.util;

/**
 * This class latches a boolean variable. This class keeps track of the value of
 * a boolean and can tell you when it is changed (only when update is called)
 * 
 * @author vmagro
 * 
 */
public class Latch {

	private boolean lastVal;

	/**
	 * Get the state of the latch after using this new value
	 * 
	 * @param newVal
	 *            the new value of the boolean
	 * @return true if the value is different from the previous value, false if
	 *         it is the same
	 */
	public boolean update(boolean newVal) {
		boolean result = newVal && !lastVal;
		lastVal = newVal;
		return result;
	}

}
