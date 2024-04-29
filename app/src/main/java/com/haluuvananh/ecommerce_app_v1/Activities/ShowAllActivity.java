package com.haluuvananh.ecommerce_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.haluuvananh.ecommerce_app_v1.Adapters.ShowAllAdapter;
import com.haluuvananh.ecommerce_app_v1.Models.ShowAllProductModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowAllActivity extends AppCompatActivity {

    // RecyclerView
    RecyclerView recyclerView;
    ShowAllAdapter showAllAdapter;
    List<ShowAllProductModel> showAllProductModelList;
    Toolbar toolbar;
    ShowAllProductModel showAllProductModel = null;
    // Firebase
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);
        String category = getIntent().getStringExtra("type");
        firestore = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.show_all_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener( v -> {
            finish();
        });
        recyclerView = findViewById(R.id.show_all_rec);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        showAllProductModelList = new ArrayList<>();
        showAllAdapter = new ShowAllAdapter(this, showAllProductModelList);
        recyclerView.setAdapter(showAllAdapter);

        if (category == null || category.isEmpty()) {
            firestore.collection("ShowAllProducts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    ShowAllProductModel showAllProductModel = documentSnapshot.toObject(ShowAllProductModel.class);
                                    showAllProductModelList.add(showAllProductModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        } else {
            firestore.collection("ShowAllProducts").whereEqualTo("category", category)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                    ShowAllProductModel showAllProductModel = documentSnapshot.toObject(ShowAllProductModel.class);
                                    showAllProductModelList.add(showAllProductModel);
                                    showAllAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }

    }
}