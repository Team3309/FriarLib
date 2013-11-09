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

/**
 * This class latches a boolean variable. This class keeps track of the value of
 * a boolean and can tell you when it is changed (only when update is called)
 *
 * @author vmagro
 *
 */
public class Latch {

	private boolean lastVal = false;

	/**
	 * Create a new Latch with an initial value of false
	 */
	public Latch(){
		lastVal = false;
	}

	/**
	 * Create a new Latch with a specified initial value
	 * @param initialVal the initial value of the Latch
	 */
	public Latch(boolean initialVal){
		lastVal = initialVal;
	}

	/**
	 * Get the state of the latch after using this new value
	 *
	 * @param newVal
	 *            the new value of the boolean
	 * @return true if the value is different from the previous value, false if
	 *         it is the same
	 */
	public boolean update(boolean newVal) {
		boolean result = newVal == !lastVal;
		lastVal = newVal;
		return result;
	}

}
