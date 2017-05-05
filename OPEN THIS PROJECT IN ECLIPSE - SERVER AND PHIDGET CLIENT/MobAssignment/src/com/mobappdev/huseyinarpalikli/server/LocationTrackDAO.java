package com.mobappdev.huseyinarpalikli.server;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.rdbms.AppEngineDriver;
import java.sql.*;


public class LocationTrackDAO {
	
	BeanLocationTrack oneTrackedLocation = null;
	Connection conn = null;
    Statement stmt = null;
    String url = null;
	public LocationTrackDAO() {}

	
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
			// connection string for demos database, username demos, password demos
		    conn = DriverManager.getConnection(url);
		    //System.out.println("CONN"+conn.toString());
		    stmt = conn.createStatement();
		    //System.out.println("STMT"+stmt.toString());
		} catch(SQLException se) { System.out.println(se); }	   
    }
	
	private void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BeanLocationTrack getNextTrackedLocation(ResultSet rs){
		BeanLocationTrack thisTrackedLocation=null;
		try {
			thisTrackedLocation = new BeanLocationTrack(
					rs.getString("userid"),
					rs.getDouble("latitude"),
					rs.getDouble("longitude"),
					rs.getDouble("altitude"),
					rs.getString("timeinserted") );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thisTrackedLocation;		
	}
	
	
	
   public ArrayList<BeanLocationTrack> getAllTrackingHistory(){
	   
		ArrayList<BeanLocationTrack> allTrackingHistory = new ArrayList<BeanLocationTrack>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from locationtrace";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneTrackedLocation = getNextTrackedLocation(rs1);
		    	allTrackingHistory.add(oneTrackedLocation);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allTrackingHistory;
   }

   public BeanLocationTrack getLastTrackedForID(String userID){
	   
		openConnection();
		oneTrackedLocation=null;
	    // Create select statement and execute it
		try{
			String selectSQL = "select * from locationtrace where userid=\"" + userID +"\"";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneTrackedLocation = getNextTrackedLocation(rs1);
		    }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return oneTrackedLocation;
   }
   
   public ArrayList<BeanLocationTrack> getAllTrackingHistoryForID(String userID){
	   
		ArrayList<BeanLocationTrack> allTrackingHistoryForID = new ArrayList<BeanLocationTrack>();
		openConnection();
		
	    // Create select statement and execute it
		try{
		    String selectSQL = "select * from locationtrace where userid=\"" + userID +"\" ORDER BY timeinserted DESC;";
		    ResultSet rs1 = stmt.executeQuery(selectSQL);
	    // Retrieve the results
		    while(rs1.next()){
		    	oneTrackedLocation = getNextTrackedLocation(rs1);
		    	allTrackingHistoryForID.add(oneTrackedLocation);
		   }

		    stmt.close();
		    closeConnection();
		} catch(SQLException se) { System.out.println(se); }

	   return allTrackingHistoryForID;
   } 
   
}
