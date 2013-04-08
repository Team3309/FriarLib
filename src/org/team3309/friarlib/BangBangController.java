package org.team3309.friarlib;

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

	public BangBangController(SpeedController controller, Counter encoder,
			int countsPerRev) {
		this.motor = controller;
		this.encoder = encoder;
		this.countsPerRev = countsPerRev;
		thread = new Thread(this);
		thread.start();
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
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
