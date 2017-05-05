package com.mobappdev.huseyinarpalikli.server;
import java.util.ArrayList;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.appengine.api.utils.SystemProperty;

import java.sql.*;


public class SensorRecorderDAO {
	
	BeanSensorRecorder oneSensorRecord = null;
	Connection conn = null;
    Statement stmt = null;
    String url;
	public SensorRecorderDAO() {}

	
	private void openConnection(){
		// loading jdbc driver for mysql
		try{
 			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
 		   Class.forName("com.mysql.jdbc.GoogleDriver").newInstance();
 			} else {
 				Class.forName("com.mysql.jdbc.Driver").newInstance();
 			}
 		} catch(Exception e) { System.out.println(e); }

 		// connecting to database
 		try{
 			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
 				DriverManager.registerDriver(new AppEngineDriver());
 				url = "jdbc:google:mysql://mobile-assignmen-1482166041830:europe-west1:mobileassignment/mobiledev?user=root&password=hus280294";
 				} else {
 				   //Local MySQL instance to use during development.
 				  url = "jdbc:mysql://104.199.71.212:3306/mobiledev?user=root&password=hus280294";
 				}

		// get a connection with the user/pass
            conn = DriverManager.getConnection(url);
            // System.out.println("DEBUG: Connection to database successful.");
            stmt = conn.createStatement();
        } catch (SQLException se) {
            System.out.println(se);
        }   
    }
	private void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BeanSensorRecorder getNextSensorRecord(ResultSet rs){
		BeanSensorRecorder thisSensorRecord=null;
		try {
			thisSensorRecord = new BeanSensorRecorder(
					rs.getString("userid"),
					rs.getString("sensorname"),
					rs.getString("sensorvalue"),
					rs.getString("timeinserted") );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thisSensorRecord;		
	}
	
   public ArrayList<BeanSensorRecorder> getAllSensorRecordsBySensorNameForID(String userID, String sensorName){
	   
		ArrayList<BeanSensorRecorder> allSensorRecordsForID = new ArrayList<BeanSensorRecorder>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from sensorUsage where userid=\"" + userID +"\" and sensorname=\"" + sensorName +"\" ORDER BY timeinserted DESC;";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneSensorRecord = getNextSensorRecord(rs1);
		    	allSensorRecordsForID.add(oneSensorRecord);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allSensorRecordsForID;
   } 
   public ArrayList<BeanSensorRecorder> getAllSensorRecordsForID(String userID){
	   
		ArrayList<BeanSensorRecorder> allSensorRecordsForID = new ArrayList<BeanSensorRecorder>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from sensorUsage where userid=\"" + userID +"\" ORDER BY timeinserted DESC;";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneSensorRecord = getNextSensorRecord(rs1);
		    	allSensorRecordsForID.add(oneSensorRecord);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allSensorRecordsForID;
   } 
   
}
