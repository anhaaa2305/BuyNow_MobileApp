package com.haluuvananh.ecommerce_app_v1.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.haluuvananh.ecommerce_app_v1.Adapters.AddressAdapter;
import com.haluuvananh.ecommerce_app_v1.Interfaces.SelectedAddress;
import com.haluuvananh.ecommerce_app_v1.Models.AddressModel;
import com.haluuvananh.ecommerce_app_v1.Models.Product.ProductModel;
import com.haluuvananh.ecommerce_app_v1.Models.PopularProductsModel;
import com.haluuvananh.ecommerce_app_v1.Models.ShowAllProductModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddressActivity extends AppCompatActivity implements SelectedAddress {

    private List<AddressModel> addressModelList;
    private AddressAdapter addressAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    private Toolbar toolbar;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Components
        Button paymentBtn = findViewById(R.id.payment_btn);
        Button addAddressBtn = findViewById(R.id.add_address_btn);
        RecyclerView recyclerView = findViewById(R.id.address_recycler);

        // Get Data From Details
        Object obj = getIntent().getSerializableExtra("item");


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addressModelList = new ArrayList<>();
        addressAdapter = new AddressAdapter(getApplicationContext(),addressModelList, this);
        recyclerView.setAdapter(addressAdapter);

        firestore.collection("CurrentUser").document(Objects.requireNonNull(auth.getCurrentUser()).getUid()).collection("Address").get().addOnCompleteListener(task -> {
              for (DocumentSnapshot document : task.getResult().getDocuments()) {
                  AddressModel addressModel = document.toObject(AddressModel.class) ;
                  addressModelList.add(addressModel);
                  addressAdapter.notifyDataSetChanged();
              }
        });
        // setOnClickListener
        addAddressBtn.setOnClickListener(v ->
                startActivity(new Intent(AddressActivity.this, AddAddressActivity.class))
        );

        paymentBtn.setOnClickListener(v -> {
            double amount = 0.0 ;
            if (obj instanceof ProductModel){
                ProductModel productModel = (ProductModel) obj;
                amount = productModel.getPrice();
            }
            else if (obj instanceof PopularProductsModel){
                PopularProductsModel popularProductsModel = (PopularProductsModel) obj;
                amount = popularProductsModel.getPrice();
            }
           else if (obj instanceof ShowAllProductModel){
                ShowAllProductModel allProductModel = (ShowAllProductModel) obj;
                amount = allProductModel.getPrice();
            }
           Intent intent = new Intent(AddressActivity.this, PaymentActivity.class);
           intent.putExtra("amount", amount);
           startActivity(intent);
        });
    }

    @Override
    public void setAddress(String address) {
    }
}