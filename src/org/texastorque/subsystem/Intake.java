package org.texastorque.subsystem;

import org.texastorque.io.HumanInput;
import org.texastorque.io.RobotOutput;

public class Intake extends Subsystem {

	private static Intake instance;
	
	private double lowerSpeed = 0d;
	private double upperSpeed = 0d;
	
	@Override
	public void autoInit() {
		init();
	}

	@Override
	public void teleopInit() {
		init();
	}
	
	@Override
	public void disabledInit() {
		lowerSpeed = 0;
		upperSpeed = 0;
	}

	private void init() {
	}
	
	@Override
	public void autoContinuous() {
//		run();
	}

	@Override
	public void teleopContinuous() {
		run();
	}
	
	@Override
	public void disabledContinuous() {
		output();
	}

	private void run() {
		lowerSpeed = i.getIN_lowerSpeed();
		upperSpeed = i.getIN_upperSpeed();
		output();
	}
	
	private void output() {
		if(i instanceof HumanInput) {
			if(i.getVI_rpmsGood()) {
				RobotOutput.getInstance().setIntakeSpeed(1, .3);
			} else {
				RobotOutput.getInstance().setIntakeSpeed(upperSpeed, lowerSpeed);
			}
		}
	}
	
	@Override
	public void smartDashboard() {
	}
	
	public static Intake getInstance() {
		return instance == null ? instance = new Intake() : instance;
	}

}
