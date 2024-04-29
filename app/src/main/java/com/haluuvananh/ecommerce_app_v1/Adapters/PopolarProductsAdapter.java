package com.haluuvananh.ecommerce_app_v1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haluuvananh.ecommerce_app_v1.Activities.DetailProductActivity;
import com.haluuvananh.ecommerce_app_v1.Models.PopularProductsModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.text.DecimalFormat;
import java.util.List;

public class PopolarProductsAdapter extends RecyclerView.Adapter<PopolarProductsAdapter.ViewHolder> {
    Context context;
    List<PopularProductsModel> list;

    public PopolarProductsAdapter(Context context, List<PopularProductsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(list.get(position).getImg_url()).into(holder.popularImg);
        holder.popularName.setText(list.get(position).getName());
        DecimalFormat formatter = new DecimalFormat("#,##0");
        String formattedPrice = formatter.format(list.get(position).getPrice());
        holder.popularPrice.setText(formattedPrice);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailProductActivity.class);
                intent.putExtra("detail", list.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView popularImg;
        TextView popularName, popularPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            popularImg = itemView.findViewById(R.id.popular_img);
            popularName = itemView.findViewById(R.id.popular_product_name);
            popularPrice = itemView.findViewById(R.id.popular_price);
        }
    }
}
