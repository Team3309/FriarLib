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

package org.team3309.friarlib.motors;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * This class contains code to control the speed of a wheel using bang-bang
 * control. Bang-bang only works if the motor controller is jumpered for coast
 * mode instead of brake mode. This should be the defualt for Victors. Do not
 * use bang-bang with a Jaguar! Only Talons and Victors are supported.
 * 
 * @author Vinnie
 * 
 */
public class BangBangController implements Runnable {

	private Thread thread = null;
	private SpeedController motor = null;
	private Counter encoder = null;
	private int countsPerRev = 0;

	private double targetRpm = 0;

	private volatile boolean enabled = false;

	/**
	 * Create a new bang-bang motor controller.
	 * @param controller the underlying speed controller (a Victor or Talon, no Jaguars)
	 * @param encoder a Counter object for the speed sensor (usually a KOP photosensor)
	 * @param countsPerRev how many counts the encoder reads per revolution
	 */
	public BangBangController(SpeedController controller, Counter encoder,
			int countsPerRev) {
		this.motor = controller;
		this.encoder = encoder;
		this.countsPerRev = countsPerRev;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Enable the bang-bang controller.
	 * When enabled, the controller will run the motor to reach the desired rpm
	 */
	public void enable() {
		enabled = true;
	}

	/**
	 * Disable the bang-bang controller.
	 * When disabled, the motor is constantly run at 0.
	 */
	public void disable() {
		enabled = false;
	}
	
	/**
	 * Set the target speed of the wheel
	 * @param rpm the rpm of the wheel, this is independent of the number of counts per revolution
	 */
	public void setTargetRpm(double rpm){
		this.targetRpm = rpm;
	}

	/**
	 * This variable is used to prevent a few reads of infinity when the wheel
	 * isn't spinning fast enough
	 */
	private int infinityCounts = 0;
	private double lastSpeed = 0;

	
	@Override
	public void run() {
		while (true) {
			if (enabled) {
				try {
					double period = encoder.getPeriod();
					double speed = (60 / period) / countsPerRev;
					// do this so that we use the last speed if the loop is
					// running
					// too fast and didn't get a period
					if (period == Double.POSITIVE_INFINITY
							&& infinityCounts <= 10) {
						speed = lastSpeed;
						infinityCounts++;
					} else if (period == Double.POSITIVE_INFINITY
							&& infinityCounts > 10) {
						speed = 0;
						infinityCounts++;
					} else {
						infinityCounts = 0;
					}

					lastSpeed = speed;

					if (speed < targetRpm)
						motor.set(1);
					else if (speed > targetRpm)
						motor.set(0);
					else if (targetRpm == 0)
						motor.set(0);

					Thread.sleep(20);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
