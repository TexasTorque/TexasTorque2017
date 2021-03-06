package org.texastorque.feedback;

import java.util.ArrayList;

import org.texastorque.constants.Ports;
import org.texastorque.torquelib.component.TorqueEncoder;
import org.texastorque.torquelib.util.TorqueMathUtil;


import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feedback {

	private static Feedback instance;

	private final double C_FLYWHEEL = .24;
	private final double DB_DISTANCE_CONVERSION = 0.04927988;
	private final double GC_DISTANCE_CONVERSION = 1.41176;
	
//	sensors
	private TorqueEncoder DB_leftEncoder;
	private TorqueEncoder DB_rightEncoder;

	private AHRS DB_gyro;
	
	private TorqueEncoder FW_leftEncoder;
	private TorqueEncoder FW_rightEncoder;

	private AnalogInput DB_ultrasonic;
	
//	related values
	private double DB_leftDistance;
	private double DB_rightDistance;
	
	private double DB_leftRate;
	private double DB_rightRate;

	private double DB_leftAcceleration;
	private double DB_rightAcceleration;
	
	private double DB_angle;
	private double DB_angleRate;
	
	private double DB_distance;
	
	private double FW_leftDistance;
	private double FW_rightDistance;
	
	private double FW_leftRate;
	private double FW_rightRate;
	
	private double FW_leftAcceleration;
	private double FW_rightAcceleration;
	
	private double GC_distance;
	private double GC_rate;
	
	private Pixy pixy;
	
	private double PX_x1;
	private double PX_y1;
	private double PX_surfaceArea1;
	private double PX_x2;
	private double PX_y2;
	private double PX_surfaceArea2;
	
	private final double PX_CONVERSIONH = .234;
	private final double PX_CONVERSIONV = .235;
	
	private TorqueEncoder GC_encoder;
	
	private boolean PX_goodPacket = false;
	
	private int leftRateLogSize = 3;
	private ArrayList<Double> leftRateLog;
	
	private int rightRateLogSize = 3;
	private ArrayList<Double> rightRateLog;
	
	
	public Feedback() {
		init();
	}
	
	private void init() {
		DB_leftEncoder = new TorqueEncoder(Ports.DB_LEFTENCODER_A, Ports.DB_LEFTENCODER_B, false, EncodingType.k4X);
		DB_rightEncoder = new TorqueEncoder(Ports.DB_RIGHTENCODER_A, Ports.DB_RIGHTENCODER_B, false, EncodingType.k4X);
		DB_gyro = new AHRS(SPI.Port.kMXP);

		DB_ultrasonic = new AnalogInput(Ports.DB_ULTRASONIC);
		
		FW_leftEncoder = new TorqueEncoder(Ports.FW_LEFTENCODER_A, Ports.FW_LEFTENCODER_B, false, EncodingType.k4X);
		FW_rightEncoder = new TorqueEncoder(Ports.FW_RIGHTENCODER_A, Ports.FW_RIGHTENCODER_B, false, EncodingType.k4X);
		
		GC_encoder = new TorqueEncoder(Ports.GC_A, Ports.GC_B, false, EncodingType.k4X);
		
		pixy = new Pixy();

		leftRateLog = new ArrayList<Double>(leftRateLogSize);
		rightRateLog = new ArrayList<Double>(rightRateLogSize);
		
		initializeLists();
	}
	
	public void update() {
		DB_leftEncoder.calc();
		DB_rightEncoder.calc();
		
		FW_leftEncoder.calc();
		FW_rightEncoder.calc();
		
		GC_encoder.calc();
		
		DB_leftDistance = DB_leftEncoder.getDistance() * DB_DISTANCE_CONVERSION;
		DB_rightDistance = DB_rightEncoder.getDistance() * DB_DISTANCE_CONVERSION;
		DB_leftRate = DB_leftEncoder.getRate() * DB_DISTANCE_CONVERSION;
		DB_rightRate = DB_rightEncoder.getRate() * DB_DISTANCE_CONVERSION;
		
		DB_angle = DB_gyro.getAngle();
		DB_angleRate = DB_gyro.getVelocityX();
		
		GC_distance = GC_encoder.getDistance() * GC_DISTANCE_CONVERSION;
		GC_rate = GC_encoder.getRate() * GC_DISTANCE_CONVERSION;
		
		if(DB_ultrasonic.getAverageVoltage() >= .5) {
			DB_distance = (26 / (DB_ultrasonic.getAverageVoltage() - .15))/2.54;
			if(DB_distance >= 28 || DB_distance <= 4) {
				DB_distance = -1;
			}
		} else {
			DB_distance = -1;
		}
		
		FW_leftDistance = FW_leftEncoder.getDistance();
		FW_rightDistance = FW_rightEncoder.getDistance();
		
		
		FW_leftRate = FW_leftEncoder.getRate() * C_FLYWHEEL;
		FW_rightRate = FW_rightEncoder.getRate() * C_FLYWHEEL;
		
		if(FW_leftRate != 0) {
			leftRateLog.remove(leftRateLogSize - 1);
			leftRateLog.add(0,FW_leftRate);
			FW_leftRate = sumLeftRate();
		} else {
			FW_leftRate = sumLeftRate();
		}
		if(FW_rightRate != 0) {
			rightRateLog.remove(rightRateLogSize-1);
			rightRateLog.add(0,FW_rightRate);
			FW_rightRate = sumRightRate();
		} else {
			FW_rightRate = sumRightRate();
		}
		
		try {
			PixyPacket one = pixy.readPacket(1);
			PX_x1 = one.X - 160;
			PX_y1 = one.Y - 100;
//			PixyPacket two = pixy.readPacket(1);
//			PX_y2 = two.Y - 100;
//			PX_x2 = two.X - 160;
			PX_goodPacket = true;
		} catch (Exception e) {
			PX_goodPacket = false;
		}
	}
	
	private void PX_clearData() {
		PX_x1 = -999;
		PX_x2 = -999;
		PX_y1 = -999;
		PX_y2 = -999;
	}
	
//	public double getFW_LeftRateAverage() {
//		leftRateLog.add(0,FW_leftEncoder.getA)
//	}
	
	private void initializeLists() {
		for(int x = 0; x < leftRateLogSize; x++) {
			leftRateLog.add(0d);
		}
		for(int x = 0; x < rightRateLogSize; x++) {
			rightRateLog.add(0d);
		}
	}
	
	private double sumLeftRate() {
		double sum = 0;
		for(double num : leftRateLog)
			sum+=num;
		return sum/leftRateLogSize;
	}
	
	private double sumRightRate() {
		double sum = 0;
		for(double num : rightRateLog)
			sum+=num;
		return sum/rightRateLogSize;
	}
	
	public double getDB_distance() {
		return DB_distance;
	}
	
	public double getDB_leftDistance() {
		return DB_leftDistance;
	}
	
	public double getDB_rightDistance() {
		return DB_rightDistance;
	}
	
	public double getDB_leftRate() {
		return DB_leftRate;
	}
	
	public double getDB_rightRate() {
		return DB_rightRate;
	}
	
	public double getFW_leftRate() {
		return FW_leftRate;
	}
	
	public double getFW_rightRate() {
		return FW_rightRate;
	}

	public double getDB_angle() {
		return DB_angle;
	}
	
	public double getDB_angleRate() {
		return DB_angleRate;
	}
	
	public boolean getPX_goodPacket() {
		return PX_goodPacket;
	}
	
	public double getPX_HorizontalDegreeOff() {
		return ((PX_x1 + PX_x2) / 2)*PX_CONVERSIONH;
	}
	
	public double getGC_distance() {
		return GC_distance;
	}
	
	public double getGC_rate() {
		return GC_rate;
	}
	
	public void resetGC_Encoder() {
		GC_encoder.reset();
	}
	
	public void resetDB_encoders() {
		DB_leftEncoder.reset();
		DB_rightEncoder.reset();
	}
	
	public void resetDB_gyro() {
		DB_gyro.reset();
	}
	
	public void smartDashboard() {
		
		SmartDashboard.putNumber("DB_LEFTPOSITION", DB_leftDistance);
		SmartDashboard.putNumber("DB_RIGHTPOSITION", DB_rightDistance);
		SmartDashboard.putNumber("FW_LEFTPOSITION", FW_leftDistance);
		SmartDashboard.putNumber("FW_RIGHTPOSITION", FW_rightDistance);
		SmartDashboard.putNumber("FW_LEFTRATE", FW_leftRate);
		SmartDashboard.putNumber("FW_RIGHTRATE", FW_rightRate);
		SmartDashboard.putNumber("DB_GYRO", DB_angle);
		SmartDashboard.putNumber("DB_GYRORATE", DB_angleRate);
		SmartDashboard.putNumber("GYROX", DB_gyro.getAngle());
		
		SmartDashboard.putNumber("PIXYX_1", PX_x1);
		SmartDashboard.putNumber("PIXYY_1", PX_y1);

		SmartDashboard.putNumber("PIXYX_2", PX_x2);
		SmartDashboard.putNumber("PIXYY_2", PX_y2);
		
		SmartDashboard.putNumber("DB_SAMPLESIZE", leftRateLogSize);
		SmartDashboard.putNumber("GC_ENCODERDISTANCE", GC_distance);
	}
	
	public static Feedback getInstance() {
		return instance == null ? instance = new Feedback() : instance;
	}
	
}
