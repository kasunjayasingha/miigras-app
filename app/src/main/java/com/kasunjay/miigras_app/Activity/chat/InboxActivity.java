package com.kasunjay.miigras_app.Activity.chat;

import static com.kasunjay.miigras_app.util.Constants.KEY_ACCESS_TOKEN;
import static com.kasunjay.miigras_app.util.Constants.KEY_COLLECTION_USERS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_EMPLOYEE_DETAILS;
import static com.kasunjay.miigras_app.util.Constants.SHARED_PREF_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kasunjay.miigras_app.Activity.HomeActivity;
import com.kasunjay.miigras_app.Activity.MainActivity;
import com.kasunjay.miigras_app.Activity.SplashActivity;
import com.kasunjay.miigras_app.Adapter.ChatUserAdapter;
import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ActivityInboxBinding;
import com.kasunjay.miigras_app.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity {
    private static final String TAG = "InboxActivity";
    private ActivityInboxBinding binding;

    private static final String getChatUsersURL = Constants.BASE_URL + "/api/v1/mobile/find-nearby-employees";

    SharedPreferences sharedPref;
    SharedPreferences employeeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        employeeDetails = getSharedPreferences(SHARED_PREF_EMPLOYEE_DETAILS, Context.MODE_PRIVATE);

        ConstraintLayout chat = binding.chatBackBtn;
        EditText search = binding.searchDistanceTxt;
        Spinner distance_unit = binding.distanceUnitSpinner;

        chat.setOnClickListener(v -> {
            startActivity(new android.content.Intent(InboxActivity.this, HomeActivity.class));
            finish();
        });

        search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH || actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                try {
                    getChatUsers(search, distance_unit);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        });
    }

    private void getChatUsers(EditText search, Spinner distance_unit) throws JSONException {
        loading(true);
        String searchDistance = search.getText().toString();
        String distanceUnit = distance_unit.getSelectedItem().toString();
        if(searchDistance.isEmpty()){
            search.setError("Please enter a distance");
            search.requestFocus();
            return;
        }
        Double distance = 0.0;
        if(distanceUnit.equals("km")){
            distance = Double.parseDouble(searchDistance) * 1000;
        }else {
            distance = Double.parseDouble(searchDistance);
        }
        JSONObject payload = new JSONObject();
        JSONObject employeeDetailsJson = new JSONObject(employeeDetails.getString(SHARED_PREF_EMPLOYEE_DETAILS, ""));
        try {
            payload.put("id", employeeDetailsJson.getLong("id"));
            payload.put("radius", distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest (Request.Method.POST, getChatUsersURL, payload,
                response -> {
                    try {
                        JSONArray chatUsers = response.optJSONArray("data");
                        binding.UserRecyclerView.setAdapter(null);
                        binding.UserRecyclerView.setVisibility(View.INVISIBLE);
                        if (chatUsers != null) {
                            Log.d(TAG, "name: " + chatUsers.getJSONObject(0).getString("name"));
                            List<ChatUser> chatUserList = new ArrayList<>();
                            for (int i = 0; i < chatUsers.length(); i++) {
                                JSONObject chatUser = chatUsers.getJSONObject(i);
                                ChatUser user = new ChatUser();
                                user.setEmployeeId(chatUser.getLong("employeeId"));
                                user.setUserId(chatUser.getLong("userId"));
                                user.setName(chatUser.getString("name"));
                                user.setEmail(chatUser.getString("email"));
                                user.setPhone(chatUser.getString("phone"));
                                user.setJobType(chatUser.getString("jobType"));
                                user.setLatitude(chatUser.getDouble("latitude"));
                                user.setLongitude(chatUser.getDouble("longitude"));

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference documentReference = db.collection(KEY_COLLECTION_USERS).document(String.valueOf(chatUser.getLong("userId")));
                                documentReference.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        if(documentSnapshot.getString("fcm_token") != null){
                                            user.setFcmToken(documentSnapshot.getString("fcm_token"));
                                        }else {
                                            user.setFcmToken("");
                                        }
                                    } else {
                                        Log.d(TAG, "No firebase user found");
                                    }
                                });
                                chatUserList.add(user);
                            }
                            loading(false);
                            if (chatUserList.size() > 0) {
                                ChatUserAdapter adapter = new ChatUserAdapter(chatUserList);
                                binding.UserRecyclerView.setAdapter(adapter);
                                binding.UserRecyclerView.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "No chat users found");
                                showErrorMessage();
                            }
                        } else {
                            Log.d(TAG, "No users found in response");
                            Toast.makeText(this, "No friends found", Toast.LENGTH_SHORT).show();
                        }
                        loading(false);


                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle Volley error
                    loading(false);
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "";
                    int statusCode = 0;

                    if (networkResponse != null) {
                        String result = new String(networkResponse.data);
                        statusCode = networkResponse.statusCode;

                        Log.e(TAG, "Status Code: " + statusCode);
                        Log.e(TAG, "Response Data: " + result);

                        try {
                            JSONObject response = new JSONObject(result);
                            String errorResponse = response.optString("error", "No error details");
                            errorMessage = "Error: " + errorResponse;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            errorMessage = "JSON parsing error in error response: " + e.getMessage();
                        }
                    } else {
                        // Handle case where network response is null
                        Log.e(TAG, "Network response is null, error: " + error.getMessage());
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

    private void showErrorMessage(){
        binding.errorTxt.setText(String.format("%s", "No Chat Users Found"));
        binding.errorTxt.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }


}