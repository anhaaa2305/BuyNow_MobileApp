package com.haluuvananh.buynowv1.Activities.Shared;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haluuvananh.buynowv1.Activities.Admin.AdminCategoryActivity;
import com.haluuvananh.buynowv1.Model.Users;
import com.haluuvananh.buynowv1.Prevalent.Prevalent;
import com.haluuvananh.buynowv1.R;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    public EditText inputPhoneNumber, inputPassword;
    public Button loginButton;
    public ProgressDialog loadingProgressBar;
    public String parentDbName = "Users";
    public CheckBox checkBoxRememberMe;
    public TextView adminLink, notAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputPhoneNumber = findViewById(R.id.login_phone_input);
        inputPassword = findViewById(R.id.login_password_input);
        loginButton = findViewById(R.id.login_btn);
        loadingProgressBar = new ProgressDialog(this);
        checkBoxRememberMe = findViewById(R.id.remember_me_ckb);

        adminLink = findViewById(R.id.admin_panel_link);
       /* notAdminLink = findViewById(R.id.not_admin_panel_link);*/

        Paper.init(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText(R.string.login_admin);
                adminLink.setVisibility(View.INVISIBLE);
                //notAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
                inputPassword.setText("");
                inputPhoneNumber.setText("");
            }
        });

        /*notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText(R.string.login);
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });*/
    }
    private void LoginUser() {
        String phone = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter a phone number ...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password ...", Toast.LENGTH_SHORT).show();
        } else {
            loadingProgressBar.setTitle("Login processing...");
            loadingProgressBar.setMessage("Please wait, while we are checking the credentials. ");
            loadingProgressBar.setCanceledOnTouchOutside(false);
            loadingProgressBar.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(String phone, String password) {

        if (checkBoxRememberMe.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);

        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child(parentDbName).child(phone).exists())) {
                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (usersData != null) {
                        if (usersData.getPhone().equals(phone)) {
                            if (usersData.getPassword().equals(password)) {
                                if (parentDbName.equals("Admins")) {
                                    Toast.makeText(LoginActivity.this, "Welcome Admin, you are logged in Successfully", Toast.LENGTH_SHORT).show();
                                    loadingProgressBar.dismiss();
                                    Intent i = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                    startActivity(i);
                                } else if (parentDbName.equals("Users")) {
                                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    loadingProgressBar.dismiss();
                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(i);
                                }
                            } else {
                                loadingProgressBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + " number do not exist", Toast.LENGTH_SHORT).show();
                    loadingProgressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loginToRegister(View view) {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }
}