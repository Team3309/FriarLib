package org.team3309.friarlib;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.team3309.friarlib.TankDrive.GyroConfig;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;

public class TankDriveTest {
	
	private TankDrive drive = null;
	private SpeedController left, right;
	private DoubleSolenoid driveShifter;
	private Solenoid ptoShifter;
	private Encoder leftEncoder, rightEncoder;
	private FriarGyro gyro;
	private GyroConfig gyroConfig = new GyroConfig();
	
	private DoubleSolenoid.Value highGear = DoubleSolenoid.Value.kForward;
	private DoubleSolenoid.Value lowGear = DoubleSolenoid.Value.kReverse;
	
	@Before
	public void setup(){
		left = createNiceMock(SpeedController.class);
		right = createNiceMock(SpeedController.class);
		SpeedController[] leftArr = new SpeedController[]{left};
		SpeedController[] rightArr = new SpeedController[]{right};
		
		leftEncoder = createNiceMock(Encoder.class);
		rightEncoder = createNiceMock(Encoder.class);
		
		driveShifter = createNiceMock(DoubleSolenoid.class);
		ptoShifter = createNiceMock(Solenoid.class);
		
		gyro = createNiceMock(FriarGyro.class);
		
		drive = new TankDrive.Builder()
			.gyro(gyro)
			.left(leftArr)
			.right(rightArr)
			.leftEncoder(leftEncoder)
			.rightEncoder(rightEncoder)
			.driveShifter(driveShifter)
			.ptoShifter(ptoShifter)
			.ptoSequence(TankDrive.PtoSequence.kFromHigh)
			.ptoSide(TankDrive.PtoSide.kLeft)
			.highGear(highGear)
			.lowGear(lowGear)
			.gyroConfig(gyroConfig)
			.build();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(TankDrive.getInstance());
	}
	
	@Test
	public void testDriveStraight() {
		expect(gyro.getAngularVelocity()).andReturn(0d);
		replay(gyro);
		
		left.set(-1);
		right.set(1);
		replay(left);
		replay(right);
		
		drive.drive(1, 0);
		
		verify(left);
		verify(right);
	}
	
	@Test
	public void testDriveTurnInPlace(){
		expect(gyro.getAngularVelocity()).andReturn(0d);
		replay(gyro);
		
		//turn right
		left.set(1);
		right.set(1);
		replay(left);
		replay(right);
		
		drive.drive(0,1);
		
		verify(left);
		verify(right);
		
		//reset to change expected values
		resetToNice(left);
		resetToNice(right);
		
		//turn left
		left.set(-1);
		right.set(-1);
		replay(left);
		replay(right);
		
		drive.drive(0,-1);
		
		verify(left);
		verify(right);
	}

	@Test
	public void testHighGear() {
		driveShifter.set(highGear);
		replay(driveShifter);
		
		drive.highGear();
		
		verify(driveShifter);
	}

	@Test
	public void testLowGear() {
		driveShifter.set(lowGear);
		replay(driveShifter);
		
		drive.lowGear();
		
		verify(driveShifter);
	}

	@Test
	public void testEngagePto() {
		driveShifter.set(DoubleSolenoid.Value.kOff);
		replay(driveShifter);
		ptoShifter.set(true);
		replay(ptoShifter);
		
		drive.engagePto();
		
		verify(driveShifter);
		verify(ptoShifter);
	}

	@Test
	public void testDisengagePto() {
		ptoShifter.set(false);
		replay(ptoShifter);
		
		drive.disengagePto();
		
		verify(ptoShifter);
	}

	@Test
	public void testStop() {
		left.set(0);
		right.set(0);
		replay(left);
		replay(right);
		
		drive.stop();
		
		verify(left);
		verify(right);
	}

	@Test
	public void testSetPto() {
		//the drivetrain is setup with pto side as left
		left.set(1);
		replay(left);
		
		drive.setPto(1);
		
		verify(left);
	}

	@Test
	public void testResetGyro() {
		gyro.reset();
		replay(gyro);
		
		drive.resetGyro();
		
		verify(gyro);
	}

	@Test
	public void testResetEncoders() {
		resetToNice(leftEncoder);
		resetToNice(rightEncoder);
		leftEncoder.reset();
		rightEncoder.reset();
		replay(leftEncoder);
		replay(rightEncoder);
		
		drive.resetEncoders();
		
		verify(leftEncoder);
		verify(rightEncoder);
	}

	@Test
	public void testGetGyroConfig() {
		assertNotNull(drive.getGyroConfig());
	}

}
