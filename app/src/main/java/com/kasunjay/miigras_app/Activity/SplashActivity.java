package com.kasunjay.miigras_app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.service.LocationService;
import com.kasunjay.miigras_app.util.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "SplashActivity";
    String getEmployeeURL = GlobalData.BASE_URL + "/api/v1/mobile/getEmployeeByUserId";

    private static final String SHARED_PREF_NAME = "user_login_pref";
    private static final String SHARED_PREF_EMPLOYEE_DETAILS = "employee_details";
    private static final String KEY_ACCESS_TOKEN = "access_token";


    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

//       Start the MainActivity after 2 seconds
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        sharedPref = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, MODE_PRIVATE);

                        if (sharedPref.getString(KEY_ACCESS_TOKEN, "").equals("")) {
                            startActivity(new android.content.Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        } else {
                            setEmployee();
                        }
                    }
                },
                2000);
    }

    private void setEmployee() {
        getEmployeeURL = GlobalData.BASE_URL + "/api/v1/mobile/getEmployeeByUserId";
        getEmployeeURL = getEmployeeURL + "?userId=" + sharedPref.getLong("userId", 0);
        Log.d(TAG, "getEmployeeURL: " + getEmployeeURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getEmployeeURL, null,
                response -> {

                    employeeDetails.edit().putString(SHARED_PREF_EMPLOYEE_DETAILS, response.toString()).apply();
                    Log.d(TAG, "Employee: " + employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));

                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

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
                            startActivity(new android.content.Intent(SplashActivity.this, MainActivity.class));
                            finish();
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
//                        errorMessage = error.getClass().getSimpleName() + ": " + error.getMessage();
                        errorMessage = "No employee details found!";
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
}