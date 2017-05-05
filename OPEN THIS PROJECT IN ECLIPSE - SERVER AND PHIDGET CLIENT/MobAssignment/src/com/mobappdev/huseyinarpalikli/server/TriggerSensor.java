package com.mobappdev.huseyinarpalikli.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TriggerSensor
 */
@WebServlet("/TriggerSensor")
public class TriggerSensor extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public String lastValidUserIDStr = "no user";
	public String lastValidSensorNameStr  = "no sensor";
    public String lastValidSensorValueStr = "invalid";
    private String returnMessage = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TriggerSensor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setStatus(HttpServletResponse.SC_OK);
		String info = request.getParameter("triggerservo");

		// Do we want info or to enter data on current sensor?
		// if request for info isn't here, record the current sensor name/value from the parameters
		if (info == null){
			String userID = request.getParameter("userid");
			String sensorNameStr = request.getParameter("sensorname");
			String sensorValueStr = request.getParameter("sensorvalue");
			if (!(sensorNameStr==null) && !(sensorValueStr==null)) {
				returnMessage = updateSensors(userID, sensorNameStr, sensorValueStr);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private String updateSensors(String userIDStr, String sensorNameStr, String sensorValueStr){
		// all ok, update last known values and return
		lastValidUserIDStr = userIDStr;
		lastValidSensorNameStr = sensorNameStr;
		lastValidSensorValueStr = sensorValueStr;	
		return "Sent to sensors";
	}	
}
