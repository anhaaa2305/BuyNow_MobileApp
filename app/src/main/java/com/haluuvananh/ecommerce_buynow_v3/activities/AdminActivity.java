package com.haluuvananh.ecommerce_buynow_v3.activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;

import java.util.Objects;

public class AdminActivity extends AppCompatActivity {
    private TextView countOrders, priceOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        LinearLayout logoutBtn = findViewById(R.id.logoutBtn);
        CardView addProductBtn = findViewById(R.id.addProductBtn);
        CardView modifyProductBtn = findViewById(R.id.modifyProductBtn);
        CardView addCategoryBtn = findViewById(R.id.addCategoryBtn);
        CardView modifyCategoryBtn = findViewById(R.id.modifyCategoryBtn);
        CardView addBannerBtn = findViewById(R.id.addBannerBtn);
        CardView modifyBannerBtn = findViewById(R.id.modifyBannerBtn);
        countOrders = findViewById(R.id.countOrders);
        priceOrders = findViewById(R.id.priceOrders);

        getDetails();

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        addProductBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AddProductActivity.class));
        });

        modifyProductBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ModifyProductActivity.class));
        });

        addCategoryBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AddCategoryActivity.class));
        });

        modifyCategoryBtn.setOnClickListener(v -> startActivity(new Intent(this, ModifyCategoryActivity.class)));
        addBannerBtn.setOnClickListener(v -> startActivity(new Intent(this, AddBannerActivity.class)));
        modifyBannerBtn.setOnClickListener(v -> startActivity(new Intent(this, ModifyBannerActivity.class)));
    }

    private void getDetails() {
        FirebaseUtil.getDetails().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        countOrders.setText(Objects.requireNonNull(task.getResult().get("countOfOrderedItems")).toString());
                        priceOrders.setText(Objects.requireNonNull(task.getResult().get("priceOfOrders")).toString());
                    }
                });
    }
}