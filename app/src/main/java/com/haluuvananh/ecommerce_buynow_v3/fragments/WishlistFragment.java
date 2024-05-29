package com.haluuvananh.ecommerce_buynow_v3.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.activities.MainActivity;
import com.haluuvananh.ecommerce_buynow_v3.adapters.WishlistProductAdapter;
import com.haluuvananh.ecommerce_buynow_v3.model.CartItemModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;

public class WishlistFragment extends Fragment {
    RecyclerView productRecyclerView;
    WishlistProductAdapter productAdapter;
    ImageView backBtn;

    public WishlistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        MainActivity activity = (MainActivity) getActivity();

        assert activity != null;
        activity.hideSearchBar();

        productRecyclerView = view.findViewById(R.id.wishlistRecyclerView);
        backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            activity.onBackPressed();
        });

        initProducts();

        return view;
    }

    private void initProducts() {
        Query query = FirebaseUtil.getWishlistItems().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<CartItemModel> options = new FirestoreRecyclerOptions.Builder<CartItemModel>()
                .setQuery(query, CartItemModel.class)
                .build();

        productAdapter = new WishlistProductAdapter(options, getActivity());
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        productRecyclerView.setAdapter(productAdapter);
        productAdapter.startListening();
    }
}