package com.mobappdev.huseyinarpalikli.server;

public class BeanSensorRecorder {

	private String userID;
	
	private String sensorName;
	
	private String sensorValue;
	
	private String timeStamp;

	public BeanSensorRecorder(String userID) {
		this.userID = userID;
	}
	public BeanSensorRecorder(String userID, String sensorName, String sensorValue, String timeStamp ) {
		this.userID = userID;
		this.sensorName = sensorName;
		this.sensorValue = sensorValue;
		this.timeStamp = timeStamp;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getSensorValue() {
		return sensorValue;
	}

	public void getSensorValue(String sensorValue) {
		this.sensorValue = sensorValue;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("User ID:" + getUserID());
		sb.append(", ");
		sb.append("Sensor Name:" + getSensorName());
		sb.append(", ");
		sb.append("Sensor Value:" + getSensorValue());
		sb.append(", ");
		sb.append("Time and Date:" + getTimeStamp());
		sb.append(". } \n");
		
		return sb.toString();
	}
}