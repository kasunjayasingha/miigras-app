package com.kasunjay.miigras_app.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kasunjay.miigras_app.Activity.MainActivity;
import com.kasunjay.miigras_app.Activity.ProfileActivity;
import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.util.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    String URL = GlobalData.BASE_URL + "/api/v1/mobile/updateLocation";

    private FusedLocationProviderClient fusedLocationClient;

    private static final String SHARED_PREF_NAME = "user_login_pref";
    private static final String SHARED_PREF_EMPLOYEE_DETAILS = "employee_details";
    private static final String KEY_ACCESS_TOKEN = "access_token";


    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, Context.MODE_PRIVATE);
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Build the notification for the foreground service
        Notification notification = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("User Tracking")
                .setContentText("Tracking your location...")
                .setSmallIcon(R.drawable.notification)  // Replace with your own icon
                .build();

        // Start the service in the foreground
        startForeground(1, notification);
        requestLocationUpdates();
        return START_STICKY;
    }

    private void requestLocationUpdates() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        Log.d(TAG, "requestLocationUpdates: 1");
//        fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if (task.isSuccessful() && task.getResult() != null) {
//                    Log.d(TAG, "onComplete: Got location");
//                    sendLocationToServer(task.getResult());
//                }else {
//                    Log.e(TAG, "onComplete: Failed to get location");
//                    task.getException().printStackTrace();
//                }
//
//            }
//        });


        Log.d(TAG, "requestLocationUpdates: ");
//        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY , 10 * 60 * 1000)
//                .setMinUpdateIntervalMillis(5 * 60 * 1000)
//                .build();
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 120000) // 2 minutes
                .setMinUpdateIntervalMillis(120000) // 2 minutes
                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "requestLocationUpdates: Location permission not granted");
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

//        CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
//                .setGranularity(Granularity.GRANULARITY_FINE)
//                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//                .setDurationMillis(120000) // 2 minutes
//                .setMaxUpdateAgeMillis(0) // 2 minutes
//                .build();
//
//        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationClient.getCurrentLocation(currentLocationRequest, cancellationTokenSource.getToken())
//                .addOnCompleteListener(new OnCompleteListener<Location>() {
//                                           @Override
//                                           public void onComplete(@NonNull Task<Location> task) {
//                                               if (task.isSuccessful() && task.getResult() != null) {
//                                                   Log.d(TAG, "onComplete: Got location");
//                                                   sendLocationToServer(task.getResult());
//                                               } else {
//                                                   Log.e(TAG, "onComplete: Failed to get location");
//                                                   task.getException().printStackTrace();
//                                               }
//                                           }
//                                       });

    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                for (Location location : locationResult.getLocations()) {
                    sendLocationToServer(location);
                }
            }
        }
    };

    private void sendLocationToServer(Location location) {
        // Send location to your Spring Boot backend
        saveEmployeeLocation(location);
    }

    private void saveEmployeeLocation(Location location) {

        JSONObject payload = new JSONObject();
        try {
            JSONObject employee = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
            payload.put("employeeId", employee.getLong("id"));
            payload.put("latitude", location.getLatitude());
            payload.put("longitude", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, payload,
                response -> {
                    // Handle response
                    Log.d(TAG, "onResponse: " + response.toString());
                },
                error -> {
                    // Handle Volley error
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "";
                    int statusCode = 0;

                    if (networkResponse != null) {
                        String result = new String(networkResponse.data);
                        statusCode = networkResponse.statusCode;
                        if(statusCode == 403){
                            stopSelf();
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();
                            editor.apply();

                            SharedPreferences.Editor editor1 = employeeDetails.edit();
                            editor1.clear();
                            editor1.apply();

                            Toast.makeText(getApplicationContext(), "Session expired, please login again", Toast.LENGTH_LONG).show();
                            startActivity(new android.content.Intent(LocationService.this, MainActivity.class));
                        }
                        try {
                            JSONObject response = new JSONObject(result);
                            String errorResponse = response.optString("error", "No error details");
                            errorMessage = "Error: " + errorResponse;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorMessage = "JSON parsing error in error response: " + e.getMessage();
                        }
                    } else {
                        errorMessage = error.getClass().getSimpleName() + ": " + error.getMessage();
                    }
                    Log.e(TAG, "onErrorResponse: " + errorMessage);
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Access-Token", "Bearer " + sharedPref.getString(KEY_ACCESS_TOKEN, ""));
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setContentTitle("Tracking Location")
                .setContentText("Your location is being tracked")
                .setSmallIcon(R.drawable.notification)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
