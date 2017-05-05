package com.mobappdev.huseyinarpalikli.server;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.appengine.api.utils.SystemProperty;

import java.util.*;
import java.io.*;
import java.sql.*;


/**
 * Servlet implementation class sensorToDB
 */
@WebServlet("/sensorToDB")
public class sensorToDB extends HttpServlet {
	
		private static final long serialVersionUID = 1L;
		private String lastValidUserIDStr = "no user";
		private String lastValidSensorNameStr  = "no sensor";
        private String lastValidSensorValueStr = "invalid";
        private String returnMessage = "";
        
       Connection conn = null;
	  Statement stmt;

	  public void init(ServletConfig config) throws ServletException {
	  // init method is run once at the start of the servlet loading
	  // This will load the driver and establish a connection
	    super.init(config);
	    String url;
		//String user = "arpalikh";
	    //String password = "Vanscerq9";
	    //String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:3306/"+user;

		// Load the database driver
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
        } // init()
	  }

	  public void destroy() {
	        try { conn.close(); } catch (SQLException se) {
	            System.out.println(se);
	        }
	  } // destroy()
	  
	
	
    public sensorToDB() {
        super();
        // TODO Auto-generated constructor stub
    }


public void doGet(HttpServletRequest request,
           		  HttpServletResponse response)	throws ServletException, IOException {


	response.setStatus(HttpServletResponse.SC_OK);
	String info = request.getParameter("getdata");

	// Do we want info or to enter data on current sensor?
	// if request for info isn't here, record the current sensor name/value from the parameters
	if (info == null){
		String userID = request.getParameter("userid");
		String sensorNameStr = request.getParameter("sensorname");
		String sensorValueStr = request.getParameter("sensorvalue");
		if (!(sensorNameStr==null) && !(sensorValueStr==null)) {
			returnMessage = updateSensorTable(userID, sensorNameStr, sensorValueStr);
		}
		else returnMessage = "bad data";

		PrintWriter out = response.getWriter();
		System.out.println("DEBUG: Return response for receiving data "+ returnMessage);
		out.print(returnMessage);
		out.close();

	} // endif not requesting info

else {  // send info as json
	response.setContentType("application/json");
	
    String json = "{" +
	"\"userid\": \"" + lastValidUserIDStr + "\", " +
	"\"sensorname\": \"" + lastValidSensorNameStr + "\", " +
	"\"sensorvalue\": \"" + lastValidSensorValueStr + "\"}";

	PrintWriter out = response.getWriter();
	System.out.println("DEBUG: json return: "+json);
	out.print(json);
	out.close();
}
}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // Post is same as Get, so pass on parameters and do same
	    doGet(request, response);
	}

private String updateSensorTable(String userIDStr, String sensorNameStr, String sensorValueStr){
	try {
		// Create the INSERT statement from the parameters
		// set time inserted to be the current time on database server
		String updateSQL = 
	     	"insert into sensorUsage(UserID, SensorName, SensorValue, TimeInserted) "+
	      	"values('"+userIDStr+"','"+sensorNameStr+"','"+sensorValueStr+"', now());";
	        System.out.println("DEBUG: Update: " + updateSQL);
	        stmt.executeUpdate(updateSQL);
	} catch (SQLException se) {
		// Problem with update, return failure message
	    System.out.println(se);
	    return("Invalid");
	}

	// all ok, update last known values and return
	lastValidUserIDStr = userIDStr;
	lastValidSensorNameStr = sensorNameStr;
	lastValidSensorValueStr = sensorValueStr;	
	return "OK";
}	
	
	
}
