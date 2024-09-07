package com.kasunjay.miigras_app.Activity;

import static com.kasunjay.miigras_app.util.Constants.BASE_URL;
import static com.kasunjay.miigras_app.util.Constants.KEY_ACCESS_TOKEN;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_USERS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_EMPLOYEE_DETAILS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_NAME;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kasunjay.miigras_app.databinding.ActivityLoginBinding;
import com.kasunjay.miigras_app.service.LocationService;
import com.kasunjay.miigras_app.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    String URL = BASE_URL + "/api/v1/user/login";
    String getEmployeeURL = BASE_URL + "/api/v1/mobile/getEmployeeByUserId";
    private static final int PERMISSION_REQUEST_CODE = 100;

    Button btn;
    EditText username, password;
    String username_shred, password_shred, token_shred, userNameTxt, passwordTxt;
    Boolean isInternetAvailable = false;
private ActivityLoginBinding binding;

    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = binding.emailTil.getEditText();
        password = binding.passwordEt;
        btn = binding.loginBtn;
        btn.setEnabled(false);
        internet_check();

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, Context.MODE_PRIVATE);

        btn.setOnClickListener(view -> {
            userNameTxt = username.getText().toString();
            passwordTxt = password.getText().toString();
            username_shred = sharedPref.getString("username", "");
            password_shred = sharedPref.getString("password", "");
            token_shred = sharedPref.getString("token", "");
            loginDataChanged(userNameTxt, passwordTxt);
        });

    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            Toast.makeText(this, "Invalid username", Toast.LENGTH_SHORT).show();
        } else if (!isPasswordValid(password)) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            loading(true);
            checkPermissions();
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public void internet_check() {
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                // Internet is available
                runOnUiThread(() -> {
//                    Toast.makeText(LoginActivity.this, "Internet is available", Toast.LENGTH_SHORT).show();
                    btn.setEnabled(true);
                });
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                // Internet is lost
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Internet is lost", Toast.LENGTH_SHORT).show();
                });
            }
        };

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }

    private void login() {
        JSONObject payload = new JSONObject();
        try {
            payload.put("username", userNameTxt);
            payload.put("password", passwordTxt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, payload,
                response -> {
                    try {

                        Long id = response.getLong("id");
                        String role = response.getString("role");
                        String accessToken = response.getString("accessToken");
                        Boolean isVerified = response.getBoolean("isFirebaseRegistered");

                        // Save the access token in SharedPreferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putLong("userId", id);
                        editor.putString("role", role);
                        editor.putString(KEY_ACCESS_TOKEN, accessToken);
                        editor.putBoolean("isFirebaseRegistered", isVerified);
                        editor.apply();
                        setEmployee();

                        Log.d(TAG, "sharedPref: " + sharedPref.getString(KEY_ACCESS_TOKEN, ""));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    loading(false);
                    // Handle Volley error
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "";

                    if (networkResponse != null) {
                        String result = new String(networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(result);
//                            String status = response.optString("status", "Unknown status");
//                            String message = response.optString("message", "No message");
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
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    private void setEmployee() {
        getEmployeeURL = BASE_URL + "/api/v1/mobile/getEmployeeByUserId";
        getEmployeeURL = getEmployeeURL + "?userId=" + sharedPref.getLong("userId", 0);
        Log.d(TAG, "getEmployeeURL: " + getEmployeeURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getEmployeeURL, null,
                response -> {

                    employeeDetails.edit().putString(SHARED_PREF_EMPLOYEE_DETAILS, response.toString()).apply();
                    Log.d(TAG, "Employee: " + employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));

                    if(!sharedPref.getBoolean("isFirebaseRegistered", false)) {
                        try {
                            registerUserInFirebase();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }else {
                        successLogin();
                    }
                },
                error -> {
                    loading(false);
                    // Handle Volley error
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "";

                    if (networkResponse != null) {
                        String result = new String(networkResponse.data);
                        try {
                            JSONObject response = new JSONObject(result);
                            String errorResponse = response.optString("error", "No error details");
                            errorMessage = "Error: " + errorResponse;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorMessage = "JSON parsing error in error response: " + e.getMessage();
                        }
                    } else {
//                        errorMessage = error.getClass().getSimpleName() + ": " + error.getMessage();
                        errorMessage = "No employee details found!";
                    }

                    Log.e(TAG, "onErrorResponse: " + errorMessage);
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
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


    private void registerUserInFirebase() throws JSONException {
        JSONObject employee = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put("userId", sharedPref.getLong("userId", 0));
        user.put("role", sharedPref.getString("role", ""));
        user.put("name", employee.getJSONObject("person").getString("firstName") + " " + employee.getJSONObject("person").getString("lastName"));
        user.put("email", employee.getJSONObject("person").getString("email"));
        db.collection(KEY_COLLECTION_USERS).document(String.valueOf(sharedPref.getLong("userId", 0)))
                .set(user)
                .addOnSuccessListener(aVoid -> {

                    String updateURL = BASE_URL + "/api/v1/user/updateFirebaseStatus";
                    updateURL = updateURL + "?userId=" + sharedPref.getLong("userId", 0);
                    Log.d(TAG, "updateFirebaseURL: " + updateURL);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, updateURL, null,
                            response -> {
                                successLogin();
                            },
                            error -> {
                                loading(false);
                                // Handle Volley error
                                NetworkResponse networkResponse = error.networkResponse;
                                String errorMessage = "";

                                if (networkResponse != null) {
                                    String result = new String(networkResponse.data);
                                    try {
                                        JSONObject response = new JSONObject(result);
                                        String errorResponse = response.optString("error", "No error details");
                                        errorMessage = "Error: " + errorResponse;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        errorMessage = "JSON parsing error in error response: " + e.getMessage();
                                    }
                                } else {
                                    errorMessage = "Failed to update Firebase status!";
                                }

                                Log.e(TAG, "onErrorResponse: " + errorMessage);
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
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
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    Toast.makeText(getApplicationContext(), "Failed to register user in Firebase", Toast.LENGTH_LONG).show();
                });

    }

    private void successLogin() {
        loading(false);
        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();

        createNotificationChannel();
        Intent serviceIntent = new Intent(LoginActivity.this, LocationService.class);
        ContextCompat.startForegroundService(LoginActivity.this, serviceIntent);

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void checkPermissions() {
        if (arePermissionsGranted()) {
            // All permissions are granted, enable the login button
            login();
        } else {
            // Permissions are not granted, disable the login button
            requestPermissions();
        }
    }

    private boolean arePermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.FOREGROUND_SERVICE
                },
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (arePermissionsGranted()) {
                // Permissions are granted, enable the login button
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permissions are denied, show a message and disable the login button
                Toast.makeText(this, "Location permissions are required to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "CHANNEL_ID",    // Replace with your channel ID
                    "Tracking Service",  // Replace with your channel name
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notification channel for tracking service");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.loginBtn.setVisibility(View.INVISIBLE);
            binding.progressBarCl.setVisibility(View.VISIBLE);
        }else {
            binding.loginBtn.setVisibility(View.VISIBLE);
            binding.progressBarCl.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}