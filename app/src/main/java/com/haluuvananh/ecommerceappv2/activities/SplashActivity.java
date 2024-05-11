package com.haluuvananh.ecommerceappv2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseApp;
import com.haluuvananh.ecommerceappv2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        lottieAnimation = findViewById(R.id.lottieAnimationView);
        lottieAnimation.playAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currUser == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } else {
                    if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("harshlohiya.photos@gmail.com"))
                        startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                    else
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }
        }, 3000);
    }
}