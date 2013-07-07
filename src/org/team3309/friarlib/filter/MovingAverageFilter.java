package org.team3309.friarlib.filter;

/**
 * Filters data according to a moving average.
 * 
 * A moving average filter outputs the average of the last n inputs given to the
 * filter. This tends to "smooth" out data given to the filter.
 * 
 * A larger value of n gives an average that "lags" more, but a smaller value is
 * more sensitive to noise.
 * 
 * This class simultaneously takes a data point to be filtered and returns the
 * current moving average using the method Update().
 * 
 * @author Vinnie
 */

public class MovingAverageFilter implements Filter {

	private int nSamples = 0;
	private double[] samples = null;

	/**
	 * Create a new moving average filter
	 * 
	 * @param numSamples
	 *            the number of samples to average
	 */
	public MovingAverageFilter(int numSamples) {
		this(numSamples, 0);
	}

	/**
	 * Create a new moving average filter. Use this constructor if a starting
	 * value of 0 for the average is unacceptable. Normally this wouldn't be an
	 * issue because the 0s would only affect the filter for the first N
	 * samples.
	 * 
	 * @param numSamples
	 *            the number of samples to average
	 * @param defaultVal
	 *            the value that the array is initialized to
	 */
	public MovingAverageFilter(int numSamples, double defaultVal) {
		if (numSamples <= 0) {
			throw new IllegalArgumentException(
					"MovingAverageFilter: numSamples must be greater than 0");
		}
		this.nSamples = numSamples;
		samples = new double[nSamples];
		for (int i = 0; i < samples.length; i++) {
			samples[i] = defaultVal;
		}
	}

	@Override
	/**
	 * Get a new filtered value
	 * @param val the latest measured value
	 * @return a value filtered based on the last n samples
	 */
	public double update(double val) {
		for (int i = 0; i < samples.length - 1; i++) {
			samples[i] = samples[i + 1];
		}
		samples[samples.length - 1] = val;
		
		return get();
	}
	
	@Override
	public double get(){
		double sum = 0;
		for (int i = 0; i < samples.length; i++) {
			sum += samples[i];
		}
		return sum / samples.length;
	}

}
