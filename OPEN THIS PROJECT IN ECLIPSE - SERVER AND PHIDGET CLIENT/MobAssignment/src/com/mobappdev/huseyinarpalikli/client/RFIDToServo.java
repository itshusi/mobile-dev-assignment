package com.mobappdev.huseyinarpalikli.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.phidgets.AdvancedServoPhidget;
import com.phidgets.Phidget;
import com.phidgets.PhidgetException;
import com.phidgets.RFIDPhidget;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.ServoPositionChangeEvent;
import com.phidgets.event.ServoPositionChangeListener;
import com.phidgets.event.TagGainEvent;
import com.phidgets.event.TagGainListener;
import com.phidgets.event.TagLossEvent;
import com.phidgets.event.TagLossListener;


public class RFIDToServo implements TagLossListener, TagGainListener {

    // servo board and port number of motor
    static AdvancedServoPhidget servo;
    int servoNumber = 0;
    static ArrayList<String> validTags = new ArrayList<String>();
    static ArrayList<String> validUsers = new ArrayList<String>();
    public static String sensorServerURL = "http://1-dot-mobile-assignmen-1482166041830.appspot.com/sensor-to-db";
    int openPosition = 180; 			// position of lock to open
    int keepLockOpenTime = 5000; 	// how long to keep lock open (milliseconds)
    int closedPosition = 0; 			// position of lock when closed
    String lastTag = "";
    public RFIDToServo() throws PhidgetException {

        // Valid Tags for Lock
        validTags.add("testcard1"); // Test card
        validUsers.add("harpalikli@hotmail.com");

        // Create rfid reader object and attach loss/gain listeners
        RFIDPhidget phid = new RFIDPhidget();
        phid.addTagLossListener(this);
        phid.addTagGainListener(this);
        phid.openAny();
        phid.waitForAttachment();

        // wait for motor attachments on servo board
        servo.openAny();
        servo.waitForAttachment();

        // display info on rfid reader to show connected
        System.out.println(phid.getDeviceType());
        System.out.println("RFID Reader Serial Number " + phid.getSerialNumber());
        System.out.println("Device Version " + phid.getDeviceVersion());
        System.out.println("Attached " + phid.isAttached());

        // Display info on servo motors
        System.out.println("Servo Motor Serial: " + servo.getSerialNumber());
        System.out.println("Servo motor count: " + servo.getMotorCount());

        phid.setLEDOn(true);
        phid.setAntennaOn(true);

        // initialise lock
        moveServoTo(closedPosition);

        System.out.println("\nListening for RFID card swipe");
        // sleeping around, just to avoid the program finishing
        while (true)
            // repeat forever detecting rfid tag values
            try {
                Thread.sleep(1000);
            } catch (Throwable t) {
                t.printStackTrace();
            }

    }
    public static void main(String[] args) throws PhidgetException {
// TODO Auto-generated method stub


        System.out.println(Phidget.getLibraryVersion());
        servo = new AdvancedServoPhidget();

// main code starts here
        new RFIDToServo();

    }

    public void moveServoTo(int position){
        // utility method to move motor to indicated position
        try {
            servo.setEngaged(servoNumber, false);
            servo.setSpeedRampingOn(servoNumber, false);
            servo.setPosition(servoNumber, position);
            servo.setEngaged(servoNumber, true);
            Thread.sleep(500);
        }
        catch (PhidgetException pe) {System.out.println("Motor error");}
        catch (InterruptedException ie) {System.out.println("Sleep error");}
    }


    @Override
    public void tagGained(TagGainEvent arg0)  {
        // Check tag value and move if necessary
        String tagIDFound = arg0.getValue();
        lastTag = tagIDFound;
        System.out.println("Card swiped - " + tagIDFound);

        if (validTag(tagIDFound))  {
            // open door lock
            openDoorLock(servoNumber);
        }
        else {
            System.out.println("Non-recognised tag for lock - "+tagIDFound);
        }
        String validTag = String.valueOf(validTag(tagIDFound));
        String sendResult = sendToServer(lastTag, "RFID", validTag);
    }

    @Override
    public void tagLost(TagLossEvent arg0) {
    }

    private void openDoorLock(int lockID) {
        System.out.println("Opening lock for " + keepLockOpenTime / 1000.0 + " seconds");

        moveServoTo(openPosition);
        String sendResult = sendToServer(lastTag, "Servo", String.valueOf(openPosition));
        try {
            Thread.sleep(keepLockOpenTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Closing lock.");

        moveServoTo(closedPosition);
        sendResult = sendToServer(lastTag, "Servo", String.valueOf(closedPosition));
    }

    private boolean validTag(String searchTag) {
        boolean tagRecognised = validTags.contains(searchTag);
        System.out.println("Card checked as valid - " + tagRecognised);
        return tagRecognised;
    }

    public static void setServoListeners(){
        servo.addAttachListener(new AttachListener() {
            public void attached(AttachEvent ae) {
                System.out.println("attachment of " + ae);
            }
        });

        servo.addDetachListener(new DetachListener() {
            public void detached(DetachEvent ae) {
                System.out.println("detachment of " + ae);
            }
        });

        servo.addErrorListener(new ErrorListener() {
            public void error(ErrorEvent ee) {
                System.out.println("error event for " + ee);
            }
        });

        servo.addServoPositionChangeListener(new ServoPositionChangeListener()
        {
            public void servoPositionChanged(ServoPositionChangeEvent oe)
            {
                //
                // System.out.println(oe);
            }
        });
    }

    private boolean validUser(String searchUser) {

        boolean userAuthenticated = validUsers.contains(searchUser);
        System.out.println("User checked as valid - " + userAuthenticated);
        return userAuthenticated;
    }
    public void userTrigger(String userID) throws IOException  {
        // Check tag value and move if necessary
        String userIDFound = userID;

        System.out.println("User triggered - " + userIDFound);
        if (getLastTriggeredVal().get(2)=="true"){
            if (validUser(userIDFound))  {
                // open door lock
                openDoorLock(servoNumber);
            }
            else {
                System.out.println("Non-recognised user for lock - "+userIDFound);
            }
            String validUser = String.valueOf(validTag(userIDFound));
        }
        //String sendResult = sendToServer(userIDFound, "Servo", validUser );
    }



    public String sendToServer(String userID, String sensorName, String sensorValue) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String fullURL = sensorServerURL + "?userid="+userID+"&sensorname="+sensorName+"&sensorvalue="+sensorValue;
        //String encodedURL=java.net.URLEncoder.encode(fullURL,"UTF-8");
        System.out.println("Sending data to: "+fullURL);
        String line;
        String result = "";
        try {
            url = new URL(fullURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "done : "+result;
//    return result;

    }

    public static ArrayList<String> getLastTriggeredVal() throws IOException {
        String sURL = "http://1-dot-mobile-assignmen-1482166041830.appspot.com/trigger-sensor?triggerservo=true"; //just a string

        // Connect to the URL using java's native library
        URL url = new URL(sURL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
        ArrayList<String> data = new ArrayList<String>();
        data.add(rootobj.get("userid").getAsString());
        data.add(rootobj.get("sensorname").getAsString());
        data.add(rootobj.get("sensorvalue").getAsString());
        return data;
    }

}
