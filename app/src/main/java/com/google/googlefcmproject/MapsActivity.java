package com.google.googlefcmproject;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.googlefcmproject.databinding.ActivityMapsBinding;

    /*
    Create a FragmentActivity class and implement the onMapReadyCallback, this OnMapReadyCallback
    is used to perform actions on the map when it is ready.
     **/
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    /*
    Declare the  the googleMap variable, MarkerOptions variable,
    BroadcastReceiver variable, longitude and latitude variable.
    These variables are class variable,
    they are used within the class
    **/
    private GoogleMap mMap;
    private MarkerOptions markerOpts;
    private BroadcastReceiver receiver;
    public static final String MAP_ACTIVITY_BROADCAST_CHANNEL = "MAP_ACTIVITY_BROADCAST_CHANNEL";
    private String longitude;
    private String latitude;
    private String notificationBody = "";


    /*
    This is an activity lifecycle function, you use this function to perform certain actions
    when an activity is created.
    This function call the  getFirebaseToken function  then sends it to the standard output stream.
    **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
       This line of code binds the view with the activity.
        **/
        com.google.googlefcmproject.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getFirebaseToken(getApplicationContext());

        /*
        Declare and initialize your map fragment and map it your google map fragment and
        initialize and call the initializeBroadcastReceiver function.
        **/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initializeBroadcastReceiver();
    }

    /* Get firebase token from from FirebaseMessaging instance, you can use this token
    to send push notifications  from the firebase console to a particular user.
    **/
    public static void getFirebaseToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    String token = task.getResult();
                    String msg = context.getString(R.string.msg_token_fmt, token);
                    System.out.println("fcmToken " + msg);
                });
    }

    /*
     A function that check to confirm if bundle from notification is null, do nothing if it null
     but if it has value, assign it to the longitude and latitude string variable and change the map
     coordinates based on the longitude and latitude from the intent.
     **/
    private void getDataFromPushNotification() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            notificationBody = extras.getString("body");
            longitude = extras.getString("longitude");
            latitude = extras.getString("latitude");
            changeGoogleMapCoordinates(getLatitudeAndLongitude(latitude, latitude));
        }
    }
    /*
      A function that initialize the broad cast receiver, adds it to a broadcast receiver,
      class variable and pass the values from the broadcast receiver intent to the longitude,
      latitude and notificationBody variable, the function also checks of the receiver is null,
      The function also changes the coordinates on the map by parsing the longitude and
      latitude values.
       **/
    private void initializeBroadcastReceiver() {

        if (receiver == null) {
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    System.out.println("extras " + intent.getExtras());
                    notificationBody = intent.getStringExtra("body");
                    longitude = intent.getStringExtra("longitude");
                    latitude = intent.getStringExtra("latitude");
                    changeGoogleMapCoordinates(getLatitudeAndLongitude(longitude, latitude));
                }
            };
        }
    }

    /*
    This function does the following
    1, Clears the map from previous markers or object that has been already assigned to the map
    2, Set the map to the new longitude and latitude
    3, Animate the camera based on the location
    4, add the marker on the map
    5, move the camera and update the map based on the latitude and longitude
    **/
    private void changeGoogleMapCoordinates(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(markerOpts.position(latLng).title(notificationBody));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
    }


    /*
    This function converts the coordinates to longitude and latitude by getting the string
    value from the parameters, converting them to a double primitive type and parsing it to
    the Latitude and longitude object that is return back to the function.
    **/
    private LatLng getLatitudeAndLongitude(String longitude, String latitude) {
        double longitudeValue = Double.parseDouble(longitude);
        double latitudeValue = Double.parseDouble(latitude);
        return new LatLng(latitudeValue, longitudeValue);
    }

    /*
    Register the broadcast receiver with receiver object as well as the receiver channel for proper
    monitoring and data retrieval.
    **/
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter(MAP_ACTIVITY_BROADCAST_CHANNEL));
    }

    /*
    Override the onMapReady function, this function is used to initialize your map, mapType
    and your map options. call the getDataFromPushNotification function here to prevent the app
    from crashing. note: the activities on the map can only happen when it is ready.
    **/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        markerOpts = new MarkerOptions()
                .position(new LatLng(-0, -0))
                .title(notificationBody)
                .icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(markerOpts);
        getDataFromPushNotification();
    }


}