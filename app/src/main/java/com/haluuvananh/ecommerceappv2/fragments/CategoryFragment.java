package com.haluuvananh.ecommerceappv2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.haluuvananh.ecommerceappv2.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.haluuvananh.ecommerceappv2.activities.MainActivity;
import com.haluuvananh.ecommerceappv2.adapters.SearchAdapter;
import com.haluuvananh.ecommerceappv2.model.ProductModel;
import com.haluuvananh.ecommerceappv2.utils.FirebaseUtil;

public class CategoryFragment extends Fragment {
    RecyclerView productRecyclerView;
    SearchAdapter searchProductAdapter;
    ImageView backBtn;
    TextView labelTextView;

    String categoryName;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_category, container, false);
        labelTextView = view.findViewById(R.id.labelTextView);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        backBtn = view.findViewById(R.id.backBtn);

        assert getArguments() != null;
        categoryName = getArguments().getString("categoryName", "Electronics");

        labelTextView.setText(categoryName);
        getProducts(categoryName);

        MainActivity activity = (MainActivity) getActivity();
        activity.hideSearchBar();

        backBtn.setOnClickListener(v -> {
            activity.onBackPressed();
        });
        return view;
    }

    private void getProducts(String categoryName){
        Query query = FirebaseUtil.getProducts().whereEqualTo("category", categoryName);
        FirestoreRecyclerOptions<ProductModel> options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        searchProductAdapter = new SearchAdapter(options, getActivity());
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productRecyclerView.setAdapter(searchProductAdapter);
        searchProductAdapter.startListening();
    }
}