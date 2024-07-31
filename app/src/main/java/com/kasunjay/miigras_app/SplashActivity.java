package com.kasunjay.miigras_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

//       Start the MainActivity after 2 seconds
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        startActivity(new android.content.Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                },
                2000);
    }
}