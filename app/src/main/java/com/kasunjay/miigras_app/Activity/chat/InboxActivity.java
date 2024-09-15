package com.kasunjay.miigras_app.Activity.chat;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.kasunjay.miigras_app.Activity.HomeActivity;
import com.kasunjay.miigras_app.Activity.ProfileActivity;
import com.kasunjay.miigras_app.data.model.ChatMessage;
import com.kasunjay.miigras_app.databinding.ActivityInboxBinding;

import java.util.List;

public class InboxActivity extends AppCompatActivity {

    private ActivityInboxBinding binding;
    private static final String TAG = "InboxActivity";
    private List<ChatMessage> conversationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.inboxBackBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(InboxActivity.this, HomeActivity.class));
            finish();
        });

        binding.addFriendBtn.setOnClickListener(v -> {
            startActivity(new android.content.Intent(InboxActivity.this, FindFriendActivity.class));
            finish();
        });

    }
}