package com.google.googlefcmproject.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.googlefcmproject.MapsActivity;
import com.google.googlefcmproject.R;

import java.util.Arrays;


    /* The firebase service is a class that we use to receive firebase messages that is sent
    from the firebase console
    **/
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /*
    This function is triggered when a message is received from Firebase, the function
    calls the sendMessageToBroadcastReceiver and passes the RemoteMessage as a parameter.
    **/
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        sendMessageToBroadcastReceiver(message);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    /*
       This function creates and intent, gets the message in  from the RemoteMessage object and
       parses it to the intent with the putExtra function , this putExtra function from the intent
       class receives the name and the value parameter.
       This sendMessageToBroadcastReceiver function also sends the intent to the broadcast receiver
       **/
    public void sendMessageToBroadcastReceiver(RemoteMessage message) {
        try {
            Intent messageIntent = new Intent(MapsActivity.MAP_ACTIVITY_BROADCAST_CHANNEL);
            messageIntent.putExtra("title", message.getNotification().getTitle());
            messageIntent.putExtra("body", message.getNotification().getBody());
            messageIntent.putExtra("longitude", message.getData().get("longitude"));
            messageIntent.putExtra("latitude", message.getData().get("latitude"));
            sendBroadcast(messageIntent);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
