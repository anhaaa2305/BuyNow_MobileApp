package com.haluuvananh.ecommerceappv2.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haluuvananh.ecommerceappv2.R;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.haluuvananh.ecommerceappv2.activities.MainActivity;
import com.haluuvananh.ecommerceappv2.activities.SplashActivity;
import com.haluuvananh.ecommerceappv2.model.CartItemModel;
import com.haluuvananh.ecommerceappv2.utils.FirebaseUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class CartAdapter extends FirestoreRecyclerAdapter<CartItemModel, CartAdapter.CartViewHolder> {

    private Context context;
    private AppCompatActivity activity;
    final int[] stock = new int[1];
    int totalPrice = 0;
    boolean gotSum = false;
    int count;
    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartItemModel> options, Context context) {
        super(options);
        count = options.getSnapshots().size();
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_adapter, parent, false);
        activity = (AppCompatActivity) view.getContext();
        return new CartViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartItemModel model) {
        if (position == 0 && !gotSum) {
            calculateTotalPrice();
        }
        holder.productName.setText(model.getName());
        holder.singleProductPrice.setText("$ " + model.getPrice());
        holder.productPrice.setText("$ " + model.getPrice() * model.getQuantity());
        holder.originalPrice.setText("$ " + model.getOriginalPrice());
        holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.productQuantity.setText(model.getQuantity() + "");
        Picasso.get().load(model.getImage()).into(holder.productCartImage, new Callback() {
            @Override
            public void onSuccess() {
                if (holder.getBindingAdapterPosition() == getSnapshots().size() - 1) {
                    ShimmerFrameLayout shimmerLayout = activity.findViewById(R.id.shimmerLayout);
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    activity.findViewById(R.id.mainLinearLayout).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });

        holder.plusBtn.setOnClickListener(v -> {
            changeQuantity(model, true);
        });
        holder.minusBtn.setOnClickListener(v -> {
            changeQuantity(model, false);
        });
    }

    private void calculateTotalPrice() {
        gotSum = true;
        for (CartItemModel model : getSnapshots()) {
            totalPrice += model.getPrice() * model.getQuantity();
        }
        Intent intent = new Intent("price");
        intent.putExtra("totalPrice", totalPrice);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void changeQuantity(CartItemModel model, boolean plus) {
        FirebaseUtil.getProducts().whereEqualTo("productId", model.getProductId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            stock[0] = (int) (long) Objects.requireNonNull(document.getData().get("stock"));
                        }
                    }
                });

        FirebaseUtil.getCartItems().whereEqualTo("productId", model.getProductId())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String docId = document.getId();
                            int quantity = (int) (long) Objects.requireNonNull(document.getData().get("quantity"));
                            if (plus) {
                                if (quantity < stock[0]) {
                                    FirebaseUtil.getCartItems().document(docId).update("quantity", quantity + 1);
                                    totalPrice += model.getPrice();
                                } else
                                    Toast.makeText(context, "Max stock available: " + stock[0], Toast.LENGTH_SHORT).show();
                            } else {
                                totalPrice -= model.getPrice();
                                if (quantity > 1)
                                    FirebaseUtil.getCartItems().document(docId).update("quantity", quantity - 1);
                                else
                                    FirebaseUtil.getCartItems().document(docId)
                                            .delete().addOnCompleteListener(task1 -> {
                                            });
                            }

                            MainActivity activity = (MainActivity) context;
                            activity.addOrRemoveBadge();
                            Intent intent = new Intent("price");
                            intent.putExtra("totalPrice", totalPrice);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        }
                    }
                });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        if (getItemCount() == 0) {
            Activity activity = (Activity) context;
            ShimmerFrameLayout shimmerLayout = activity.findViewById(R.id.shimmerLayout);
            shimmerLayout.stopShimmer();
            shimmerLayout.setVisibility(View.GONE);
            activity.findViewById(R.id.mainLinearLayout).setVisibility(View.VISIBLE);
            activity.findViewById(R.id.emptyCartImageView).setVisibility(View.VISIBLE);
        } else {
            Activity activity = (Activity) context;
            activity.findViewById(R.id.emptyCartImageView).setVisibility(View.INVISIBLE);
        }
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, singleProductPrice, productQuantity, minusBtn, plusBtn, originalPrice;
        ImageView productCartImage;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.nameTextView);
            singleProductPrice = itemView.findViewById(R.id.priceTextView1);
            productPrice = itemView.findViewById(R.id.priceTextView);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            productQuantity = itemView.findViewById(R.id.quantityTextView);
            productCartImage = itemView.findViewById(R.id.productImageCart);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            plusBtn = itemView.findViewById(R.id.plusBtn);
        }
    }
}
