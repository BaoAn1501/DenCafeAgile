package com.antbps15545.dencafeagile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.model.OrderState;
import com.antbps15545.dencafeagile.order.MoreDetailOrderFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class OrderApprovalAdapter extends RecyclerView.Adapter<OrderApprovalAdapter.ViewHolder> {
    Context context;
    List<OrderState> list = new ArrayList<>();
    String uid = FirebaseAuth.getInstance().getUid();
    public OrderApprovalAdapter(Context context, List<OrderState> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_approval_state, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        OrderState state = list.get(position);
        holder.tvDate.setText(state.getDate());
        holder.tvState.setText("Đang duyệt");
        holder.tvState.setTextColor(R.color.Red);
        holder.tvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreDetailOrderFragment fragment = new MoreDetailOrderFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("from",1);
                bundle.putSerializable("state", state);
                fragment.setArguments(bundle);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.frame_AdminMain, fragment, null).commit();
            }
        });
        holder.ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeStateOrder(state);
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
        ImageView ivCheck;
        TextView tvDate, tvState, tvShow;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(R.id.ivCheckItemOrderAState);
            tvDate = itemView.findViewById(R.id.tvDateItemOrderAState);
            tvState = itemView.findViewById(R.id.tvStateItemOrderAState);
            tvShow = itemView.findViewById(R.id.tvShowBillItemOrderAState);
        }
    }

    private void changeStateOrder(OrderState state){
        DatabaseReference orderstateRef = FirebaseDatabase.getInstance().getReference("list_order_state");
        DatabaseReference orderdetailRef = FirebaseDatabase.getInstance().getReference("list_order_detail");
        orderstateRef.child(state.getIdState()).child("state").setValue(true);
        orderdetailRef.child(state.getIdState()).child("state").setValue(true);
    }
}
