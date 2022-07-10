package com.antbps15545.dencafeagile.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.model.Product;
import com.antbps15545.dencafeagile.model.ProductType;
import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.ViewHolder> {
    Context context;
    List<ProductType> list;
    private OnItemTouchListener onItemTouchListener;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    public interface OnItemTouchListener {
        void onButtonEditClick(View view, int position);
        void onButtonDelClick(View view, int position);
    }

    public TypeListAdapter(Context context, List<ProductType> list, OnItemTouchListener listener) {
        this.context = context;
        this.list = list;
        this.onItemTouchListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reveal_type, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductType type = list.get(position);
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.layout, type.getTypeId());
        viewBinderHelper.closeLayout(type.getTypeId());
        holder.tv.setText(type.getTypeName());
        Glide
                .with(context)
                .load(type.getTypeImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.civ);
        holder.cvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onButtonEditClick(v, position);
            }
        });
        holder.cvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onButtonDelClick(v, position);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv, cvEdit, cvDel;
        CircleImageView civ;
        TextView tv;
        SwipeRevealLayout layout;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvTypeItem);
            civ = itemView.findViewById(R.id.civTypeItem);
            tv = itemView.findViewById(R.id.nameTypeItem);
            cvEdit = itemView.findViewById(R.id.cvEditTypeItem);
            cvDel = itemView.findViewById(R.id.cvDelTypeItem);
            layout = itemView.findViewById(R.id.swipLayoutTypeItem);
        }
    }


}
