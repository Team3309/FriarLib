package org.team3309.friarlib.exception;

/**
 * This is an exception that we use to show that a subsystem has not been initialized
 * @author Vinnie
 *
 */
public class NotInitializedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotInitializedException(String msg) {
		super(msg);
	}

}
