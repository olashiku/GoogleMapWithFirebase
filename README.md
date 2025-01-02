# Simple Firebase Location Pins

## Overview
A basic Android application that receives Firebase push notifications containing coordinates and displays pins on Google Maps.

## Features
- Receives Firebase push notifications
- Places pins on Google Maps based on notification coordinates
- Background notification handling

## Technical Stack
- Kotlin
- Firebase Cloud Messaging (FCM)
- Google Maps SDK

## Implementation

### 1. Project Setup
Add dependencies in build.gradle:
```gradle
dependencies {
    implementation 'com.google.firebase:firebase-messaging-ktx:24.1.1'
    implementation 'com.google.android.gms:play-services-maps:18.1.0'
}
```

### 2. Firebase Service
```kotlin
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // Extract coordinates from notification
        val latitude = remoteMessage.data["latitude"]?.toDouble()
        val longitude = remoteMessage.data["longitude"]?.toDouble()
        
        // Send broadcast to activity
        val intent = Intent("LOCATION_UPDATE")
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        sendBroadcast(intent)
    }
}
```

### 3. Main Activity
```kotlin
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getDoubleExtra("latitude", 0.0)
            val longitude = intent?.getDoubleExtra("longitude", 0.0)
            addPin(latitude, longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
        // Register receiver
        registerReceiver(locationReceiver, IntentFilter("LOCATION_UPDATE"))
    }

    private fun addPin(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            val location = LatLng(latitude, longitude)
            map.addMarker(MarkerOptions().position(location))
            map.moveCamera(CameraUpdateFactory.newLatLng(location))
        }
    }
}
```

### 4. Test Notification Payload
```json
{
    "to": "[DEVICE_FCM_TOKEN]",
    "data": {
        "latitude": "37.7749",
        "longitude": "-122.4194"
    }
}
```

## Setup Steps

1. **Firebase Setup**
   - Create Firebase project
   - Add Android app
   - Download google-services.json
   - Place in app folder

2. **Google Maps**
   - Get Maps API key
   - Add to AndroidManifest.xml
   ```xml
   <meta-data
       android:name="com.google.android.geo.API_KEY"
       android:value="YOUR_API_KEY"/>
   ```

3. **Required Permissions**
   ```xml
   <uses-permission android:name="android.permission.INTERNET" />
   ```

## Testing
Send test notification using Firebase console or cURL:
```bash
curl -X POST -H "Authorization: key=YOUR_SERVER_KEY" \
     -H "Content-Type: application/json" \
     -d '{
       "to": "DEVICE_TOKEN",
       "data": {
         "latitude": "37.7749",
         "longitude": "-122.4194"
       }
     }' \
     https://fcm.googleapis.com/fcm/send
```

Would you like me to add any other specific details?
