package org.team3309.friarlib;

import edu.wpi.first.wpilibj.AccumulatorResult;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author vmagr_000
 */
public class FriarGyro {
    
    private AnalogChannel channel = null;
    private double voltageOffset = 2.5;
    
    //this stuff copied from WPILib gyro
    private static final int kOversampleBits = 10;
    private static final int kAverageBits = 0;
    private static final double kSamplesPerSecond = 50.0;
    private static final double kCalibrationSampleTime = 5.0;
    private static final double kVoltsPerDegreePerSecond = 0.007; //7mV per degree per sec
    private AccumulatorResult result;
    
    public FriarGyro(int port){
        channel = new AnalogChannel(port);
        
        initGyro();
    }
    
     /**
     * Initialize the gyro.
     * Calibrate the gyro by running for a number of samples and computing the center value for this
     * part. Then use the center value as the Accumulator center value for subsequent measurements.
     * It's important to make sure that the robot is not moving while the centering calculations are
     * in progress, this is typically done when the robot is first turned on while it's sitting at
     * rest before the competition starts.
     */
    private void initGyro() {
        result = new AccumulatorResult();
        
        channel.setAverageBits(kAverageBits);
        channel.setOversampleBits(kOversampleBits);
        double sampleRate = kSamplesPerSecond * (1 << (kAverageBits + kOversampleBits));
        channel.getModule().setSampleRate(sampleRate);

        Timer.delay(1.0);
        channel.initAccumulator();

        Timer.delay(kCalibrationSampleTime);

        channel.getAccumulatorOutput(result);

        int center = (int) ((double)result.value / (double)result.count + .5);

        voltageOffset = ((double)result.value / (double)result.count) - (double)center;

        channel.setAccumulatorCenter(center);

        channel.setAccumulatorDeadband(0); ///< TODO: compute / parameterize this
        channel.resetAccumulator();
    }
    
    /**
     * Get the rate of change of the angle
     * @return rate of change in degrees/second
     */
    @Deprecated
    public double getAngularRateOfChange(){
        double rate = (channel.getVoltage() - voltageOffset) / kVoltsPerDegreePerSecond; 
        return rate;
    }
    
    /**
     * Get the rate of change of the angle
     * @return rate of change in degrees/second
     */
    public double getAngularVelocity(){
    	double rate = (channel.getVoltage() - voltageOffset) / kVoltsPerDegreePerSecond; 
        return rate;
    }
    
    /**
     * Return the actual angle in degrees that the robot is currently facing.
     *
     * The angle is based on the current accumulator value corrected by the oversampling rate, the
     * gyro type and the A/D calibration values.
     * The angle is continuous, that is can go beyond 360 degrees. This make algorithms that wouldn't
     * want to see a discontinuity in the gyro output as it sweeps past 0 on the second time around.
     *
     * @return the current heading of the robot in degrees. This heading is based on integration
     * of the returned rate from the gyro.
     */
    public double getAngle() {
        if (channel == null) {
            return 0.0;
        } else {
            channel.getAccumulatorOutput(result);

            long value = result.value - (long) (result.count * voltageOffset);

            double scaledValue = value * 1e-9 * channel.getLSBWeight() * (1 << channel.getAverageBits()) /
                    (channel.getModule().getSampleRate() * kVoltsPerDegreePerSecond);

            return scaledValue;
        }
    }
    
    /**
     * Reset and recalibrate the gyro
     */
    public void reset(){
        voltageOffset = channel.getVoltage();
        channel.resetAccumulator();
    }
    
}