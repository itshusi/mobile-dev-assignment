package com.mobappdev.huseyinarpalikli.mobileassignment1;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SensorActivity extends AppCompatActivity {

    private static final String TAG="MainActivitySensorEg";
    //public static String sensorServerURL = "http://10.0.2.2:8080/Assignment1/sensorToDB";
    public static String sensorServerURL = "http://1-dot-mobile-assignmen-1482166041830.appspot.com/sensor-to-db";

    private TextView sensorValueField;
    private Button mRetrieveSensorValueButton;
    private Button mUpdateSensorValueButton;
    private Button mServoTrigger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPORTANT: Strict mode only here to allow networking in main thread.
        // Ideally create an AsyncTask
        // Need to remove this after testing initial solution and use AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_sensor);

        sensorValueField = (TextView) findViewById(R.id.sensorvalueTV);
        mRetrieveSensorValueButton = (Button) findViewById(R.id.getSensorValueButton);
        mUpdateSensorValueButton = (Button) findViewById(R.id.updateSensorValueButton);
        mServoTrigger = (Button) findViewById(R.id.button3);

        mRetrieveSensorValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGettingData();
            }
        });

        mUpdateSensorValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuttingData();
            }
        });
        mServoTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTriggerServo();

            }
        });

    }

    private void startGettingData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                getSensorData("sensorname");
                Log.i(TAG, "started thread to get sensor data");
            }
        }).run();
    }
    private void startPuttingData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                sendSensorData("userid", "sensorname","sensorvalue");
                Log.i(TAG, "started thread to get sensor data");
            }
        }).run();
    }
    private void startTriggerServo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                sendSensorData("harpalikli@hotmail.com", "Servo","true");
                Log.i(TAG, "started thread to get sensor data");
            }
        }).run();
    }

    private String getSensorData(String userID) {
        String serverResponse = "";
        Log.i(TAG,"Retrieving data");
        String fullURL = sensorServerURL + "?getdata=true&userid="+userID+"&sensorname=slider";
        Log.i(TAG,"Sending request for sensor data to: "+fullURL);
        serverResponse = contactSensorServer(fullURL);
        Log.i(TAG, "Updating sensor value field");

        sensorValueField.setText(serverResponse);
        Log.i(TAG, "Updated sensor value field");

        return serverResponse;
    }

    private String sendSensorData(String userID, String sensorName, String sensorValue) {
        String serverResponse = "";
        Log.i(TAG,"Sending sensor data for "+sensorName + " value "+sensorValue);
        //String fullURL = "http://10.0.2.2:8080/Assignment1/sensorToDB?" + "userid="+userID+"&sensorname="+sensorName+"&sensorvalue="+sensorValue;
        String fullURL = " http://1-dot-mobile-assignmen-1482166041830.appspot.com/sensor-to-db?" + "userid="+userID+"&sensorname="+sensorName+"&sensorvalue="+sensorValue;

        serverResponse = contactSensorServer(fullURL);
        Log.i(TAG, "Updating sensor value field");

        sensorValueField.setText(serverResponse);
        Log.i(TAG, "Updated sensor value field");

        return serverResponse;
    }

    private String contactSensorServer(String urlStr) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        // Build the full URL from parameter name and value
        Log.i(TAG,"Sending sensor string to: "+urlStr);
        String line = "";
        String result = "";
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // Issue the GET to send the data
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            // read the result to process response and ensure data sent
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Response line from server >"+result+"<");
        // return result;
        return result;
    }




}
