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

package org.team3309.friarlib.util;

public class Functions {
	
	/**
	 * Generate a sine function value at the specified moment in time with the specified period and amplitude
	 * @param timeSec time in seconds
	 * @param periodSec period in seconds
	 * @param amplitude amplitude of the function
	 * @return the result of the sine function with the specified parameters
	 */
	public static double sin(double timeSec, double periodSec, double amplitude){
		return Math.sin(timeSec * 2 * Math.PI / periodSec) * amplitude;
	}
	
	/**
	 * Generate a sine function value at the specified moment in time with the specified period and amplitude
	 * @param timeMs time in milliseconds
	 * @param periodMs period in milliseconds
	 * @param amplitude amplitude of the function
	 * @return the result of the sine function with the specified parameters
	 */
	public static double sin(long timeMs, int periodMs, double amplitude){
		return sin((double) timeMs*1000, periodMs*1000, amplitude);
	}
	
	/**
	 * Generate a cosine function value at the specified moment in time with the specified period and amplitude
	 * @param timeSec time in seconds
	 * @param periodSec period in seconds
	 * @param amplitude amplitude of the function
	 * @return the result of the cosine function with the specified parameters
	 */
	public static double cos(double timeSec, double periodSec, double amplitude){
		return Math.cos(timeSec * 2 * Math.PI / periodSec) * amplitude;
	}
	
	/**
	 * Generate a cosine function value at the specified moment in time with the specified period and amplitude
	 * @param timeMs time in milliseconds
	 * @param periodMs period in milliseconds
	 * @param amplitude amplitude of the function
	 * @return the result of the cosine function with the specified parameters
	 */
	public static double cos(long timeMs, int periodMs, double amplitude){
		return cos((double) timeMs*1000, periodMs*1000, amplitude);
	}


}
