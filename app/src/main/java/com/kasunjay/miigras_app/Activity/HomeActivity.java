package com.kasunjay.miigras_app.Activity;

import static com.kasunjay.miigras_app.util.Constants.KEY_ACCESS_TOKEN;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_EMPLOYEE_DETAILS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasunjay.miigras_app.Adapter.NewsAdapter;
import com.kasunjay.miigras_app.Domain.NewsDomain;
import com.kasunjay.miigras_app.databinding.ActivityHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private static final String TAG = "HomeActivity";
    private RecyclerView.Adapter adapterNewsList;
    private RecyclerView recyclerViewNewsList;

    private static String ACCESS_TOKEN = "";
    private static long userId = 0;


    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, Context.MODE_PRIVATE);
        
        initRecyclerView();
        BottomNavigation();

        ACCESS_TOKEN = (sharedPref.getString(KEY_ACCESS_TOKEN, ""));
        userId = sharedPref.getLong("userId", 0);
        TextView employeeName = binding.employeeName;

        try {
            JSONObject employee = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
            employeeName.setText(String.format("Hi, %s", employee.getJSONObject("person").getString("firstName")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void BottomNavigation() {
        LinearLayout home = binding.homeBtn;
        LinearLayout profile = binding.profileBtn;

        home.setOnClickListener(v -> {
            startActivity(new android.content.Intent(HomeActivity.this, HomeActivity.class));
            finish();
        });

        profile.setOnClickListener(v -> {
            startActivity(new android.content.Intent(HomeActivity.this, ProfileActivity.class));
            finish();
        });
    }

    private void initRecyclerView() {
        ArrayList<NewsDomain> newsList = new ArrayList<>();
        newsList.add(new NewsDomain("Title 1", "Subtitle 1", "trends"));
        newsList.add(new NewsDomain("Title 2", "Subtitle 2", "trends2"));

        recyclerViewNewsList = binding.newsView;
        recyclerViewNewsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterNewsList = new NewsAdapter(newsList);
        recyclerViewNewsList.setAdapter(adapterNewsList);

    }
}