package com.haluuvananh.ecommerce_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    FirebaseAuth auth;
    ProgressDialog loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Match with Layout
        auth = FirebaseAuth.getInstance();
        loadingProgressBar = new ProgressDialog(this);
        email = findViewById(R.id.login_edt_email);
        password = findViewById(R.id.login_edt_password);
    }
    public void signin(View view) {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userEmail) || !userEmail.contains("@gmail.com")) {
            Toast.makeText(this, "Enter a valid Email Address!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPassword) || userPassword.length() < 8) {
            Toast.makeText(this, "Password is invalid!", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingProgressBar.setTitle("Login processing...");
            loadingProgressBar.setMessage("Please wait, while we are checking the credentials. ");
            loadingProgressBar.setCanceledOnTouchOutside(false);
            loadingProgressBar.show();
            auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        loadingProgressBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else {
                        loadingProgressBar.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void signup(View view) {

    }

}