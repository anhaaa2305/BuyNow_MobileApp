package com.haluuvananh.ecommerce_buynow_v3.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.activities.MainActivity;
import com.haluuvananh.ecommerce_buynow_v3.activities.SplashActivity;
import com.haluuvananh.ecommerce_buynow_v3.adapters.OrderListAdapter;
import com.haluuvananh.ecommerce_buynow_v3.model.OrderItemModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;

public class ProfileFragment extends Fragment {
    RecyclerView orderRecyclerView;
    OrderListAdapter orderAdapter;
    LinearLayout logoutBtn;
    TextView userNameTextView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        userNameTextView = view.findViewById(R.id.userNameTextView);

        userNameTextView.setText("Hello, " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        getOrderProducts();

        MainActivity activity = (MainActivity) getActivity();

        assert activity != null;
        activity.hideSearchBar();

        return view;
    }

    private void getOrderProducts() {
        Query query = FirebaseUtil.getOrderItems().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<OrderItemModel> options = new FirestoreRecyclerOptions.Builder<OrderItemModel>()
                .setQuery(query, OrderItemModel.class)
                .build();

        orderAdapter = new OrderListAdapter(options, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        orderRecyclerView.setLayoutManager(manager);
        orderRecyclerView.setAdapter(orderAdapter);
        orderAdapter.startListening();
    }
}