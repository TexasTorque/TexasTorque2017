package org.texastorque.io;

public class Input {

	private static Input instance;
	
	protected double DB_leftSpeed;
	protected double DB_rightSpeed;
	
	protected double FW_leftSpeed;
	protected double FW_rightSpeed;
	
	public Input() {
		DB_leftSpeed = 0.0;
		DB_rightSpeed = 0.0;
	}

	public void setDB_leftSpeed(double leftSpeed) {
		DB_leftSpeed = leftSpeed;
	}
	
	public double getDB_leftSpeed() {
		return DB_leftSpeed;
	}

	public void setDB_rightSpeed(double rightSpeed) {
		DB_rightSpeed = rightSpeed;
	}
	
	public double getDB_rightSpeed() {
		return DB_rightSpeed;
	}
	
	public void setFW_leftSpeed(double leftSpeed) {
		FW_leftSpeed = leftSpeed;
	}
	
	public double getFW_leftSpeed() {
		return FW_leftSpeed;
	}
	
	public void setFW_rightSpeed(double rightSpeed) {
		FW_rightSpeed = rightSpeed;
	}
	
	public double getFW_rightSpeed() {
		return FW_rightSpeed;
	}
	
	public static Input getInstance() {
		return instance == null ? instance = new Input() : instance;
	}

}
