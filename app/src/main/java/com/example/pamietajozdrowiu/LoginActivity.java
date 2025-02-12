package com.example.pamietajozdrowiu;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText mailEditText, passwordEditText;
    TextView forgotPasswordTextview, createAccountTextview;
    Button loginButton;
    ProgressBar progressBar;

    FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);


        mailEditText = findViewById(R.id.mail_textbox);
        passwordEditText = findViewById(R.id.password_textbox);
        forgotPasswordTextview = findViewById(R.id.forgot_password_textview);
        createAccountTextview = findViewById(R.id.create_account_textview);
        loginButton = findViewById(R.id.login_button);

        createAccountTextview.setOnClickListener(view -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        loginButton.setOnClickListener(view -> {
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            indicateEmptyFields();

            String mailText = mailEditText.getText().toString();
            String passwordText = passwordEditText.getText().toString();

            if (isFormValid(mailText, passwordText)) {
                mAuth.signInWithEmailAndPassword(mailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this,
                                            "Login Successful",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, CalendarActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this,
                                            "Login Failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void indicateEmptyFields() {
        setEditTextColor(passwordEditText);
        setEditTextColor(mailEditText);
    }

    private void setEditTextColor(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.getBackground().mutate().setColorFilter(getResources()
                    .getColor(android.R.color.holo_red_light), PorterDuff.Mode.SRC_ATOP);
            editText.setHintTextColor(getResources()
                    .getColor(android.R.color.holo_red_light));
        }
        else {
            editText.getBackground().mutate().setColorFilter(getResources()
                    .getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
            editText.setHintTextColor(getResources()
                    .getColor(android.R.color.darker_gray));
        }
    }

    private boolean isFormValid(String mail, String password) {
        return !(mail.isEmpty() || password.isEmpty());
    }
}