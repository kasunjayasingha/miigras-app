package com.kasunjay.miigras_app.Activity;

import static com.kasunjay.miigras_app.util.Constants.BASE_URL;
import static com.kasunjay.miigras_app.util.Constants.KEY_ACCESS_TOKEN;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_USERS;
import static com.kasunjay.miigras_app.util.Constants.KEY_FCM_TOKEN;
import static com.kasunjay.miigras_app.util.Constants.KEY_USER;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_EMPLOYEE_DETAILS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_NAME;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kasunjay.miigras_app.Activity.chat.InboxActivity;
import com.kasunjay.miigras_app.Adapter.NewsAdapter;
import com.kasunjay.miigras_app.Domain.NewsDomain;
import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ActivityHomeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private static final String TAG = "HomeActivity";
    private RecyclerView.Adapter adapterNewsList;
    private RecyclerView recyclerViewNewsList;

    private static String ACCESS_TOKEN = "";
    private static long userId = 0;

    private static String sendSOSURL = BASE_URL + "/api/v1/mobile/sos/";


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
        getToken();

        ACCESS_TOKEN = (sharedPref.getString(KEY_ACCESS_TOKEN, ""));
        userId = sharedPref.getLong("userId", 0);
        TextView employeeName = binding.employeeName;
        LinearLayout chat = binding.chatLl;
        LinearLayout emergencyChat = binding.emergencyLl;
        LinearLayout sos = binding.sosLl;

        try {
            JSONObject employee = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
            employeeName.setText(String.format("Hi, %s", employee.getJSONObject("person").getString("firstName")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        chat.setOnClickListener(v -> {
            startActivity(new android.content.Intent(HomeActivity.this, InboxActivity.class));
            finish();
        });

        emergencyChat.setOnClickListener(v -> {
            ChatUser chatUser = new ChatUser();
            chatUser.setUserId(1L);
            Intent intent = new Intent(this, PredictionActivity.class);
            intent.putExtra(KEY_USER, chatUser);
            startActivity(intent);
            finish();
        });

        sos.setOnClickListener(v -> {
            showSosDialog();
        });

    }

    private void showSosDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_sos);

        // Set width and height for the dialog
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Reference the views inside the dialog
        TextView tvCountdownTimer = dialog.findViewById(R.id.tvCountdownTimer);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        // Create a countdown timer starting from 5 seconds (5000 milliseconds)
        CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // Update the countdown timer text
                tvCountdownTimer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Call the sendSos() method when the timer finishes
                try {
                    sendSos();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                dialog.dismiss();
            }
        }.start(); // Start the countdown

        // Cancel button stops the timer and dismisses the dialog
        btnCancel.setOnClickListener(v -> {
            countDownTimer.cancel();
            dialog.dismiss();
        });

        // If user clicks outside the dialog, stop the countdown and close the dialog
        dialog.setOnCancelListener(dialogInterface -> countDownTimer.cancel());

        // Show the dialog
        dialog.show();
    }

    private void sendSos() throws JSONException {
        JSONObject employeeDetailsJson = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
        try {
            sendSOSURL = BASE_URL + "/api/v1/mobile/sos/";
            sendSOSURL = sendSOSURL + employeeDetailsJson.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "sendSos: " + sendSOSURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, sendSOSURL, null,
                response -> {
                    // Handle response
                    Log.d(TAG, "onResponse: " + response);
                    Toast.makeText(getApplicationContext(), "SOS Sent!", Toast.LENGTH_LONG).show();
                },
                error -> {
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
        newsList.add(new NewsDomain("SLBFE", "The Local Support", "news_1"));
        newsList.add(new NewsDomain("Don't Get Caught", "Use legal channels", "news_2"));

        recyclerViewNewsList = binding.newsView;
        recyclerViewNewsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterNewsList = new NewsAdapter(newsList);
        recyclerViewNewsList.setAdapter(adapterNewsList);

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(KEY_COLLECTION_USERS).document(String.valueOf(sharedPref.getLong("userId", 0)));
        documentReference.update(KEY_FCM_TOKEN, token)
                .addOnSuccessListener(aVoid -> {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(KEY_FCM_TOKEN, token);
                    editor.apply();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to update token", Toast.LENGTH_SHORT).show();
                });
    }


}