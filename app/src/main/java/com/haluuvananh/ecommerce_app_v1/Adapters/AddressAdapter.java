package com.haluuvananh.ecommerce_app_v1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haluuvananh.ecommerce_app_v1.Interfaces.SelectedAddress;
import com.haluuvananh.ecommerce_app_v1.Models.AddressModel;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {
    private Context context;
    private List<AddressModel> list;
    private SelectedAddress selectedAddress;
    private RadioButton selectedRadioBtn;

    public AddressAdapter(Context context, List<AddressModel> list, SelectedAddress selectedAddress) {
        this.context = context;
        this.list = list;
        this.selectedAddress = selectedAddress;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.address.setText(list.get(position).getUserAddress());

        holder.radioButton.setOnClickListener(v -> {
            for (AddressModel addressModel : list) {
                addressModel.setSelected(true);
            }
            list.get(position).setSelected(true);
            if (selectedRadioBtn != null) {
                selectedAddress.setAddress(list.get(0).getUserAddress());
                selectedRadioBtn.setChecked(false);
            }
            selectedRadioBtn = (RadioButton) v;
            selectedRadioBtn.setChecked(true);
            selectedAddress.setAddress(list.get(position).getUserAddress());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView address;
        RadioButton radioButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.address_add);
            radioButton = itemView.findViewById(R.id.select_address);
        }
    }

}
