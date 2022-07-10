package com.antbps15545.dencafeagile.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.model.Product;
import com.antbps15545.dencafeagile.product.DetailProductFragment;

import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ViewHolder> {
    Context context;
    List<Product> list;
    public SearchProductAdapter(Context context, List<Product> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getProductName());
        holder.tvPrice.setText(product.getProductPrice()+"");
        Glide
                .with(context)
                .load(product.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.imageView);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailProductFragment fragment = new DetailProductFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_UserMain, fragment, null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView tvName, tvPrice;
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvItemProduct);
            tvName = itemView.findViewById(R.id.tvNameItemProduct);
            tvPrice = itemView.findViewById(R.id.tvPriceItemProduct);
            imageView = itemView.findViewById(R.id.ivItemProduct);
        }
    }
}