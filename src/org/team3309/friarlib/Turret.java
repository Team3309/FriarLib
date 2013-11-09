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

package org.team3309.friarlib;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * This class contains generic turret control code. A turret controlled by this
 * class must have one motor to control the heading (this is the orientation of
 * the turret horizontally) and the azimuth (the vertical angle of the turret).
 * This class provides functionality to control the heading and azimuth of any
 * turret as long as the PID values for this class are configured properly. If
 * you need help tuning PID, there are many posts on ChiefDelphi about it.
 *
 * @author Vinnie
 *
 */
public class Turret {

	private PIDController azimuthController = null;
	private PIDController headingController = null;

	private SpeedController azimuthMotor, headingMotor;
	private Encoder azimuthEncoder, headingEncoder;

	private double countsPerAzimuthDegree = 1;
	private double countsPerHeadingDegree = 1;

	public Turret(SpeedController azimuthMotor, Encoder azimuthEncoder,
			SpeedController headingMotor, Encoder headingEncoder) {
		this.azimuthMotor = azimuthMotor;
		this.azimuthEncoder = azimuthEncoder;

		this.headingMotor = headingMotor;
		this.headingEncoder = headingEncoder;

		setup();
	}

	/**
	 * This method sets everything up for the turret like PID controllers and
	 * starting encoders
	 */
	private void setup() {
		azimuthEncoder.start();
		AzimuthPID azimuthPid = new AzimuthPID();
		azimuthController = new PIDController(0, 0, 0, azimuthPid, azimuthPid);

		headingEncoder.start();
		HeadingPID headingPid = new HeadingPID();
		headingController = new PIDController(0, 0, 0, headingPid, headingPid);
	}

	/**
	 * Configure the number of encoder counts per degree for the azimuth of the
	 * turret.
	 *
	 * @param counts
	 *            the number of counts
	 */
	public void setAzimuthCountsPerDegree(double counts) {
		countsPerAzimuthDegree = counts;
	}

	/**
	 * Configure the number of encoder counts per degree for the heading of the
	 * turret.
	 *
	 * @param counts
	 */
	public void setHeadingCountsPerDegree(double counts) {
		countsPerHeadingDegree = counts;
	}

	/**
	 * Set the heading of the turret
	 *
	 * @param degrees
	 *            the angle to set the heading to - not relative to the current
	 *            position
	 */
	public void setHeading(double degrees) {
		headingController.setSetpoint(degrees);
	}

	/**
	 * Add or subtract from the heading of the turret
	 *
	 * @param delta
	 *            the number of degrees to change by (may be negative)
	 */
	public void changeHeading(double delta) {
		setHeading(headingController.getSetpoint() + delta);
	}

	/**
	 * Set the azimuth of the turret
	 *
	 * @param degrees
	 *            the angle to set the azimuth to - not relative to current
	 *            position
	 */
	public void setAzimuth(double degrees) {
		headingController.setSetpoint(degrees);
	}

	/**
	 * Add or subtract from the azimuth of the turret
	 *
	 * @param delta
	 *            the number of degrees to change by (may be negative)
	 */
	public void changeAzimuth(double delta) {
		setAzimuth(azimuthController.getSetpoint() + delta);
	}

	/**
	 * Get the azimuth of the turret. This value is relative to the position
	 * where the encoders were last zeroed.
	 *
	 * @return the azimuth in degrees
	 */
	public double getAzimuth() {
		return azimuthEncoder.get() / countsPerAzimuthDegree;
	}

	/**
	 * Get the heading of the turret. This value is relative to the position
	 * where the encoders were last zeroed.
	 *
	 * @return the heading in degrees
	 */
	public double getHeading() {
		return headingEncoder.get() / countsPerHeadingDegree;
	}

	/**
	 * Set the PID constants for the Azimuth PID controller
	 *
	 * @param p
	 * @param i
	 * @param d
	 */
	public void setAzimuthPid(double p, double i, double d) {
		azimuthController.setPID(p, i, d);
	}

	/**
	 * Set the PID constants for the Heading PID controller
	 *
	 * @param p
	 * @param i
	 * @param d
	 */
	public void setHeadingPid(double p, double i, double d) {
		headingController.setPID(p, i, d);
	}

	/**
	 * Reset the encoders for azimuth and heading to 0
	 */
	public void resetEncoders() {
		azimuthEncoder.reset();
		headingEncoder.reset();
	}

	/**
	 * Enable the PID controllers
	 */
	public void enable() {
		azimuthController.enable();
		headingController.enable();
	}

	private class AzimuthPID implements PIDSource, PIDOutput {

		@Override
		public void pidWrite(double output) {
			azimuthMotor.set(output);
		}

		@Override
		public double pidGet() {
			return azimuthEncoder.get() / countsPerAzimuthDegree;
		}

	}

	private class HeadingPID implements PIDSource, PIDOutput {

		@Override
		public void pidWrite(double output) {
			headingMotor.set(output);
		}

		@Override
		public double pidGet() {
			return headingEncoder.get() / countsPerHeadingDegree;
		}

	}

}
