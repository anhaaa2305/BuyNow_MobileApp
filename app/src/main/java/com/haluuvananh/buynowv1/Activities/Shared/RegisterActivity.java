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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haluuvananh.buynowv1.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    public Button createAccountButton;
    public EditText inputName, inputPhoneNumber, inputAddress, inputPassword;

    public ProgressDialog loadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createAccountButton = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputAddress = findViewById(R.id.register_address_input);
        inputPhoneNumber = findViewById(R.id.login_phone_input);
        inputPassword = findViewById(R.id.register_password_input);
        loadingProgressBar = new ProgressDialog(this);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount(){
        String name = inputName.getText().toString();
        String phone = inputPhoneNumber.getText().toString();
        String address = inputAddress.getText().toString();
        String password = inputPassword.getText().toString();

        // Validate input data
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please enter your name ...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Please enter your phone number ...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(address)){
            Toast.makeText(this, "Please enter your address ...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password ...", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingProgressBar.setTitle("Create Account");
            loadingProgressBar.setMessage("Please wait, while we are checking the credentials. ");
            loadingProgressBar.setCanceledOnTouchOutside(false);
            loadingProgressBar.show();

            ValidatePhoneNumber(name, phone, address, password);
        }
    }

    private void ValidatePhoneNumber(String name,String phone,String address,String password){
           final DatabaseReference RootRef;
           RootRef = FirebaseDatabase.getInstance().getReference();

           RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if ((!snapshot.child("Users").child(phone).exists())){
                       HashMap<String, Object> userDataMap = new HashMap<String, Object>();
                       userDataMap.put("name", name);
                       userDataMap.put("phone", phone);
                       userDataMap.put("address", address);
                       userDataMap.put("password", password); 
                       
                       RootRef.child("Users").child(phone).updateChildren(userDataMap)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()) {
                                           Toast.makeText(RegisterActivity.this, "Congratulations, your account has been created!", Toast.LENGTH_SHORT).show();
                                           loadingProgressBar.dismiss();
                                           clearInput();
                                           Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                           startActivity(i) ;
                                       }
                                       else {
                                           loadingProgressBar.dismiss();
                                           Toast.makeText(RegisterActivity.this, "Network Error: Please try again some time...", Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                   } else {
                       Toast.makeText(RegisterActivity.this, "This " + phone + "already exist!", Toast.LENGTH_SHORT).show();
                       loadingProgressBar.dismiss();
                       inputPhoneNumber.setText("");
                       Toast.makeText(RegisterActivity.this, "Please try again using another phone number", Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });

    }

    private void clearInput() {
        inputName.setText("");
        inputAddress.setText("");
        inputPassword.setText("");
        inputPhoneNumber.setText("");
    }
}