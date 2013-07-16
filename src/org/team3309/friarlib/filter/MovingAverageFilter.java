/*
 * Copyright (c) 2013, FRC Team 3309 All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
	private int i = 0;

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
	 * Get a new filtered value based on the input. For a MovingAverageFilter, this is based on the current input and the last n inputs.
	 * @param val the latest measured value
	 * @return a value filtered based on the last n samples
	 */
	public double update(double val) {
		samples[i] = val;
		if(i >= samples.length-1)
			i = 0;
		else
			i++;
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
