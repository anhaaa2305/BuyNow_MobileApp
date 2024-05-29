package com.haluuvananh.ecommerce_buynow_v3.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.activities.MainActivity;
import com.haluuvananh.ecommerce_buynow_v3.model.OrderItemModel;
import com.haluuvananh.ecommerce_buynow_v3.model.ProductModel;
import com.haluuvananh.ecommerce_buynow_v3.model.ReviewModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderDetailsFragment extends Fragment {
    TextView productNameTextView, orderIdTextView, nameTextView, emailTextView, phoneTextView, addressTextView, commentTextView;
    ImageView productImageView;
    RatingBar ratingBar;
    TextInputEditText titleReviewEditText, reviewEditText;
    Button submitBtn;
    ImageView backBtn;
    LinearLayout productLinearLayout;
    OrderItemModel orderItem;
    ProductModel productModel;
    ReviewModel oldReviewModel;
    SweetAlertDialog dialog;

    boolean isNew = true;

    public OrderDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);

        productImageView = view.findViewById(R.id.productImage);
        productNameTextView = view.findViewById(R.id.productName);
        orderIdTextView = view.findViewById(R.id.orderIdTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        phoneTextView = view.findViewById(R.id.phoneTextView);
        addressTextView = view.findViewById(R.id.addressTextView);
        commentTextView = view.findViewById(R.id.commentTextView);
        productLinearLayout = view.findViewById(R.id.productLinearLayout);

        ratingBar = view.findViewById(R.id.ratingBar);
        titleReviewEditText = view.findViewById(R.id.titleReviewEditText);
        reviewEditText = view.findViewById(R.id.reviewEditText);
        backBtn = view.findViewById(R.id.backBtn);
        submitBtn = view.findViewById(R.id.submitBtn);

        MainActivity activity = (MainActivity) getActivity();
        Objects.requireNonNull(activity).hideSearchBar();

        backBtn.setOnClickListener(v -> activity.onBackPressed());

        assert getArguments() != null;
        int oid = getArguments().getInt("orderId");

        dialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Loading...");
        dialog.setCancelable(false);

        dialog.show();

        initProduct(oid, new FirestoreCallback() {
            @Override
            public void onCallback(OrderItemModel orderItem) {
                Picasso.get().load(orderItem.getImage()).into(productImageView);

                orderIdTextView.setText(orderItem.getOrderId());
                productNameTextView.setText(orderItem.getName());
                nameTextView.setText(orderItem.getFullName());
                emailTextView.setText(orderItem.getEmail());
                phoneTextView.setText(orderItem.getPhoneNumber());
                addressTextView.setText(orderItem.getAddress());
                commentTextView.setText(orderItem.getComments());

                initReview();
                initSubmitBtn(new FirestoreCallback() {
                    @Override
                    public void onCallback(String productDocId, ProductModel productModel) {
                        submitBtn.setOnClickListener(v -> {
                            float rating = ratingBar.getRating();
                            if (rating == 0){
                                Toast.makeText(activity, "Please select the rating", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ReviewModel review = new ReviewModel(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName(), rating, Objects.requireNonNull(titleReviewEditText.getText()).toString(), Objects.requireNonNull(reviewEditText.getText()).toString(), Timestamp.now());
                            FirebaseUtil.getReviews(orderItem.getProductId()).document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).set(review);

                            if (isNew){
                                int newNoOfRating = productModel.getNoOfRating() + 1;
                                float newRating = (productModel.getRating() + rating) / newNoOfRating;
                                FirebaseUtil.getProducts().document(productDocId).update("rating", newRating);
                                FirebaseUtil.getProducts().document(productDocId).update("noOfRating", newNoOfRating);
                            } else {
                                float newRating = (productModel.getRating() - oldReviewModel.getRating() + rating) / productModel.getNoOfRating();
                                FirebaseUtil.getProducts().document(productDocId).update("rating", newRating);
                            }

                            Toast.makeText(activity, "Review saved successfully!", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onCallback(OrderItemModel orderItem) {

                    }
                });
            }

            @Override
            public void onCallback(String productDocId, ProductModel productModel) {

            }
        });

        productLinearLayout.setOnClickListener(v -> {
            Fragment fragment = ProductFragment.newInstance(orderItem.getProductId());
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment).addToBackStack(null).commit();
        });

        return view;
    }

    private void initSubmitBtn(FirestoreCallback callback) {
        FirebaseUtil.getProducts().whereEqualTo("productId", orderItem.getProductId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            productModel = document.toObject(ProductModel.class);
                            String productDocId = document.getId();

                            callback.onCallback(productDocId, productModel);
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void initReview() {
        FirebaseFirestore.getInstance().collection("reviews").document(orderItem.getProductId()+"").collection("review")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            isNew = false;
                            oldReviewModel = document.toObject(ReviewModel.class);
                            assert oldReviewModel != null;
                            ratingBar.setRating(oldReviewModel.getRating());
                            titleReviewEditText.setText(oldReviewModel.getTitle());
                            reviewEditText.setText(oldReviewModel.getReview());
                            submitBtn.setText("Edit review");
                        }
                    }
                });
    }

    private void initProduct(int orderId, FirestoreCallback callback) {
        FirebaseUtil.getOrderItems().whereEqualTo("orderId", orderId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            orderItem = document.toObject(OrderItemModel.class);

                            callback.onCallback(orderItem);
                        }
                    }
                });
    }

    public interface FirestoreCallback {
        void onCallback(OrderItemModel orderItem);
        void onCallback(String productDocId, ProductModel productModel);
    }
}