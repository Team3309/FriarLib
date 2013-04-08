package org.team3309.friarlib;

import org.team3309.friarlib.exception.NotInitializedException;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;

/**
 * 
 * @author Vinnie
 */
public class TankDrive {

	/**
	 * No PTO
	 */
	public static final int PTO_NONE = -1;
	
	/**
	 * PTO is on the left side
	 */
	public static final int PTO_SIDE_LEFT = 0;
	
	/**
	 * PTO is on the right side
	 */
	public static final int PTO_SIDE_RIGHT = 1;
	
	/**
	 * This is the member variable holding which side the PTO is on
	 */
	private int ptoSide = -1;
	

	/**
	 * Must be in low gear to shift into PTO
	 */
	public static final int LOW_TO_PTO = 0;
	
	/**
	 * Must be in high gear to shift into PTO
	 */
	public static final int HIGH_TO_PTO = 1;
	
	/**
	 * This is the member variable holding which side the PTO is on
	 */
	private int ptoShifting = -1;

	/**
	 * This holds the singleton instance of TankDrive
	 */
	private static TankDrive instance;

	/**
	 * Get the singleton instance of TankDrive
	 * @return the singleton instance of TankDrive
	 */
	public static TankDrive getInstance() {
		if (instance == null) {
			throw new NotInitializedException(
					"TankDrive is not initialized.\n Use TankDrive.Builder to construct the object");
		}
		return instance;
	}

	private boolean straightPidEnabled = false;

	/**
	 * This class implements PIDSource and PIDOutput for a straight driving PID controller
	 * It does this by returning an average of the left and right side encoder counts
	 * @author Vinnie
	 *
	 */
	private class StraightPID implements PIDSource, PIDOutput {

		public double pidGet() {
			return (leftEncoder.get() + rightEncoder.get()) / 2;
		}

		public void pidWrite(double d) {
			if (straightPidEnabled) {
				drive(d, 0);
			}
		}
	}

	
	private Victor[] leftVictors = null;
	private Victor[] rightVictors = null;

	private DoubleSolenoid driveShifter = null;

	private Solenoid ptoShifter = null;

	private Encoder leftEncoder = null;
	private Encoder rightEncoder = null;

	private PIDController straightPid = null;

	private boolean gyroEnabled = true;
	private double GYRO_KP_LOW_GEAR = .001;
	private double GYRO_KP_STOPPED = .01;
	private double GYRO_KP_LOW_SPEED = .03;
	private double GYRO_KP_HIGH_GEAR = .02;
	private double gyroKp = GYRO_KP_HIGH_GEAR;

	private boolean isLowGear = false;

	private SuperGyro gyro;

	/**
	 * Max angular rate of change commandable by the joystick
	 */
	private double MAX_ANGULAR_RATE_OF_CHANGE = 720;
	
	private double skimGain = .25;

	/**
	 * This is used internally to make it easier to turn when driving at high
	 * speeds
	 * 
	 * @param v
	 * @return
	 */
	private double skim(double v) {
		// gain determines how much to skim off the top
		if (v > 1.0) {
			return -((v - 1.0) * skimGain);
		} else if (v < -1.0) {
			return -((v + 1.0) * skimGain);
		}
		return 0;
	}

	/**
	 * This method commands the drivetrain to drive
	 * 
	 * @param throttle
	 *            the forward speed of the drivetrain (-1,1)
	 * @param turn
	 *            the turning speed of the drivetrain (-1,1) - this sets the
	 *            desired angular rate of change based on the max angular rate
	 *            of change
	 */
	public void drive(double throttle, double turn) {
		throttle = -throttle; // flip throttle so that a positive value will
								// make the robot drive forward

		if (gyroEnabled) {
			if (Math.abs(throttle) < .1 && Math.abs(turn) < .1) // do nothing
																// when there is
																// no driving
																// commands - do
																// this to
																// prevent the
																// waggle -
																// James
			{
				gyroKp = GYRO_KP_STOPPED;
			} else if (Math.abs(throttle) < .5) {
				gyroKp = GYRO_KP_LOW_SPEED;
			}
			if (isLowGear) {
				gyroKp = GYRO_KP_LOW_GEAR;
			} else {
				gyroKp = GYRO_KP_HIGH_GEAR;
			}

			double omega = gyro.getAngularRateOfChange();
			double desiredOmega = turn * MAX_ANGULAR_RATE_OF_CHANGE;

			turn = (omega - desiredOmega) * gyroKp;
		} else
			turn = -turn;

		double t_left = throttle + turn;
		double t_right = throttle - turn;

		double left = t_left + skim(t_right);
		double right = t_right + skim(t_left);

		setLeft(-left);
		setRight(right);
	}

	/**
	 * Shift into high gear
	 */
	public void highGear() {
		driveShifter.set(DoubleSolenoid.Value.kReverse);
		gyroKp = GYRO_KP_HIGH_GEAR;
		isLowGear = false;
	}

	/**
	 * Shift into low gear
	 */
	public void lowGear() {
		driveShifter.set(DoubleSolenoid.Value.kForward);
		gyroKp = GYRO_KP_LOW_GEAR;
		isLowGear = true;
	}

	/**
	 * Shift PTO gearbox into neutral and engage the PTO
	 */
	public void engagePto() {
		if (ptoShifter != null && ptoSide != PTO_NONE) {
			if (ptoShifting == LOW_TO_PTO) {
				lowGear();
			} else if (ptoShifting == HIGH_TO_PTO) {
				highGear();
			}
			driveShifter.set(DoubleSolenoid.Value.kOff);
			Timer.delay(.1);
			ptoShifter.set(true);
		} else {
			System.out
					.println("PTO has not been configured for this drivetrain");
		}
	}

	/**
	 * Disengage the PTO
	 */
	public void disengagePto() {
		if (ptoShifter != null && ptoSide != PTO_NONE) {
			ptoShifter.set(false);
		} else {
			System.out
					.println("PTO has not been configured for this drivetrain");
		}
	}

	/**
	 * Calls {@link #drive(double, double) drive} with 0,0
	 */
	public void stop() {
		drive(0, 0);
	}

	/**
	 * This is used internally to set the power for the left side of the
	 * drivetrain
	 * 
	 * @param val
	 */
	private void setLeft(double val) {
		for (int i = 0; i < leftVictors.length; i++) {
			leftVictors[i].set(val);
		}
	}

	/**
	 * This is used internally to set the power for the right side of the
	 * drivetrain
	 * 
	 * @param val
	 */
	private void setRight(double val) {
		for (int i = 0; i < rightVictors.length; i++) {
			rightVictors[i].set(val);
		}
	}

	/**
	 * This sets the power on the PTO side
	 * 
	 * @param val
	 */
	public void setPto(double val) {
		if (ptoSide == PTO_SIDE_LEFT) {
			setLeft(val);
		} else if (ptoSide == PTO_SIDE_RIGHT) {
			setRight(val);
		} else {
			System.out
					.println("PTO has not been configured for this drivetrain");
		}
	}

	/**
	 * Reset and recalibrate the gyro
	 */
	public void resetGyro() {
		gyro.reset();
	}

	/**
	 * Reset the encoders to 0
	 */
	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	/**
	 * Disable the drive-straight pid
	 */
	public void disableStraightPid() {
		straightPid.disable();
		straightPidEnabled = false;
	}

	/**
	 * Enable the drive-straight pid
	 */
	public void enableStraightPid() {
		straightPid.enable();
		straightPidEnabled = true;
	}

	/**
	 * Drive straight for an amount of counts
	 * 
	 * @param counts
	 */
	public void driveStraight(int counts) {
		straightPid.setSetpoint(counts);
	}

	/**
	 * Enable gyro-feedback for driving straight and in smooth arcs
	 */
	public void enableGyro() {
		gyroEnabled = true;
		System.out.println("gyro enabled");
	}

	/**
	 * Disable gyro-feedback for driving straight and in smooth arcs
	 */
	public void disableGyro() {
		gyroEnabled = false;
		System.out.println("gyro disabled");
	}

	/**
	 * This is used internally to run any code after the object is created
	 */
	private void onBuild() {
		leftEncoder.start();
		rightEncoder.start();

		StraightPID straight = new StraightPID();
		straightPid = new PIDController(0.001, 0, 0.02, straight, straight);

		straightPid.enable();
	}

	/**
	 * Use this class to create a new TankDrive object
	 * 
	 * @author Vinnie
	 * 
	 */
	public static class Builder {

		private TankDrive drive;

		public Builder() {
			drive = new TankDrive();
		}

		/**
		 * Set the Victors for the left side of the drivetrain
		 * @param ports an array of ports for the victor motor controllers on the left side
		 */
		public Builder left(int[] ports) {
			drive.leftVictors = new Victor[ports.length];
			for (int i = 0; i < ports.length; i++) {
				drive.leftVictors[i] = new Victor(ports[i]);
			}
			return this;
		}

		/**
		 * Set the Victors for the right side of the drivetrain
		 * @param ports an array of ports for the victor motor controllers on the right side
		 */
		public Builder right(int[] ports) {
			drive.rightVictors = new Victor[ports.length];
			for (int i = 0; i < ports.length; i++) {
				drive.rightVictors[i] = new Victor(ports[i]);
			}
			return this;
		}

		/**
		 * Configure the main drive shifters
		 * @param forward the forward channel of the DoubleSolenoid
		 * @param reverse the reverse channel of the DoubleSolenoid
		 */
		public Builder driveShifter(int forward, int reverse) {
			drive.driveShifter = new DoubleSolenoid(forward, reverse);
			return this;
		}

		/**
		 * Configure the PTO shifter
		 * @param port the channel on the solenoid module for the PTO shifter
		 * @return
		 */
		public Builder ptoShifter(int port) {
			drive.ptoShifter = new Solenoid(port);
			return this;
		}

		/**
		 * Set up the left side encoder
		 * @param a the a channel of the encoder
		 * @param b the b channel of the encoder
		 */
		public Builder leftEncoder(int a, int b) {
			drive.leftEncoder = new Encoder(a, b, true,
					CounterBase.EncodingType.k1X);
			return this;
		}

		/**
		 * Set up the right side encoder
		 * @param a the a channel of the encoder
		 * @param b the b channel of the encoder
		 */
		public Builder rightEncoder(int a, int b) {
			drive.rightEncoder = new Encoder(a, b, false,
					CounterBase.EncodingType.k1X);
			return this;
		}

		/**
		 * Configure the gyro
		 * @param port the port for the gyro on the Analog Module
		 */
		public Builder gyro(int port) {
			drive.gyro = new SuperGyro(port);
			return this;
		}

		/**
		 * Set the side that the PTO is on
		 * @param side the side of the PTO
		 * 	either @link{TankDrive#PTO_SIDE_LEFT PTO_SIDE_LEFT} or @link{TankDrive#PTO_SIDE_RIGHT PTO_SIDE_RIGHT}
		 */
		public Builder pto(int side) {
			drive.ptoSide = side;
			return this;
		}

		public Builder gyroConfig(GyroConfig config) {
			drive.GYRO_KP_HIGH_GEAR = config.kPHighGear;
			drive.GYRO_KP_LOW_GEAR = config.kPLowGear;
			drive.GYRO_KP_LOW_SPEED = config.kPLowSpeed;
			drive.GYRO_KP_STOPPED = config.kPStopped;
			drive.MAX_ANGULAR_RATE_OF_CHANGE = config.maxAngularRateOfChange;
			return this;
		}

		public Builder configurePto(int val) {
			drive.ptoShifting = val;
			return this;
		}

		public TankDrive build() {
			drive.onBuild();
			return drive;
		}
	}

	/**
	 * Use this class to configure gyro P constants
	 * @author Vinnie
	 *
	 */
	public static final class GyroConfig {

		/**
		 * P value used for gyro P controller when in low gear
		 */
		public double kPLowGear = 0.001;

		/**
		 * P value used for gyro P controller when joysticks are at
		 * stopped position
		 */
		public double kPStopped = 0.01;

		/**
		 * P value used for gyro P controller when in high gear
		 */
		public double kPHighGear = 0.02;

		/**
		 * P value used for gyro P controller when driving at low
		 * speed (less than .5)
		 */
		public double kPLowSpeed = 0.03;

		/**
		 * Max angular rate of change that can be commanded by the
		 * joystick Units are degrees/second
		 */
		public double maxAngularRateOfChange = 720;

	}

}