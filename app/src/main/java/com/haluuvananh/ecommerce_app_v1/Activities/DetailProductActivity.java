package com.haluuvananh.ecommerce_app_v1.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haluuvananh.ecommerce_app_v1.Models.Product.ProductModel;
import com.haluuvananh.ecommerce_app_v1.Models.PopularProductsModel;
import com.haluuvananh.ecommerce_app_v1.Models.ShowAllProductModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class DetailProductActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ImageView detailImg;
    TextView rating, name, description, price, quantity;
    Button addToCart, buyNow;
    ImageView addItems, removeItems;
    // New Products
    ProductModel productModel = null;
    // Popular Products
    PopularProductsModel popularProductsModel = null;
    // All Products
    ShowAllProductModel allProductModel = null;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    int totalQuantity = 1;
    int totalPrice = 0;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.product_detail_tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener( v -> {
            finish();
        });
        final Object obj = getIntent().getSerializableExtra("detail");
        progressDialog = new ProgressDialog(this);
        if (obj instanceof ProductModel) {
            productModel = (ProductModel) obj;   // Cast obj to NewProduct
        } else if (obj instanceof PopularProductsModel) {
            popularProductsModel = (PopularProductsModel) obj;
        } else if (obj instanceof ShowAllProductModel) {
            allProductModel = (ShowAllProductModel) obj;
        } else {
            return;
        }

        // Match Xml with Object
        detailImg = findViewById(R.id.detail_product_img);
        name = findViewById(R.id.detail_product_name);
        description = findViewById(R.id.detail_product_description);
        price = findViewById(R.id.detail_product_price);
        rating = findViewById(R.id.detail_product_rating);
        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);
        addToCart = findViewById(R.id.detail_button_add_to_cart);
        buyNow = findViewById(R.id.detail_button_buy_now);
        quantity = findViewById(R.id.detail_product_quantity);

        // NewProducts Detail
        if (productModel != null) {
            Glide.with(getApplicationContext()).load(productModel.getImg_url()).into(detailImg);
            name.setText(productModel.getName());
            description.setText(productModel.getDescription());
            // Format Price
            DecimalFormat formatter = new DecimalFormat("#,##0");
            totalPrice = productModel.getPrice() * totalQuantity;
            String formattedPrice = formatter.format(totalPrice);
            price.setText(formattedPrice);
            rating.setText(productModel.getRating());
        }

        // PopularProducts Detail
        if (popularProductsModel != null) {
            Glide.with(getApplicationContext()).load(popularProductsModel.getImg_url()).into(detailImg);
            name.setText(popularProductsModel.getName());
            description.setText(popularProductsModel.getDescription());
            // Format Price
            DecimalFormat formatter = new DecimalFormat("#,##0");
            totalPrice = popularProductsModel.getPrice();
            String formattedPrice = formatter.format(totalPrice);
            price.setText(formattedPrice);
            rating.setText(popularProductsModel.getRating());
        }

        // All Products Detail
        if (allProductModel != null) {
            Glide.with(getApplicationContext()).load(allProductModel.getImg_url()).into(detailImg);
            name.setText(allProductModel.getName());
            description.setText(allProductModel.getDescription());
            // Format Price
            DecimalFormat formatter = new DecimalFormat("#,##0");
            totalPrice = allProductModel.getPrice();
            String formattedPrice = formatter.format(totalPrice);
            price.setText(formattedPrice);
            rating.setText(allProductModel.getRating());
        }

        // Buy Now
        buyNow.setOnClickListener(v -> {
            // Progress Dialog
            progressDialog.setTitle("Buy processing");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            Intent intent = new Intent(DetailProductActivity.this, AddressActivity.class);

            if (productModel != null) {
                intent.putExtra("item", productModel);
            }
            if (popularProductsModel != null){
                intent.putExtra("item", popularProductsModel);
            }
            if (allProductModel != null) {
                intent.putExtra("item", allProductModel);
            }
            startActivity(intent);
            progressDialog.dismiss();
        });
        // Add Product to Cart
        addToCart.setOnClickListener(v -> {
            // Progress Dialog
            progressDialog.setTitle("Add to Cart processing");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            addToCart();
        });
        addItems.setOnClickListener(v -> {

            if (totalQuantity < 10) {
                totalQuantity++;
                quantity.setText(String.valueOf(totalQuantity));
                if (productModel != null) {
                    totalPrice = productModel.getPrice() * totalQuantity;
                }
                if (popularProductsModel != null) {
                    totalPrice = popularProductsModel.getPrice() * totalQuantity;
                }
                if (allProductModel != null) {
                    totalPrice = allProductModel.getPrice() * totalQuantity;
                }
            }
        });
        removeItems.setOnClickListener(v -> {
            if (totalQuantity > 1) {
                totalQuantity--;
                quantity.setText(String.valueOf(totalQuantity));
            }
        });
    }

    private void addToCart() {
        String saveCurrentTime;
        String saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());
        final HashMap<String, Object> _cartMap = new HashMap<>();
        _cartMap.put("productName", name.getText().toString());
        _cartMap.put("productPrice", price.getText().toString());
        _cartMap.put("currentTime", saveCurrentTime);
        _cartMap.put("currentDate", saveCurrentDate);
        _cartMap.put("totalQuantity", totalQuantity);
        _cartMap.put("totalPrice", totalPrice);

        try {
            if (auth.getCurrentUser() != null) {
                firestore.collection("AddToCart").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection(Objects.requireNonNull(auth.getCurrentUser().getEmail())).add(_cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailProductActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });
            }
        } catch (Exception e) {
            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
            progressDialog.dismiss();
        }

    }

}