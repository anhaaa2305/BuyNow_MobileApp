package com.haluuvananh.ecommerce_buynow_v3.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.activities.MainActivity;
import com.haluuvananh.ecommerce_buynow_v3.adapters.ProductAdapter;
import com.haluuvananh.ecommerce_buynow_v3.adapters.ReviewAdapter;
import com.haluuvananh.ecommerce_buynow_v3.model.CartItemModel;
import com.haluuvananh.ecommerce_buynow_v3.model.ProductModel;
import com.haluuvananh.ecommerce_buynow_v3.model.ReviewModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProductFragment extends Fragment {
    ImageView productImage, backBtn, shareBtn;
    TextView productName, productDescription, productSpec;
    TextView productPrice, originalPrice, discountPercentage;
    TextView ratingTextView, noOfRatingTextView;
    Button addToCartBtn;
    MaterialCardView wishlistBtn;
    ImageView wishlistImageView;
    RatingBar ratingBar;
    LottieAnimationView wishlistLottie, cartLottie;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView reviewRecyclerView, similarProductRecyclerView;
    ReviewAdapter reviewAdapter;
    ProductAdapter similarProductAdapter;
    LinearLayout mainLinearlayout;

    int productId;
    boolean wishlisted = false;

    ProductModel currentProduct = new ProductModel();

    public ProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        productImage = view.findViewById(R.id.productImage);
        productName = view.findViewById(R.id.productName);
        productPrice = view.findViewById(R.id.productPrice);
        originalPrice = view.findViewById(R.id.originalPrice);
        discountPercentage = view.findViewById(R.id.discountPercentage);
        productDescription = view.findViewById(R.id.productDescription);
        productSpec = view.findViewById(R.id.productSpecification);
        addToCartBtn = view.findViewById(R.id.addToCartBtn);
        wishlistBtn = view.findViewById(R.id.wishlistBtn);
        wishlistImageView = view.findViewById(R.id.wishlistImageView);
        backBtn = view.findViewById(R.id.backBtn);
        shareBtn = view.findViewById(R.id.shareBtn);
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingTextView = view.findViewById(R.id.ratingTextView);
        noOfRatingTextView = view.findViewById(R.id.noOfRatingTextView);

        wishlistLottie = view.findViewById(R.id.wishlistLottie);
        cartLottie = view.findViewById(R.id.cartLottie);
        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        reviewRecyclerView = view.findViewById(R.id.reviewRecyclerView);
        similarProductRecyclerView = view.findViewById(R.id.similarProductRecyclerView);
        mainLinearlayout = view.findViewById(R.id.mainLinearLayout);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        activity.hideSearchBar();
        shimmerFrameLayout.startShimmer();
        cartLottie.setVisibility(View.GONE);

        assert getArguments() != null;
        productId = getArguments().getInt("productId");
        if (getArguments().getSerializable("productObj") != null) {
            currentProduct = (ProductModel) getArguments().getSerializable("productObj");
            assert currentProduct != null;
            getProduct(currentProduct);
        } else {
            initProduct(new FirestoreCallback() {
                @Override
                public void onCallback(ProductModel productModel) {
                    currentProduct = productModel;
                    getProduct(productModel);
                }

                @Override
                public void onCallback(int stock) {

                }
            });

        }

        backBtn.setOnClickListener(v -> {
            activity.onBackPressed();
        });

        return view;
    }

    private void initProduct(FirestoreCallback callback) {
        FirebaseUtil.getProducts().whereEqualTo("productId", productId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            currentProduct = document.toObject(ProductModel.class);
                            callback.onCallback(currentProduct);
                        }
                    }
                });
    }

    private void getProduct(ProductModel currentProduct) {
        shareBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, currentProduct.getName());
            intent.putExtra(Intent.EXTRA_TEXT, currentProduct.getName() + "\n" + currentProduct.getShareLink());
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        mainLinearlayout.setVisibility(View.VISIBLE);

        Log.i("image", currentProduct.getImage());
        Picasso.get().load(currentProduct.getImage()).into(productImage);
        productName.setText(currentProduct.getName());
        int discountPerc = (currentProduct.getDiscount() * 100) / currentProduct.getOriginalPrice();
        productPrice.setText("$ " + currentProduct.getPrice());
        originalPrice.setText("$ " + currentProduct.getOriginalPrice());
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        discountPercentage.setText(discountPerc + "% OFF");

        ratingBar.setRating(currentProduct.getRating());
        ratingTextView.setText(currentProduct.getRating() + "");
        noOfRatingTextView.setText("(" + currentProduct.getNoOfRating() + ")");
        productDescription.setText(currentProduct.getDescription());
        productSpec.setText(currentProduct.getSpecification());

        FirebaseUtil.getWishlistItems().whereEqualTo("productId", currentProduct.getProductId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            wishlistImageView.setImageResource(R.drawable.ic_wishlist);
                            wishlisted = true;
                        } else
                            wishlistImageView.setImageResource(R.drawable.ic_not_wishlisted);
                    }
                });

        getReviews();
        getSimilarProducts();

        addToCartBtn.setOnClickListener(v1 -> {
            getStock(new FirestoreCallback() {
                @Override
                public void onCallback(int stock) {
                    FirebaseUtil.getCartItems().whereEqualTo("productId", currentProduct.getProductId())
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    boolean documentExists = false;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        cartLottie.cancelAnimation();
                                        documentExists = true;
                                        String docId = document.getId();
                                        int quantity = (int) (long) Objects.requireNonNull(document.getData().get("quantity"));
                                        if (quantity < stock) {
                                            FirebaseUtil.getCartItems().document(docId).update("quantity", quantity + 1);
                                            Toast.makeText(getActivity(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                            cartLottie.setVisibility(View.VISIBLE);
                                            cartLottie.playAnimation();
                                        } else
                                            Toast.makeText(getActivity(), "Max stock available: " + stock, Toast.LENGTH_SHORT).show();
                                    }
                                    if (!documentExists) {
                                        if (stock >= 1) {
                                            CartItemModel cartItem = new CartItemModel(currentProduct.getProductId(), currentProduct.getName(), currentProduct.getImage(), 1, currentProduct.getPrice(), currentProduct.getOriginalPrice(), Timestamp.now());
                                            FirebaseUtil.getCartItems().add(cartItem);
                                            cartLottie.setVisibility(View.VISIBLE);
                                            cartLottie.playAnimation();
                                            Toast.makeText(getContext(), "Added to Cart", Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(getActivity(), "Currently the item is out of stock :(", Toast.LENGTH_SHORT).show();
                                    }
                                    new Handler().postDelayed(() -> cartLottie.setVisibility(View.GONE), 2000);
                                    MainActivity activity = (MainActivity) getContext();
                                    assert activity != null;
                                    activity.addOrRemoveBadge();
                                }
                            });
                }

                @Override
                public void onCallback(ProductModel currentProduct) {

                }
            });
        });

        wishlistBtn.setOnClickListener(v -> {
            if (!wishlisted) {
                wishlistImageView.setVisibility(View.GONE);
                CartItemModel wishlistItem = new CartItemModel(currentProduct.getProductId(), currentProduct.getName(), currentProduct.getImage(), 1, currentProduct.getPrice(), currentProduct.getOriginalPrice(), Timestamp.now());
                FirebaseUtil.getWishlistItems().add(wishlistItem);
                Toast.makeText(getActivity(), "Added to Wishlist", Toast.LENGTH_SHORT).show();
                wishlistLottie.playAnimation();
                wishlisted = true;
            } else {
                Toast.makeText(getActivity(), "Product is already in your wishlist!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStock(FirestoreCallback callback) {
        FirebaseUtil.getProducts().whereEqualTo("productId", currentProduct.getProductId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int stock = (int) (long) document.getData().get("stock");
                            callback.onCallback(stock);
                        }
                    }
                });
    }

    private void getReviews() {
        Query query = FirebaseUtil.getReviews(currentProduct.getProductId()).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ReviewModel> options = new FirestoreRecyclerOptions.Builder<ReviewModel>()
                .setQuery(query, ReviewModel.class)
                .build();

        reviewAdapter = new ReviewAdapter(options, getActivity());
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewAdapter.startListening();
    }

    private void getSimilarProducts() {
        Query query = FirebaseUtil.getProducts().where(Filter.and(
                Filter.or(
                        Filter.arrayContainsAny("searchKey", currentProduct.getSearchKey()),
                        Filter.equalTo("category", currentProduct.getCategory())
                ),
                Filter.notEqualTo("productId", currentProduct.getProductId()))).limit(8);
        FirestoreRecyclerOptions<ProductModel> options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        similarProductAdapter = new ProductAdapter(options, getActivity());
        similarProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.width = (int) (getWidth() / 2.2);
                return true;
            }
        });
        similarProductRecyclerView.setAdapter(similarProductAdapter);
        similarProductAdapter.startListening();
    }

    public interface FirestoreCallback {
        void onCallback(ProductModel currentProduct);

        void onCallback(int stock);
    }

    public static ProductFragment newInstance(int productId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt("productId", productId);
        fragment.setArguments(args);

        return fragment;
    }

    public static ProductFragment newInstance(ProductModel product) {
        ProductFragment fragment = new ProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productObj", product);
        fragment.setArguments(bundle);

        return fragment;
    }
}