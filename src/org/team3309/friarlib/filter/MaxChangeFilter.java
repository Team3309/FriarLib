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
