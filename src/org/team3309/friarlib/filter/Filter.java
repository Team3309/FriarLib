package org.team3309.friarlib.filter;

/**
 * This interface is used so that all Filters are interchangeable if a subsystem
 * requires a Filter object.
 * 
 * @author Vinnie
 * 
 */
public interface Filter {

	/**
	 * Apply the filter to the given input
	 * 
	 * @param val
	 *            the input
	 * @return the filtered response
	 */
	public double update(double val);

	/**
	 * Get the last value that the filter output
	 * 
	 * @return
	 */
	public double get();

}
