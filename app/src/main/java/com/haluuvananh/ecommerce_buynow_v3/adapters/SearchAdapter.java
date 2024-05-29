package com.haluuvananh.ecommerce_buynow_v3.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.activities.MainActivity;
import com.haluuvananh.ecommerce_buynow_v3.fragments.ProductFragment;
import com.haluuvananh.ecommerce_buynow_v3.model.CartItemModel;
import com.haluuvananh.ecommerce_buynow_v3.model.ProductModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;
import com.squareup.picasso.Picasso;

public class SearchAdapter extends FirestoreRecyclerAdapter<ProductModel, SearchAdapter.SearchViewHolder> {
    private final Context context;
    private AppCompatActivity activity;

    public SearchAdapter(@NonNull FirestoreRecyclerOptions<ProductModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_adapter,parent,false);
        activity = (AppCompatActivity) view.getContext();
        return new SearchViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchViewHolder holder, int position, @NonNull ProductModel product) {
        holder.productNameTextView.setText(product.getName());
        Picasso.get().load(product.getImage()).into(holder.productImageView);
        holder.productPriceTextView.setText("Rs. "+ product.getPrice());
        holder.originalPrice.setText("$ " + product.getOriginalPrice());
        holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        int discountPerc = (product.getDiscount() * 100) / product.getOriginalPrice();
        holder.discountPercentage.setText(discountPerc + "% OFF");
        holder.ratingBar.setRating(product.getRating());
        holder.ratingTextView.setText(product.getRating() + "");
        holder.noOfRatingTextView.setText("(" + product.getNoOfRating() + ")");

        holder.productLinearLayout.setOnClickListener(v -> {
            Fragment fragment = ProductFragment.newInstance(product);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment).addToBackStack(null).commit();
        });

        holder.addToCartBtn.setOnClickListener(v -> {
            addToCart(product, stock -> FirebaseUtil.getCartItems().whereEqualTo("productId", product.getProductId())
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean documentExists = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentExists = true;
                                String docId = document.getId();
                                int quantity = (int) (long) document.getData().get("quantity");
                                if (quantity < stock) {
                                    FirebaseUtil.getCartItems().document(docId).update("quantity", quantity + 1);
                                    Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(context, "Max stock available: " + stock, Toast.LENGTH_SHORT).show();
                            }
                            if (!documentExists) {
                                CartItemModel cartItem = new CartItemModel(product.getProductId(), product.getName(), product.getImage(), 1, product.getPrice(), product.getOriginalPrice(), Timestamp.now());
                                FirebaseUtil.getCartItems().add(cartItem);
                                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
                            }
                            MainActivity activity = (MainActivity) context;
                            activity.addOrRemoveBadge();
                        }
                    }));
        });

    }

    private void addToCart(ProductModel product, WishlistProductAdapter.MyCallback myCallback) {

        FirebaseUtil.getProducts().whereEqualTo("productId", product.getProductId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int stock = (int) (long) document.getData().get("stock");
                                myCallback.onCallback(stock);
                            }
                        }
                    }
                });
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView, productPriceTextView, originalPrice, discountPercentage;
        ImageView productImageView;
        LinearLayout productLinearLayout;
        Button addToCartBtn;
        RatingBar ratingBar;
        TextView ratingTextView, noOfRatingTextView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.productImage);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            discountPercentage =itemView.findViewById(R.id.discountPercentage);
            productLinearLayout = itemView.findViewById(R.id.productLinearLayout);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingTextView = itemView.findViewById(R.id.ratingTextView);
            noOfRatingTextView = itemView.findViewById(R.id.noOfRatingTextView);
        }
    }
}
