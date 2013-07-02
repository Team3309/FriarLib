package org.team3309.friarlib;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

/**
 * This class is an implementation of GenericHID for an Xbox 360 Controller.
 * @author Vinnie
 */
public class XboxController extends GenericHID{
    
    private static final double DEADBAND = .1;
    private static final double DEADBAND_CUBED = .001;

    // Buttons
    public static final int BUTTON_D_PAD_UP = 5;
    public static final int BUTTON_D_PAD_DOWN = 6;
    public static final int BUTTON_D_PAD_LEFT = 7;
    public static final int BUTTON_D_PAD_RIGHT = 8;
    public static final int BUTTON_START = 8;
    public static final int BUTTON_BACK = 7;
    public static final int BUTTON_LEFT_STICK = 9;
    public static final int BUTTON_RIGHT_STICK = 10;
    public static final int BUTTON_LEFT_BUMPER = 5;
    public static final int BUTTON_RIGHT_BUMPER = 6;
    public static final int BUTTON_X_HOME = 15;
    public static final int BUTTON_A = 1;
    public static final int BUTTON_B = 2;
    public static final int BUTTON_X = 3;
    public static final int BUTTON_Y = 4;
    
    // Axis
    private static final int A_LEFT_X = 1;
    private static final int A_LEFT_Y = 2;
    private static final int A_TRIGGER = 3;
    private static final int A_RIGHT_X = 4;
    private static final int A_RIGHT_Y = 5;
    
    private Joystick mController;

    public XboxController(int controller) {
        mController = new Joystick(controller);
    }

    public boolean getLeftBumper() {
        return mController.getRawButton(BUTTON_LEFT_BUMPER);
    }

    public boolean getRightBumper() {
        return mController.getRawButton(BUTTON_RIGHT_BUMPER);
    }

    public boolean getDPadUp() {
        return mController.getRawButton(BUTTON_D_PAD_UP);
    }

    public boolean getDPadDown() {
        return mController.getRawButton(BUTTON_D_PAD_DOWN);
    }

    public boolean getDPadLeft() {
        return mController.getRawButton(BUTTON_D_PAD_LEFT);
    }

    public boolean getDPadRight() {
        return mController.getRawButton(BUTTON_D_PAD_RIGHT);
    }

    public boolean getStart() {
        return mController.getRawButton(BUTTON_START);
    }

    public boolean getBack() {
        return mController.getRawButton(BUTTON_BACK);
    }

    public boolean getLeftStickPressed() {
        return mController.getRawButton(BUTTON_LEFT_STICK);
    }

    public boolean getRightStickPressed() {
        return mController.getRawButton(BUTTON_RIGHT_STICK);
    }

    public boolean getAButton() {
        return mController.getRawButton(BUTTON_A);
    }

    public boolean getBButton() {
        return mController.getRawButton(BUTTON_B);
    }

    public boolean getXButton() {
        return mController.getRawButton(BUTTON_X);
    }

    public boolean getYButton() {
        return mController.getRawButton(BUTTON_Y);
    }

    public boolean getXboxButton() {
        return mController.getRawButton(BUTTON_X_HOME);
    }

    public double getLeftX() {
        double val = mController.getRawAxis(A_LEFT_X);
        return scaleAxis(val);
    }

    public double getLeftY() {
        double val = mController.getRawAxis(A_LEFT_Y);
        return -scaleAxis(val);
    }

    public double getRightX() {
        double val = mController.getRawAxis(A_RIGHT_X);
        return scaleAxis(val);
    }

    public double getRightY() {
        double val = mController.getRawAxis(A_RIGHT_Y);
        return -scaleAxis(val);
    }

    public double getRightTrigger() {
        return -Math.abs(mController.getRawAxis(A_TRIGGER));
    }

    public double getX(Hand hand) {
        if(hand.equals(Hand.kLeft))
            return getLeftX();
        else
            return getRightX();
    }

    public double getY(Hand hand) {
        if(hand.equals(Hand.kLeft))
            return getLeftY();
        else
            return getRightY();
    }

    public double getZ(Hand hand) {
        if(hand.equals(Hand.kLeft))
            return 0;
        else
            return getRightTrigger();
    }

    public double getTwist() {
        return 0;
    }

    public double getThrottle() {
        return 0;
    }

    public double getRawAxis(int i) {
        return mController.getRawAxis(i);
    }

    public boolean getTrigger(Hand hand) {
        if(hand.equals(Hand.kLeft))
            return getLeftBumper();
        else
            return getRightBumper();
    }

    public boolean getTop(Hand hand) {
        return false;
    }

    public boolean getBumper(Hand hand) {
        if(hand.equals(Hand.kLeft))
            return getLeftBumper();
        else
            return getRightBumper();
    }

    public boolean getRawButton(int i) {
        return mController.getRawButton(i);
    }
    
    private double scaleAxis(double val){
        if(Math.abs(val) < DEADBAND)
            return 0;
        else
            return (val*val*val) - DEADBAND_CUBED;
    }
}