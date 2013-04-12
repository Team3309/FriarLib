package org.team3309.friarlib.filter;

/**
 * This is an implementation of an Infinite Impulse Response Filter. An IIR
 * filter returns a filtered value based on every previous value. This filter
 * has a configurable strength which determines how aggressive the filter is.
 * For more details, see http://en.wikipedia.org/wiki/Infinite_impulse_response
 * or http://www.chiefdelphi.com/forums/showpost.php?p=1132943&postcount=15
 * 
 * @author Vinnie
 * 
 */
public class InfiniteImpulseResponseFilter implements Filter {

	private double previousFilteredValue = 0;

	private double mFilterStrength = 0;

	/**
	 * Create a new InfiniteImpulseResponseFilter.
	 * 
	 * @param strength
	 *            the filter strength. Must be between -1 and 1
	 */
	public InfiniteImpulseResponseFilter(double strength) {
		setFilterStrength(strength);
	}

	/**
	 * Set the initial value to use in the filter. This is not usually
	 * necessary, unless the initial value must be something other than 0. If
	 * the behavior of starting from 0 and (potentially slowly) increasing to
	 * the actual value is not a problem, you can ignore this.
	 * 
	 * @param val
	 *            the initial value for the filter
	 */
	public void setInitialValue(double val) {
		previousFilteredValue = val;
	}

	/**
	 * Set the strength of the filter. When the strength is 0, there is no
	 * filtering. When the strength is 1, the filtered value can never change
	 * from the initial value.
	 * 
	 * @param strength
	 */
	public void setFilterStrength(double strength) {
		if (Math.abs(strength) > 1)
			throw new IllegalArgumentException(
					"Filter strength must be between -1 and 1");
		this.mFilterStrength = strength;
	}

	/**
	 * Get the current filter strength. This method is provided if the user
	 * wants to change the filter strength on the fly and needs to know what the
	 * current value is.
	 * 
	 * @return
	 */
	public double getFilterStrength() {
		return this.mFilterStrength;
	}

	/**
	 * This returns a filtered value based on the input. For an IIR filter, this
	 * is based on every previous value.
	 */
	@Override
	public double update(double val) {
		double newFilteredValue = mFilterStrength * previousFilteredValue
				+ (1 - mFilterStrength) * val;
		previousFilteredValue = newFilteredValue;
		return newFilteredValue;
	}

}
