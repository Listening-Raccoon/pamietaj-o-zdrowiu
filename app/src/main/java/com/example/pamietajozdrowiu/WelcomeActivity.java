package com.example.pamietajozdrowiu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button signupButton, loginButton;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        signupButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.login_button);


        signupButton.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}