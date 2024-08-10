package com.kasunjay.miigras_app.Activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kasunjay.miigras_app.Adapter.NewsAdapter;
import com.kasunjay.miigras_app.Domain.NewsDomain;
import com.kasunjay.miigras_app.databinding.ActivityHomeBinding;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private static final String TAG = "HomeActivity";
    private RecyclerView.Adapter adapterNewsList;
    private RecyclerView recyclerViewNewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        initRecyclerView();
        BottomNavigation();
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