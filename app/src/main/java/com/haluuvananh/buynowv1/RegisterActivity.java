package com.haluuvananh.buynowv1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    public Button createAccountButton;
    public EditText inputName, inputPhoneNumber, inputAddress, inputPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = findViewById(R.id.register_btn);
        inputName = findViewById(R.id.register_username_input);
        inputAddress = findViewById(R.id.register_address_input);
        inputPhoneNumber = findViewById(R.id.register_phone_input);
        inputPassword = findViewById(R.id.register_password_input);

    }
}