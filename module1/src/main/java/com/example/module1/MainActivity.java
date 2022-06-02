package com.example.module1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.router_annotation.Route;

@Route(path = "/module1/main")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //GO Module2
        findViewById(R.id.button).setOnClickListener(v -> {

        });

        //go app
        findViewById(R.id.button2).setOnClickListener(v -> {

        });
    }
}