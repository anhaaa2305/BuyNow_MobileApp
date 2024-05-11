package com.haluuvananh.ecommerceappv2.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haluuvananh.ecommerceappv2.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.HashMap;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    ProgressBar progressBar;
    EditText nameEditText, emailEditText, passEditText;
    ImageView nextBtn;
    ProgressDialog loadingProgressBar;
    private DatabaseReference RootRef;

    TextView loginPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseApp.initializeApp(this);
        RootRef = FirebaseDatabase.getInstance().getReference();

        progressBar = findViewById(R.id.progress_bar);
        loadingProgressBar = new ProgressDialog(this);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);
        nextBtn = findViewById(R.id.nextBtn);
        loginPageBtn = findViewById(R.id.loginPageBtn);

        nextBtn.setOnClickListener(v -> createAccount());

        loginPageBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void createAccount() {
        String email = emailEditText.getText().toString();
        String pass = passEditText.getText().toString();
//        String confPass = confPassEditText.getText().toString();

        boolean isValidated = validate(email, pass);
        if (!isValidated)
            return;

        loadingProgressBar.setTitle("Create Account");
        loadingProgressBar.setMessage("Please wait, while we are checking the credentials. ");
        loadingProgressBar.setCanceledOnTouchOutside(false);
        loadingProgressBar.show();
        createAccountInFirebase(email,pass);
        storeDataUserUsingRealtimeDatabase(nameEditText.getText().toString().trim(), email, pass);
    }

    void createAccountInFirebase(String email, String pass) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this,
                task -> {
                    changeInProgress(false);
                    if (task.isSuccessful()){
                        Toast.makeText(SignupActivity.this, "Successfully created account, check email to verify", Toast.LENGTH_SHORT).show();
                        Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(nameEditText.getText().toString()).build();
                        assert user != null;
                        user.updateProfile(profileUpdates);
                        firebaseAuth.signOut();
                        finish();
                    }
                    else {
                        Toast.makeText(SignupActivity.this, Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeDataUserUsingRealtimeDatabase(String userName, String userEmail, String userPassword) {
        HashMap<String, Object> userDataMap = new HashMap<>();
        userDataMap.put("email", userEmail);
        userDataMap.put("name", userName);
        userDataMap.put("password", userPassword);

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RootRef.child("User").child(userEmail).updateChildren(userDataMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                loadingProgressBar.dismiss();
                                Toast.makeText(SignupActivity.this, "Congratulations, your account has been created!", Toast.LENGTH_LONG).show();
                                loadingProgressBar.dismiss();
                            } else {
                                loadingProgressBar.dismiss();
                                Toast.makeText(SignupActivity.this, "Network Error: Please try again some time...", Toast.LENGTH_LONG).show();
                            }
                        });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validate(String email, String pass){
        int flag=0;
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email is invalid");
            flag=1;
        }
        if (pass.length() < 6){
            passEditText.setError("Password must be of six characters");
            flag=1;
        }
        return flag == 0;
    }
}