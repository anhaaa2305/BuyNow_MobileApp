package com.haluuvananh.ecommerce_app_v1.Fragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.haluuvananh.ecommerce_app_v1.Activities.ShowAllActivity;
import com.haluuvananh.ecommerce_app_v1.Adapters.CategoryAdapter;
import com.haluuvananh.ecommerce_app_v1.Adapters.NewProductsAdapter;
import com.haluuvananh.ecommerce_app_v1.Adapters.PopolarProductsAdapter;
import com.haluuvananh.ecommerce_app_v1.Models.CategoryModel;
import com.haluuvananh.ecommerce_app_v1.Models.NewProductModel;
import com.haluuvananh.ecommerce_app_v1.Models.PopularProductsModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {


    TextView categoryShowAll, popularShowAll, newProductShowAll;
    // Linear Layout
    LinearLayout linearLayout;
    // Progress Dialog
    ProgressDialog progressDialog;
    //Recyclerview
    RecyclerView categoryRecyclerView, newProductRecyclerView, popularRecyclerView;
    // Adapter
    CategoryAdapter categoryAdapter;
    NewProductsAdapter newProductsAdapter;
    PopolarProductsAdapter popolarProductsAdapter;
    // List Model
    List<CategoryModel> categoryModelList;
    List<NewProductModel> newProductModelList;
    List<PopularProductsModel> popularProductsModelList;
    // Firestore
    FirebaseFirestore firestore;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        // Match with xml
        progressDialog = new ProgressDialog(getActivity());
        categoryRecyclerView = root.findViewById(R.id.rec_category);
        newProductRecyclerView = root.findViewById(R.id.new_product_rec);
        popularRecyclerView = root.findViewById(R.id.popular_rec);

        categoryShowAll = root.findViewById(R.id.category_see_all);
        newProductShowAll = root.findViewById(R.id.newProducts_see_all);
        popularShowAll = root.findViewById(R.id.popular_see_all);

        // Click Listener with Button
        categoryShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                startActivity(intent);
            }
        });
        newProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                startActivity(intent);
            }
        });
        popularShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                startActivity(intent);
            }
        });
       /* View.OnClickListener showAllClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ShowAllActivity.class);
                startActivity(intent);
            }
        };
        categoryShowAll.setOnClickListener(showAllClickListener);
        newProductShowAll.setOnClickListener(showAllClickListener);
        popularShowAll.setOnClickListener(showAllClickListener);*/


        firestore = FirebaseFirestore.getInstance();

        linearLayout = root.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);
        // Image slider
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.sale_all_banner, "Discount On All Items", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.apple_sale_banner, "Discount On Apple Devices", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.sale_black_friday_png, "70% OFF", ScaleTypes.CENTER_CROP));

        // Home ImageSlider
        imageSlider.setImageList(slideModels);

        // Progress Dialog
        progressDialog.setTitle("Welcome to Buy Now");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        // Category
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        categoryRecyclerView.setAdapter(categoryAdapter);

        firestore.collection("Category").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CategoryModel categoryModel = document.toObject(CategoryModel.class);
                        categoryModelList.add(categoryModel);
                        categoryAdapter.notifyDataSetChanged();
                        linearLayout.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                } else {
                    Log.w(TAG, "Error get Documents", task.getException());
                    Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // New Products
        newProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false ));
        newProductModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getContext(), newProductModelList);
        newProductRecyclerView.setAdapter(newProductsAdapter);
        firestore.collection("NewProduct").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        NewProductModel newProductModel = document.toObject(NewProductModel.class);
                        newProductModelList.add(newProductModel);
                        newProductsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.w(TAG, "Error get Documents", task.getException());
                    Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Popular Products
        popularRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        popularProductsModelList = new ArrayList<>();
        popolarProductsAdapter = new PopolarProductsAdapter(getContext(), popularProductsModelList);
        popularRecyclerView.setAdapter(popolarProductsAdapter);

        firestore.collection("AllProducts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PopularProductsModel popularProductModel = document.toObject(PopularProductsModel.class);
                        popularProductsModelList.add(popularProductModel);
                        popolarProductsAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.w(TAG, "Error get Documents", task.getException());
                    Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
}