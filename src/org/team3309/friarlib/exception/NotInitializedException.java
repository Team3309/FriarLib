package org.team3309.friarlib.exception;

/**
 * This is an exception that we use to show that a subsystem has not been initialized
 * @author Vinnie
 *
 */
public class NotInitializedException extends RuntimeException {

	public NotInitializedException(String msg) {
		super(msg);
	}

}
