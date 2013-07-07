package org.team3309.friarlib.filter;

/**
 * This {@link Filter} is a max change filter. It only lets the input
 * change by a specified amount. An example use of this would be applying it to
 * a joystick value, so that the robot response is smoother and doesn't seem as
 * jarring to the drivers.
 * 
 * @author Vinnie
 * 
 */
public class MaxChangeFilter implements Filter {

	private double maxChange = 0;
	private double lastVal = 0;

	/**
	 * Create a new MaxChangeFilter
	 * 
	 * @param maxChange
	 *            the maximum amount of change to allow - in 2013 we used a
	 *            value of .05 for the drive joysticks
	 */
	public MaxChangeFilter(double maxChange) {
		this.maxChange = Math.abs(maxChange);
	}

	/**
	 * Set the maximum amount of change allowed
	 * @param maxChange
	 */
	public void setMaxChange(double maxChange) {
		this.maxChange = Math.abs(maxChange);
	}

	/**
	 * Get the maximum amount of change allowed
	 * @return the max amount of change
	 */
	public double getMaxChange() {
		return maxChange;
	}

	@Override
	public double update(double val) {
		double filteredVal = 0;
		if (val > lastVal + maxChange)
			filteredVal = lastVal + maxChange;
		else if (val < lastVal - maxChange)
			filteredVal = lastVal - maxChange;
		else
			filteredVal = val;

		lastVal = filteredVal;
		return filteredVal;
	}
	
	@Override
	public double get(){
		return lastVal;
	}

}
