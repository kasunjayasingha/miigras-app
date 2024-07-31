package com.kasunjay.miigras_app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kasunjay.miigras_app.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    String URL = "http://192.168.1.101:8081/api/v1/user/login";

    Button btn;
    EditText username, password;
    String username_shred, password_shred, token_shred, userNameTxt, passwordTxt;
    Boolean isInternetAvailable = false;
private ActivityLoginBinding binding;

    private static final String SHARED_PREF_NAME = "my_shared_pref";
    private static final String KEY_ACCESS_TOKEN = "access_token";


    SharedPreferences sharedPref;
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
            login();
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

                        // Save the access token in SharedPreferences
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(KEY_ACCESS_TOKEN, accessToken);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
//                error -> Toast.makeText(getApplicationContext(), "Login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show()
                error -> Log.d(TAG, "onErrorResponse: " + error.getMessage())) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}