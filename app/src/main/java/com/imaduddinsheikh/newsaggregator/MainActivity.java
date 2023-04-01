package com.imaduddinsheikh.newsaggregator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.imaduddinsheikh.newsaggregator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.imaduddinsheikh.newsaggregator.databinding
                .ActivityMainBinding binding
                = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("News Gateway");
    }
}