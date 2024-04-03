package com.haluuvananh.buynowv1.Activities.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.haluuvananh.buynowv1.R;

public class AdminCategoryActivity extends AppCompatActivity {
    public ImageView tShirts, sportsTShirts, femaleDresses, sweatheres;
    public ImageView glasses, hatsCaps, walletBagsPurses, shoes;
    public ImageView headPhonesHandFree, laptops, watches, mobilePhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        // Match with Layout activity
        tShirts = findViewById(R.id.t_shirts);
        sportsTShirts = findViewById(R.id.sport_shirts);
        femaleDresses = findViewById(R.id.female_dresses);
        sweatheres = findViewById(R.id.sweather);

        glasses = findViewById(R.id.glasses);
        hatsCaps = findViewById(R.id.hats_caps);
        walletBagsPurses = findViewById(R.id.purses_bags_wallets);
        shoes = findViewById(R.id.shoes);

        headPhonesHandFree = findViewById(R.id.headphoness_handfree);
        laptops = findViewById(R.id.laptops_pc);
        watches = findViewById(R.id.watches);
        mobilePhones = findViewById(R.id.mobile_phones);

        tShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "T Shirts");
                startActivity(i);
            }
        });

        sportsTShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "SportsTShirts");
                startActivity(i);
            }
        });

        femaleDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Female Dresses");
                startActivity(i);
            }
        });

        sweatheres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Sweatheres");
                startActivity(i);
            }
        });

        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Glasses");
                startActivity(i);
            }
        });

        hatsCaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Hats caps");
                startActivity(i);
            }
        });

        walletBagsPurses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Wallet Bags Purses");
                startActivity(i);
            }
        });

        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Shoes");
                startActivity(i);
            }
        });

        headPhonesHandFree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "HeadPhone Hand Free");
                startActivity(i);
            }
        });

        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Laptops");
                startActivity(i);
            }
        });

        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Watches");
                startActivity(i);
            }
        });

        mobilePhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                i.putExtra("category", "Mobile Phones");
                startActivity(i);
            }
        });
    }
}