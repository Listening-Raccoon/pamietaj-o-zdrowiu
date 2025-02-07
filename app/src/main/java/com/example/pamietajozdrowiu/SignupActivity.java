package com.example.pamietajozdrowiu;

import android.content.Context;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, mailEditText, passwordEditText, repeatPasswordEditText;
    TextView haveAccountTextView;
    Button signupButton;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseFirestore firestore;

    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        firstNameEditText = findViewById(R.id.first_name_textbox);
        lastNameEditText = findViewById(R.id.last_name_textbox);
        mailEditText = findViewById(R.id.mail_textbox);
        passwordEditText = findViewById(R.id.password_textbox);
        repeatPasswordEditText = findViewById(R.id.repeat_password_textbox);
        haveAccountTextView = findViewById(R.id.have_account_textview);
        signupButton = findViewById(R.id.signup_button);


        Context context = this;
        haveAccountTextView.setOnClickListener(view -> {
            startActivity(new Intent(context, LoginActivity.class));
        });

        signupButton.setOnClickListener(view -> {
            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            indicateEmptyFields();
            String firstNameText = firstNameEditText.getText().toString();
            String lastNameText = lastNameEditText.getText().toString();
            String mailText = mailEditText.getText().toString();
            String passwordText = passwordEditText.getText().toString();
            String repeatPasswordText = repeatPasswordEditText.getText().toString();

            if (isFormValid(firstNameText, lastNameText, mailText, passwordText, repeatPasswordText)) {
                mAuth.createUserWithEmailAndPassword(mailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this,
                                            "Account Created",
                                            Toast.LENGTH_SHORT).show();
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    if (currentUser != null) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("id", currentUser.getUid());
                                        map.put("fullName", firstNameText + " " + lastNameText);
                                        map.put("email", mailText);

                                        firestore.collection("users")
                                                .document(currentUser.getUid())
                                                .set(map);
                                    }
                                    startActivity(new Intent(context, LoginActivity.class));
                                    finish();
                                }
                                else {
                                    Log.println(Log.ERROR, "ERROR", task.getException().toString());
                                    Toast.makeText(SignupActivity.this,
                                            "Authentication Failed",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
            progressBar.setVisibility(View.GONE);
        });
    }


    private boolean isFormValid(String firstName, String lastName, String mail, String password, String repeatPassword ) {
        if (firstName.isEmpty() || lastName.isEmpty() || mail.isEmpty() || password.isEmpty() || repeatPassword.isEmpty())
            return false;

        if (!password.equals(repeatPassword)) {
            Toast.makeText(SignupActivity.this,
                    "Passwords don't match",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isMailValid(mail))
            return false;

        return true;
    }

    private boolean isMailValid (String mail) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        return pattern.matcher(mail).matches();
    }

    private void indicateEmptyFields() {
        setEditTextColor(firstNameEditText);
        setEditTextColor(lastNameEditText);
        setEditTextColor(mailEditText);
        setEditTextColor(passwordEditText);
        setEditTextColor(repeatPasswordEditText);
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
}



