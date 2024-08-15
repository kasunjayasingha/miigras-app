package com.kasunjay.miigras_app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.databinding.ActivityProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private static final String TAG = "ProfileActivity";

    private static final String SHARED_PREF_NAME = "user_login_pref";
    private static final String SHARED_PREF_EMPLOYEE_DETAILS = "employee_details";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static String ACCESS_TOKEN = "";
    private static long userId = 0;

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
    }
}