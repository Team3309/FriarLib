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

import edu.wpi.first.wpilibj.*;
import org.team3309.friarlib.exception.NotInitializedException;

/**
 * This class is a collection of advanced tank drive algorithms. The code in
 * this class is used to make a better tank drive, which is more controllable
 * and more responsive. This tank drive class has the ability to auto-adjust the
 * power to each side of the drivetrain so that it drives straight even if one
 * side is usually slightly faster than the other. As an example, we were using
 * the precursor to this code in Las Vegas and during one of our practice
 * matches, we didn't even notice that a motor wasn't running on one side,
 * because the gyro auto-corrected so that it would still drive straight despite
 * this setback.
 *
 * @author Vinnie
 */
public class TankDrive {

    /**
     * This holds the singleton instance of TankDrive
     */
    private static TankDrive instance;
    /**
     * This is the member variable holding which side the PTO is on
     */
    private PtoSide ptoSide = null;
    /**
     * This is the member variable holding the sequence of shifting necessary to
     * shift into PTO
     */
    private PtoSequence ptoSequence = null;
    private DoubleSolenoid.Value highGearVal = DoubleSolenoid.Value.kReverse;
    private DoubleSolenoid.Value lowGearVal = DoubleSolenoid.Value.kForward;
    private boolean highGearBool = false;
    private boolean lowGearBool = true;
    private boolean straightPidEnabled = false;
    private SpeedController[] leftMotors = null;
    private SpeedController[] rightMotors = null;
    private DoubleSolenoid driveShifterDoubleSolenoid = null;
    private Solenoid driveShifter = null;
    private Solenoid ptoShifter = null;
    private Encoder leftEncoder = null;
    private Encoder rightEncoder = null;
    private PIDController straightPid = null;
    private boolean gyroEnabled = true;
    private GyroConfig gyroConfig = null;
    private double gyroKp = 0.02;
    private boolean isLowGear = false;
    private FriarGyro gyro;
    private double skimGain = .25;

    /**
     * Get the singleton instance of TankDrive
     *
     * @return the singleton instance of TankDrive
     */
    public static TankDrive getInstance() {
        if (instance == null) {
            throw new NotInitializedException(
                    "TankDrive is not initialized.\n Use TankDrive.Builder to construct the object");
        }
        return instance;
    }

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
     * @param throttle the forward speed of the drivetrain (-1,1)
     * @param turn     the turning speed of the drivetrain (-1,1) - this sets the
     *                 desired angular rate of change based on the max angular rate
     *                 of change
     */
    public void drive(double throttle, double turn) {
        if (gyroEnabled) {
            if (Math.abs(throttle) < .1 && Math.abs(turn) < .1) {
                gyroKp = gyroConfig.kPStopped;
            } else if (Math.abs(throttle) < .5) {
                gyroKp = gyroConfig.kPLowSpeed;
            } else if (isLowGear) {
                gyroKp = gyroConfig.kPLowGear;
            } else {
                gyroKp = gyroConfig.kPHighGear;
            }

            double omega = gyro.getAngularVelocity();
            double desiredOmega = turn * gyroConfig.maxAngularRateOfChange;

            turn = (omega - desiredOmega) * gyroKp;
        }

        double t_left = throttle + turn;
        double t_right = throttle - turn;

        double left = t_left + skim(t_right);
        double right = t_right + skim(t_left);

        if (left > 1)
            left = 1;
        else if (left < 1)
            left = -1;
        if (right > 1)
            right = 1;
        else if (right < -1)
            right = -1;

        setLeft(-left);
        setRight(right);
    }

    /**
     * Shift into high gear
     */
    public void highGear() {
        if (driveShifterDoubleSolenoid != null)
            driveShifterDoubleSolenoid.set(highGearVal);
        else
            driveShifter.set(highGearBool);
        gyroKp = gyroConfig.kPHighGear;
        isLowGear = false;
    }

    /**
     * Shift into low gear
     */
    public void lowGear() {
        if (driveShifterDoubleSolenoid != null)
            driveShifterDoubleSolenoid.set(lowGearVal);
        else
            driveShifter.set(lowGearBool);
        gyroKp = gyroConfig.kPLowGear;
        isLowGear = true;
    }

    /**
     * Shift PTO gearbox into neutral and engage the PTO
     */
    public void engagePto() {
        // no point in checking if using a double solenoid or not, because we
        // have to use a double solenoid if we're using pto
        if (ptoShifter != null && ptoSide.val != PtoSide.kNoneVal) {
            if (ptoSequence.val == PtoSequence.kFromLowVal) {
                lowGear();
            } else if (ptoSequence.val == PtoSequence.kFromHighVal) {
                highGear();
            }
            driveShifterDoubleSolenoid.set(DoubleSolenoid.Value.kOff);
            Timer.delay(.1);
            ptoShifter.set(true);
        } else {
            System.err
                    .println("PTO has not been configured for this drivetrain");
        }
    }

    /**
     * Disengage the PTO
     */
    public void disengagePto() {
        if (ptoShifter != null && ptoSide.val != PtoSide.kNoneVal) {
            ptoShifter.set(false);
        } else {
            System.err
                    .println("PTO has not been configured for this drivetrain");
        }
    }

    /**
     * Set the left and right motors to 0
     */
    public void stop() {
        setLeft(0);
        setRight(0);
    }

    /**
     * This is used internally to set the power for the left side of the
     * drivetrain
     *
     * @param val
     */
    private void setLeft(double val) {
        for (int i = 0; i < leftMotors.length; i++) {
            leftMotors[i].set(val);
        }
    }

    /**
     * This is used internally to set the power for the right side of the
     * drivetrain
     *
     * @param val
     */
    private void setRight(double val) {
        for (int i = 0; i < rightMotors.length; i++) {
            rightMotors[i].set(val);
        }
    }

    /**
     * This sets the power on the PTO side
     *
     * @param val
     */
    public void setPto(double val) {
        if (ptoSide.val == PtoSide.kLeftVal) {
            setLeft(val);
        } else if (ptoSide.val == PtoSide.kRightVal) {
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
     * Return the Gyro configuration. This is provided in case someone wants to
     * modify the constants for different modes of operation. Any changes to
     * this object will automatically be reflected in the drive behavior.
     *
     * @return a {@link TankDrive.GyroConfig GyroConfig} object that contains the current
     *         configuration values
     */
    public GyroConfig getGyroConfig() {
        return gyroConfig;
    }

    /**
     * This is used internally to run any code after the object is created
     */
    private void onBuild() {
        if (leftEncoder != null && rightEncoder != null) {
            leftEncoder.start();
            rightEncoder.start();
        } else {
            System.err
                    .println("One or more drive encoders are null, behavior may be unexpected");
        }

		/*
         * StraightPID straight = new StraightPID(); straightPid = new
		 * PIDController(0.001, 0, 0.02, straight, straight);
		 *
		 * straightPid.enable();
		 */
    }

    /**
     * Use this class to create a new TankDrive object
     *
     * @author Vinnie
     */
    public static class Builder {

        private TankDrive drive;

        public Builder() {
            drive = new TankDrive();
        }

        /**
         * Set the Victors for the left side of the drivetrain
         *
         * @param ports an array of ports for the victor motor controllers on the
         *              left side
         */
        public Builder left(int[] ports) {
            drive.leftMotors = new Victor[ports.length];
            for (int i = 0; i < ports.length; i++) {
                drive.leftMotors[i] = new Victor(ports[i]);
            }
            return this;
        }

        /**
         * Set the Victors for the right side of the drivetrain
         *
         * @param ports an array of ports for the victor motor controllers on the
         *              right side
         */
        public Builder right(int[] ports) {
            drive.rightMotors = new Victor[ports.length];
            for (int i = 0; i < ports.length; i++) {
                drive.rightMotors[i] = new Victor(ports[i]);
            }
            return this;
        }

        /**
         * Set the motor controllers for the left side of the drivetrain
         *
         * @param left an array of SpeedControllers that control the left side
         *              motors
         */
        public Builder left(SpeedController[] left) {
            drive.leftMotors = left;
            return this;
        }

        /**
         * Set the motor controllers for the right side of the drivetrain
         *
         * @param right an array of SpeedController that control the right side
         *              motors
         */
        public Builder right(SpeedController[] right) {
            drive.rightMotors = right;
            return this;
        }

        /**
         * Configure the main drive shifters
         *
         * @param forward the forward channel of the DoubleSolenoid
         * @param reverse the reverse channel of the DoubleSolenoid
         */
        public Builder driveShifter(int forward, int reverse) {
            return driveShifter(new DoubleSolenoid(forward, reverse));
        }

        /**
         * Configure the main drive shifters
         *
         * @param driveShifter a DoubleSolenoid that is the drive shifter
         * @return
         */
        public Builder driveShifter(DoubleSolenoid driveShifter) {
            drive.driveShifterDoubleSolenoid = driveShifter;
            return this;
        }

        /**
         * Configure the main drive shifter
         *
         * @param port the channel of the Solenoid on the solenoid module
         * @return
         */
        public Builder driveShifter(int port) {
            driveShifter(new Solenoid(port));
            return this;
        }

        /**
         * Configure the high gear value for a DoubleSolenoid
         *
         * @param val
         * @return
         */
        public Builder highGear(DoubleSolenoid.Value val) {
            drive.highGearVal = val;
            return this;
        }

        /**
         * Configure the high gear value for a single Solenoid
         *
         * @param val
         * @return
         */
        public Builder highGear(boolean val) {
            drive.highGearBool = val;
            return this;
        }

        /**
         * Configure the low gear value for a DoubleSolenoid
         *
         * @param val
         * @return
         */
        public Builder lowGear(DoubleSolenoid.Value val) {
            drive.lowGearVal = val;
            return this;
        }

        /**
         * Configure the low gear value for a single Solenoid
         *
         * @param val
         * @return
         */
        public Builder lowGear(boolean val) {
            drive.lowGearBool = val;
            return this;
        }

        /**
         * Configure the main drive shifter
         *
         * @param driveShifter a Solenoid that is the drive shifter
         * @return
         */
        public Builder driveShifter(Solenoid driveShifter) {
            drive.driveShifter = driveShifter;
            return this;
        }

        /**
         * Configure the PTO shifter
         *
         * @param port the channel on the solenoid module for the PTO shifter
         * @return
         */
        public Builder ptoShifter(int port) {
            return ptoShifter(new Solenoid(port));
        }

        /**
         * Configure the PTO Shifter
         *
         * @param ptoShifter a Solenoid that will shift in/out of the PTO gear
         * @return
         */
        public Builder ptoShifter(Solenoid ptoShifter) {
            drive.ptoShifter = ptoShifter;
            return this;
        }

        /**
         * Set up the left side encoder. Initializes the encoder with k1X
         * counting type
         *
         * @param a the a channel of the encoder
         * @param b the b channel of the encoder
         */
        public Builder leftEncoder(int a, int b) {
            return leftEncoder(new Encoder(a, b, true,
                    CounterBase.EncodingType.k1X));
        }

        /**
         * Set up the left side encoder
         *
         * @param leftEncoder Encoder for the left side of the drivetrain. Highly
         *                    recommended that this be configured with the 1X counting
         *                    type.
         * @return
         */
        public Builder leftEncoder(Encoder leftEncoder) {
            drive.leftEncoder = leftEncoder;
            return this;
        }

        /**
         * Set up the right side encoder
         *
         * @param a the a channel of the encoder
         * @param b the b channel of the encoder
         */
        public Builder rightEncoder(int a, int b) {
            drive.rightEncoder = new Encoder(a, b, false,
                    CounterBase.EncodingType.k1X);
            return this;
        }

        /**
         * Set up the right side encoder
         *
         * @param rightEncoder Encoder for the right side of the drivetrain. Highly
         *                     recommended that this be configured with the 1X counting
         *                     type.
         * @return
         */
        public Builder rightEncoder(Encoder rightEncoder) {
            drive.rightEncoder = rightEncoder;
            return this;
        }

        /**
         * Set up the gyro. The gyro should be located as close to the center as
         * possible.
         *
         * @param port the port for the gyro on the Analog Module
         */
        public Builder gyro(int port) {
            drive.gyro = new FriarGyro(port);
            return this;
        }

        /**
         * Set up the gyro. The gyro should be located as close to the center as
         * possible.
         *
         * @param gyro the drivetrain gyro
         * @return
         */
        public Builder gyro(FriarGyro gyro) {
            drive.gyro = gyro;
            return this;
        }

        /**
         * Set the gain for the skim method that "skims" power off of one side if any drive commands are greater than 1.
         * This is so the controls are still responsive at high speeds.
         */
        public Builder skimGain(double gain) {
            drive.skimGain = gain;
            return this;
        }

        /**
         * Set the side that the PTO is on
         *
         * @param side
         */
        public Builder ptoSide(PtoSide side) {
            drive.ptoSide = side;
            return this;
        }

        public Builder gyroConfig(GyroConfig config) {
            drive.gyroConfig = config;
            return this;
        }

        public Builder ptoSequence(PtoSequence seq) {
            drive.ptoSequence = seq;
            return this;
        }

        public TankDrive build() {
            drive.onBuild();
            TankDrive.instance = drive;
            return drive;
        }
    }

    /**
     * Use this class to configure gyro P constants
     *
     * @author Vinnie
     */
    public static final class GyroConfig {

        /**
         * P value used for gyro P controller when in low gear
         */
        public double kPLowGear = 0.001;
        /**
         * P value used for gyro P controller when joysticks are at stopped
         * position
         */
        public double kPStopped = 0.01;
        /**
         * P value used for gyro P controller when in high gear
         */
        public double kPHighGear = 0.02;
        /**
         * P value used for gyro P controller when driving at low speed (less
         * than .5)
         */
        public double kPLowSpeed = 0.03;
        /**
         * Max angular rate of change that can be commanded by the joystick
         * Units are degrees/second
         */
        public double maxAngularRateOfChange = 720;

        /**
         * Returns a string representation of this GyroConfig. The String
         * contains all of the configuration parameters
         */
        public String toString() {
            String s = "Gyro Configuration\n";
            s += "Low Gear:\t" + kPLowGear + "\n";
            s += "Low Speed:\t" + kPLowSpeed + "\n";
            s += "High Gear:\t" + kPHighGear + "\n";
            s += "Stopped:\t" + kPStopped + "\n";
            s += "Max Speed:\t" + maxAngularRateOfChange;
            return s;
        }
    }

    public static final class PtoSide {

        private static final int kLeftVal = 0;
        private static final int kRightVal = 1;
        private static final int kNoneVal = -1;

        public static final PtoSide kLeft = new PtoSide(kLeftVal);
        public static final PtoSide kRight = new PtoSide(kRightVal);
        public static final PtoSide kNone = new PtoSide(kNoneVal);

        private int val;

        private PtoSide(int val) {
            this.val = val;
        }
    }

    public static final class PtoSequence {

        private static final int kFromLowVal = 0;
        private static final int kFromHighVal = 1;

        public static final PtoSequence kFromLow = new PtoSequence(kFromLowVal);
        public static final PtoSequence kFromHigh = new PtoSequence(
                kFromHighVal);

        private int val;

        private PtoSequence(int val) {
            this.val = val;
        }
    }

    /**
     * This class implements PIDSource and PIDOutput for a straight driving PID
     * controller It does this by returning an average of the left and right
     * side encoder counts
     *
     * @author Vinnie
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

}