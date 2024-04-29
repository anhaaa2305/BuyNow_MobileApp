package com.haluuvananh.ecommerce_app_v1.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAddressActivity extends AppCompatActivity {

    private EditText name, addressLane, city, postalCode, phoneNumber;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address_activity);

        // Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Components
        Toolbar toolbar = findViewById(R.id.add_address_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        // Match object with XML
        name = findViewById(R.id.ad_name);
        addressLane = findViewById(R.id.ad_address_lane);
        city = findViewById(R.id.ad_city);
        postalCode = findViewById(R.id.ad_postal_code);
        phoneNumber = findViewById(R.id.ad_phone);

        Button addAddressBtn = findViewById(R.id.ad_add_address_btn);

        addAddressBtn.setOnClickListener(v -> {
            String userName = name.getText().toString().trim();
            String userCity = city.getText().toString().trim();
            String userAddress = addressLane.getText().toString().trim();
            String userPostalCode = postalCode.getText().toString().trim();
            String userPhoneNumber = phoneNumber.getText().toString().trim();
            // Progress Dialog
            progressDialog.setTitle("Add address processing");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            StringBuilder final_address = new StringBuilder();
            if (!userName.isEmpty()) {
                final_address.append(userName).append(", ");
            }
            if (!userAddress.isEmpty()) {
                final_address.append(userAddress).append(", ");
            }
            if (!userCity.isEmpty()) {
                final_address.append(userCity).append(", ");
            }
            if (!userPostalCode.isEmpty()) {
                final_address.append(userPostalCode).append(", ");
            }
            if (!userPhoneNumber.isEmpty()) {
                final_address.append(userPhoneNumber).append(", ");
            }
            if (!userName.isEmpty() && !userCity.isEmpty() && !userAddress.isEmpty() && !userPostalCode.isEmpty() && !userPhoneNumber.isEmpty()) {
                Map<String, String> mapAddress = new HashMap<>();
                mapAddress.put("userAddress", final_address.toString());
                if (auth.getCurrentUser() != null) {
                    firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid()).collection("Address").add(mapAddress)
                            .addOnCompleteListener(
                            task -> {
                                progressDialog.dismiss();
                                Toast.makeText(AddAddressActivity.this, "Add Address Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AddAddressActivity.this, AddressActivity.class));
                            });
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(AddAddressActivity.this, "Please fill in all required information!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
