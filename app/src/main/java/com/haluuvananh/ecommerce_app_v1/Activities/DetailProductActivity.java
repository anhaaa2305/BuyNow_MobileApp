package com.haluuvananh.ecommerce_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
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
import com.haluuvananh.ecommerce_app_v1.Models.NewProductModel;
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
    NewProductModel newProductModel = null;
    // Popular Products
    PopularProductsModel popularProductsModel = null;
    // All Products
    ShowAllProductModel allProductModel = null;
    FirebaseFirestore firestore;
    FirebaseAuth auth;

    int totalQuantity = 1;
    int totalPrice = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        final Object obj = getIntent().getSerializableExtra("detail");
        progressDialog = new ProgressDialog(this);
        if (obj instanceof NewProductModel) {
            newProductModel = (NewProductModel) obj;   // Cast obj to NewProduct
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
        if (newProductModel != null) {
            Glide.with(getApplicationContext()).load(newProductModel.getImg_url()).into(detailImg);
            name.setText(newProductModel.getName());
            description.setText(newProductModel.getDescription());
            // Format Price
            DecimalFormat formatter = new DecimalFormat("#,##0");
            totalPrice = newProductModel.getPrice() * totalQuantity;
            String formattedPrice = formatter.format(totalPrice);
            price.setText(formattedPrice);
            rating.setText(newProductModel.getRating());
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

        // Add Product to Cart
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Progress Dialog
                progressDialog.setTitle("Add to Cart processing");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                addToCart();
            }
        });
        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (totalQuantity < 10) {
                    totalQuantity ++;
                    quantity.setText(String.valueOf(totalQuantity));
                    if (newProductModel != null) {
                        totalPrice = newProductModel.getPrice() * totalQuantity;
                    }
                    if (popularProductsModel != null) {
                        totalPrice = popularProductsModel.getPrice() * totalQuantity;
                    }
                    if (allProductModel != null){
                        totalPrice = allProductModel.getPrice() * totalQuantity;
                    }
                }
            }
        });
        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity > 1) {
                    totalQuantity --;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
    }

    private void addToCart() {
        String saveCurrentTime;
        String saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final HashMap<String, Object> _cartMap = new HashMap<>();
        _cartMap.put("productName", name.getText().toString());
        _cartMap.put("productPrice", price.getText().toString());
        _cartMap.put("currentTime", saveCurrentTime);
        _cartMap.put("currentDate", saveCurrentDate);
        _cartMap.put("totalQuantity", totalQuantity);
        _cartMap.put("totalPrice", totalPrice);

        firestore.collection("AddToCart").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection("User").add(_cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(DetailProductActivity.this, "Add to Cart Successfully", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        });
    }

}