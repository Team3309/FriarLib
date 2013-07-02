package org.team3309.friarlib.util;

public class Functions {
	
	/**
	 * Generate a sine function value at the specified moment in time with the specified period and amplitude
	 * @param timeSec time in seconds
	 * @param periodSec period in seconds
	 * @param amplitude amplitude of the function
	 * @return
	 */
	public static double sin(double timeSec, double periodSec, double amplitude){
		return Math.sin(timeSec * 2 * Math.PI / periodSec) * amplitude;
	}
	
	/**
	 * Generate a sine function value at the specified moment in time with the specified period and amplitude
	 * @param timeMs time in milliseconds
	 * @param periodMs period in milliseconds
	 * @param amplitude amplitude of the function
	 * @return
	 */
	public static double sin(long timeMs, int periodMs, double amplitude){
		return sin(timeMs*1000, periodMs*1000, amplitude);
	}
	
	/**
	 * Generate a cosine function value at the specified moment in time with the specified period and amplitude
	 * @param timeSec time in seconds
	 * @param periodSec period in seconds
	 * @param amplitude amplitude of the function
	 * @return
	 */
	public static double cos(double timeSec, double periodSec, double amplitude){
		return Math.cos(timeSec * 2 * Math.PI / periodSec) * amplitude;
	}
	
	/**
	 * Generate a cosine function value at the specified moment in time with the specified period and amplitude
	 * @param timeMs time in milliseconds
	 * @param periodMs period in milliseconds
	 * @param amplitude amplitude of the function
	 * @return
	 */
	public static double cos(long timeMs, int periodMs, double amplitude){
		return cos(timeMs*1000, periodMs*1000, amplitude);
	}


}
