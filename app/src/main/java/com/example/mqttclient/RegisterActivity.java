package com.example.mqttclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {
    private TextView exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        exit = findViewById(R.id.loginLink);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 可以跳转到注册页面
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                Toast.makeText(RegisterActivity.this, "跳转到注册页面", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
