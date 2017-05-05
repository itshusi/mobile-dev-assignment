package com.mobappdev.huseyinarpalikli.mobileassignment1;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener, OnMapReadyCallback {
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    // id to send to server
    SupportMapFragment mapFragment;
    private String USERID= "none";
    private static final String TAG="ShowLocation";
    // replace url string with IP of server
    //private static final String baseurl="http://10.0.2.2:8080/Assignment1/UploadLocation";
    private static final String baseurl="http://1-dot-mobile-assignmen-1482166041830.appspot.com/upload-location";

    public List<String> locationHistory = new ArrayList<String>();
    public List<LatLng> locationHistoryLatLng = new ArrayList<LatLng>();
    public List<String> addresssHistory = new ArrayList<String>();
    private LocationManager locationManager;
    private String provider;
    private Double lat = 0.0;
    private Double lng = 0.0;
    private LatLng selectedLocationTrackLatLng;
    public GoogleMap mMap;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Your main activity

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            USERID = extras.getString("email");
        }

// Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
// Define the criteria how to select the location provider -> use
// default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
// Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }
        if (lat != 0 || lng != 0 ) {
            retrievePosHistoryFromServer(USERID);
        }
        ListView locationList = (ListView) findViewById(R.id.locationList);

        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                selectedLocationTrackLatLng = locationHistoryLatLng.get(position);
                Intent myIntent = new Intent(MainActivity.this,MapHistoryActivity.class);
                myIntent.putExtra("lat",selectedLocationTrackLatLng.latitude);
                myIntent.putExtra("lng",selectedLocationTrackLatLng.longitude);
                MainActivity.this.startActivity(myIntent);
            }
        });

        Geocoder geocoder;
        List<Address> addresses = new ArrayList<Address>();
        geocoder = new Geocoder(this, Locale.UK);

        try {
            if (lat != null || lng != null ) {
                addresses = geocoder.getFromLocation(lat, lng, 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

      //  Log.i("ADDRESS", addresses.get(0).getCountryName());
        //Log.i("SIZE", String.valueOf(locationHistory.size()));
        for (int i=0; i<locationHistoryLatLng.size(); i++) {
                double lat = locationHistoryLatLng.get(i).latitude;
                double lng = locationHistoryLatLng.get(i).longitude;
                try {
                    addresses = geocoder.getFromLocation(lat,lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String city = addresses.get(0).getLocality();
                String country = addresses.get(0).getCountryName();
                addresssHistory.add(city + ", " + country);

            }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(lat, lng))
                                                .title("Current Location")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
    @Override
    public void onLocationChanged(final Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendPosToServer(location.getLatitude(), location.getLongitude());
                Log.i(TAG, "done thread");
            }
        }).run();

        retrievePosHistoryFromServer(USERID);
        if (mMap!=null) {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .title("Current Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 15));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub
    }
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    public void retrievePosHistoryFromServer(String userID)
    {
        DownloadTask task = new DownloadTask();
        //task.execute("http://10.0.2.2:8080/Assignment1/RetrieveLocation?email="+userID);
        task.execute("http://1-dot-mobile-assignmen-1482166041830.appspot.com/retrieve-location?email="+userID);

    }
    public void sendPosToServer(double lat, double lon)
    {
        StrictMode.setThreadPolicy(policy);
        Log.i(TAG,"sending pos to server");
        // sending position and userid
        String url = baseurl+ "?lat="+lat+"&lon="+lon+"&email="+USERID;
        HttpClient httpclient = new DefaultHttpClient();
        Log.i(TAG, url);
        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;
        try {
            Log.i(TAG,"httpget is "+httpget. getURI());
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i(TAG,response.getStatusLine().toString());
        } catch (Exception e) {Log.i(TAG,"error in executing get "+e);}
    }

    public void postToServer(double lat, double lon) throws URISyntaxException, ClientProtocolException, IOException {
        String msgBody =  "lat="+lat+"\nlon="+lon;
        // Sets the IP address of localhost on the Windows machine where the service is running
        URI serviceUri = new URI(baseurl);
        Log.i(TAG,"post to server "+serviceUri);
        // Creates a new HttpPut instance around the supplied service uri
        HttpPost postRequest = new HttpPost(serviceUri);
        // Adds the the content type header to the request, set the value to application/json
        postRequest.addHeader("content-type", "text/html");
        postRequest.setEntity(new StringEntity(msgBody));

        // Creates a response handler (BasicResponseHandler) to process the results of the request
        ResponseHandler<String> handler = new BasicResponseHandler();

        // Creates an instance of the DefaultHttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();

        // Uses the client to execute a request passing the HttpPut and ResponseHandler as parameters
        String result = httpclient.execute(postRequest, handler);
        Log.i(TAG, "Put to Server. Result: " + result);

        // Shutdowns the HttpClient
        httpclient.getConnectionManager().shutdown();
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result ="";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1 ){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ListView lv = (ListView) findViewById(R.id.locationList);
            locationHistory = new ArrayList<String>();
            locationHistoryLatLng = new ArrayList<LatLng>();
            addresssHistory = new ArrayList<String >();
            try {

                JSONArray arr = new JSONArray(result);
                for (int i =0; i < arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    locationHistory.add(jsonPart.getString("timedate"));
                }

                for (int j =0; j < arr.length(); j++){
                    JSONObject jsonPart = arr.getJSONObject(j);
                     locationHistoryLatLng.add(new LatLng(jsonPart.getDouble("lat"), jsonPart.getDouble("lon")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Geocoder geocoder;
            List<Address> addresses = new ArrayList<Address>();
            geocoder = new Geocoder(MainActivity.this, Locale.UK);

            Log.i("SIZE OF LOC", String.valueOf(locationHistory.size()));
            Log.i("SIZE OF LOCHIS", String.valueOf(locationHistory.size()));

            for (int i=0; i<locationHistoryLatLng.size(); i++) {
                double lat = locationHistoryLatLng.get(i).latitude;
                double lng = locationHistoryLatLng.get(i).longitude;
                try {
                    addresses = geocoder.getFromLocation(lat,lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String city;
                String country;
                if (addresses.size()==0){
                    city = "Unknown City";
                    country = "Unknown Country";
                } else {
                    city = addresses.get(0).getLocality();
                    country = addresses.get(0).getCountryName();
                }
                if (city!=null) {
                    addresssHistory.add(city + ", " + country);
                } else {
                    addresssHistory.add("Unknown City, " + country);
                }
            }


            List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i=0; i<locationHistory.size(); i++) {
            Map<String, String> datum = new HashMap<String, String>(2);
            datum.put("address", addresssHistory.get(i) );
            datum.put("datetime", locationHistory.get(i) );
            data.add(datum);
        }

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, data , android.R.layout.simple_list_item_2,
                    new String[] { "address", "datetime" },
                    new int[] { android.R.id.text1, android.R.id.text2});
        // DataBind ListView with items from ArrayAdapter
             lv.setAdapter(adapter);
            lv.setTextFilterEnabled(true);

        }
    }

}
