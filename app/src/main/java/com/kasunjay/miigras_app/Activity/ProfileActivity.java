package com.kasunjay.miigras_app.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.kasunjay.miigras_app.R;
import com.kasunjay.miigras_app.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ConstraintLayout profileBackBtn = binding.profileBackBtn;

        profileBackBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });
    }
}