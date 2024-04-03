package com.haluuvananh.buynowv1.Activities.Shared;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import java.util.Objects;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    public Button joinNowBtn, loginBtn;
    public ProgressDialog loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowBtn = findViewById(R.id.main_join_now_btn);
        loginBtn = findViewById(R.id.main_login_btn);
        loadingProgressBar = new ProgressDialog(this);
        // Khởi tạo tham chiếu đến cơ sở dữ liệu
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ví dụ thêm dữ liệu vào cơ sở dữ liệu
        //mDatabase.child("users").child("userId1").child("name").setValue("John Doe");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });
        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        Paper.init(this);

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPassKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (!Objects.equals(UserPhoneKey, "") && !Objects.equals(UserPassKey, "")) {
           if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPassKey)){
               AllowAccess(UserPhoneKey, UserPassKey);
               loadingProgressBar.setTitle("Login processing ...");
               loadingProgressBar.setMessage("Please wait ....");
               loadingProgressBar.setCanceledOnTouchOutside(false);
               loadingProgressBar.show();
           }
        }
    }
    private void AllowAccess(String phone,String password){
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("Users").child(phone).exists())){
                    Users usersData = snapshot.child("Users").child(phone).getValue(Users.class);
                    if (usersData != null){
                        if (usersData.getPhone().equals(phone)) {
                            if (usersData.getPassword().equals(password)){
                                Toast.makeText(MainActivity.this, "Please wait, you are already logged in!", Toast.LENGTH_SHORT).show();
                                loadingProgressBar.dismiss();
                                Intent i = new Intent(MainActivity.this, AdminCategoryActivity.class);
                                startActivity(i);
                            }
                            else {
                                loadingProgressBar.dismiss();
                                Toast.makeText(MainActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Account with this "+ phone + " number do not exist", Toast.LENGTH_SHORT).show();
                    loadingProgressBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };
}