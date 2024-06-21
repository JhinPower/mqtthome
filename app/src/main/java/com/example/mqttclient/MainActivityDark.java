package com.example.mqttclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivityDark extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maindark);
        Button themeButton = findViewById(R.id.themeButton);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 可以跳转到注册页面
                Intent intent = new Intent(MainActivityDark.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivityDark.this, "亮色模式", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
