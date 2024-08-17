package com.kasunjay.miigras_app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.databinding.ActivityProfileBinding;
import com.kasunjay.miigras_app.service.LocationService;
import com.kasunjay.miigras_app.util.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private static final String TAG = "ProfileActivity";

    private static final String SHARED_PREF_NAME = "user_login_pref";
    private static final String SHARED_PREF_EMPLOYEE_DETAILS = "employee_details";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static String ACCESS_TOKEN = "";
    private static long userId = 0;
    String URL = GlobalData.BASE_URL + "/api/v1/user/logout";

    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView employeeName = binding.employeeName;
        TextView employeeEmail = binding.employeeEmail;

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, Context.MODE_PRIVATE);

        ACCESS_TOKEN = (sharedPref.getString(KEY_ACCESS_TOKEN, ""));
        userId = sharedPref.getLong("userId", 0);

        ConstraintLayout profileBackBtn = binding.profileBackBtn;
        ConstraintLayout profileLogoutBtn = binding.logoutCL;

        try {
            JSONObject employee = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
            employeeName.setText(String.format("%s %s", employee.getJSONObject("person").getString("firstName"), employee.getJSONObject("person").getString("lastName")));
            employeeEmail.setText(employee.getJSONObject("person").getString("email"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        profileBackBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        profileLogoutBtn.setOnClickListener(v -> {
            logOut();
        });
    }

    private void logOut() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                response -> {
                    // Handle response
                },
                error -> {
                    // Handle Volley error
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.clear();
                    editor.apply();

                    SharedPreferences.Editor editor1 = employeeDetails.edit();
                    editor1.clear();
                    editor1.apply();

                    Intent serviceIntent = new Intent(ProfileActivity.this, LocationService.class);
                    stopService(serviceIntent);
                    startActivity(new android.content.Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(getApplicationContext(), "Logout successfully", Toast.LENGTH_LONG).show();
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