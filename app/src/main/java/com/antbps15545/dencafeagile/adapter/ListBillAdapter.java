package com.antbps15545.dencafeagile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.model.Cart;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListBillAdapter extends RecyclerView.Adapter<ListBillAdapter.ViewHolder> {
    Context context;
    List<Cart> list = new ArrayList<>();
    public ListBillAdapter(Context context, List<Cart> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_bill, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Cart cart = list.get(position);
        holder.tvName.setText(cart.getProductName());
        holder.tvAmount.setText(cart.getProductAmount()+"");
        holder.tvPrice.setText(cart.getProductPrice()+"");
        holder.tvTotal.setText(cart.getProductPrice()*cart.getProductAmount()+"");
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvPrice, tvTotal;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameItemBill);
            tvAmount = itemView.findViewById(R.id.tvAmountItemBill);
            tvPrice = itemView.findViewById(R.id.tvPriceItemBill);
            tvTotal = itemView.findViewById(R.id.tvTotalItemBill);
        }
    }
}
