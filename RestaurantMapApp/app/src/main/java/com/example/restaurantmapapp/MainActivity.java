package com.example.restaurantmapapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button addButton, showAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addButton = findViewById(R.id.addButton);
        showAllButton = findViewById(R.id.showAllButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this, AddNewPlaceActivity.class);
                startActivity(newIntent);
            }
        });

        showAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showAllIntent = new Intent(MainActivity.this, ShowAllOnMapActivity.class);
                startActivity(showAllIntent);
            }
        });
    }
}