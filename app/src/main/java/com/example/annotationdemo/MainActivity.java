package com.example.annotationdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.router_annotation.Route;

@Route(path = "/app/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}