package com.haluuvananh.ecommerce_app_v1.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haluuvananh.ecommerce_app_v1.Adapters.MyCartAdapter;
import com.haluuvananh.ecommerce_app_v1.Models.MyCartModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    private TextView totalAmount;
    private List<MyCartModel> cartModelList;
    private MyCartAdapter cartAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Setup for toolbar
        Toolbar toolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
        // Get Data from CartAdapter
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("MyTotalAmount"));
        // Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        totalAmount = findViewById(R.id.cart_total_price);

        RecyclerView recyclerView = findViewById(R.id.my_cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList);
        recyclerView.setAdapter(cartAdapter);

        firestore.collection("AddToCart").document(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .collection(Objects.requireNonNull(auth.getCurrentUser().getEmail())).get()
                .addOnCompleteListener( task ->  {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                MyCartModel myCartModel = documentSnapshot.toObject(MyCartModel.class);
                                cartModelList.add(myCartModel);
                                cartAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                });
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int totalBill = intent.getIntExtra("totalAmount", 0);
            DecimalFormat formatter = new DecimalFormat("#,##0");
            String formattedTotal = formatter.format(totalBill);
            totalAmount.setText("Total:$ " + formattedTotal);
        }
    };
}