package org.texastorque.interfaces;

import org.texastorque.torquelib.base.TorqueClass;

public interface TorqueSubsystem extends TorqueClass {

	public void autoInit();
	
	public void teleopInit();
	
	public void autoContinuous();
	
	public void teleopContinuous();
	
}