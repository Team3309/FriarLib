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
