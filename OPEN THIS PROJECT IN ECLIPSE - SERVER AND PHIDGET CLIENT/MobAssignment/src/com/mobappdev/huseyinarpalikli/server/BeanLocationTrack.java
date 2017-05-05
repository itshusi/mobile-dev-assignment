package com.mobappdev.huseyinarpalikli.server;

public class BeanLocationTrack {

	private String userID;
	
	private double lat;
	
	private double lng;
	
	private double alt;
	
	private String timeStamp;

	public BeanLocationTrack(String userID) {
		this.userID = userID;
	}
	public BeanLocationTrack(String userID, double lat, double lng, double alt, String timeStamp ) {
		this.userID = userID;
		this.lat = lat;
		this.lng = lng;
		this.alt = alt;
		this.timeStamp = timeStamp;
	}
	
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
	
	public double getAlt() {
		return alt;
	}

	public void setAlt(double alt) {
		this.alt = alt;
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
		sb.append("Lat:" + getLat());
		sb.append(", ");
		sb.append("Lng:" + getLng());
		sb.append(", ");
		sb.append("Alt:" + getAlt());
		sb.append(", ");
		sb.append("Time and Date:" + getTimeStamp());
		sb.append(". } \n");
		
		return sb.toString();
	}
}