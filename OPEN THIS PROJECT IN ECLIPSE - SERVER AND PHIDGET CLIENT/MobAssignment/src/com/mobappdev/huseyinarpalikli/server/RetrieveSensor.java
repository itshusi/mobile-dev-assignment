package com.mobappdev.huseyinarpalikli.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RetrieveSensor
 */
@WebServlet("/RetrieveSensor")
public class RetrieveSensor extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RetrieveSensor() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter out = response.getWriter();
		String email = request.getParameter("email");			
		List myData = new ArrayList();
		SensorRecorderDAO senConnect = new SensorRecorderDAO();
		myData = senConnect.getAllSensorRecordsForID(email);
		response.setContentType("text/plain");
		String json="[";	
		Iterator it  = myData.iterator();
		while(it.hasNext()) {
			BeanSensorRecorder sen = (BeanSensorRecorder)it.next();
			//For each Location History object  create <BeanLocationTrack> element
			json += "{" +
					"\"email\": \"" + sen.getUserID() + "\", " +
					"\"sensorname\": \"" + sen.getSensorName() + "\", " +
					"\"sensorvalue\": \"" + sen.getSensorValue() + "\", " +
					"\"timedate\": \"" + sen.getTimeStamp() + "\"}";
					if (it.hasNext())
					{ 
						json += ",";
						json += "\n";
					}
					
	}
		json += "]";
		out = response.getWriter();
		// System.out.println("UploadLocation JSON: "+json);
		out.print(json);
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
