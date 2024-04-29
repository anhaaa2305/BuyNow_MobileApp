package com.haluuvananh.ecommerce_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class RegisterActivity extends AppCompatActivity {

    EditText name, email, phone, address, password;
    // Firebase
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    ProgressDialog loadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //getSupportActionBar().hide();
        // Match with Layout
        auth = FirebaseAuth.getInstance();
        loadingProgressBar = new ProgressDialog(this);
        name = findViewById(R.id.register_edt_name);
        email = findViewById(R.id.register_edt_email);
        phone = findViewById(R.id.register_edt_phone_number);
        address = findViewById(R.id.register_edt_address);
        password = findViewById(R.id.register_edt_password);

        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);
        if (isFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
            Intent intent = new Intent(RegisterActivity.this, OnBoardingActivity.class);
            startActivity(intent);
        }
    }
    public void signup(View view) {
        String userName = name.getText().toString().trim();
        String userEmail = email.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userAddress = address.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter Name!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userEmail) || !userEmail.contains("@gmail.com")) {
            Toast.makeText(this, "Enter a valid Email Address!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPhone) || userPhone.length() != 10) {
            Toast.makeText(this, "Enter a valid Phone Number!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userAddress)) {
            Toast.makeText(this, "Enter Address!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(userPassword) || userPassword.length() < 8) {
            Toast.makeText(this, "Password is invalid!", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingProgressBar.setTitle("Create Account");
            loadingProgressBar.setMessage("Please wait, while we are checking the credentials. ");
            loadingProgressBar.setCanceledOnTouchOutside(false);
            loadingProgressBar.show();
            auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loadingProgressBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Successfully Register", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {
                        loadingProgressBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "Register Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void signin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }
}