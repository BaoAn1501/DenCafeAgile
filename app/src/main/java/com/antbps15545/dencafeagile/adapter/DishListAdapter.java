package com.antbps15545.dencafeagile.adapter;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DishListAdapter extends RecyclerView.Adapter<DishListAdapter.ViewHolder> {
    Context context;
    List<Product> list;
    List<ProductType> tlist;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    int pos = 0;
    private OnItemTouchListener onItemTouchListener;

    public interface OnItemTouchListener {
        void onButtonEditClick(View view, int position);

        void onButtonDelClick(View view, int position);
    }

    public DishListAdapter(Context context, List<Product> list, List<ProductType> tlist, OnItemTouchListener listener) {
        this.context = context;
        this.list = list;
        this.tlist = tlist;
        this.onItemTouchListener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reveal_dish, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getProductName());
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.layout, product.getProductId());
        viewBinderHelper.closeLayout(product.getProductId());
        if (tlist.size() > 0) {
            for (int i = 0; i < tlist.size(); i++) {
                if (product.getProductType().equals(tlist.get(i).getTypeId())) {
                    pos = i;
                }
            }
            holder.tvType.setText(tlist.get(pos).getTypeName());
        }
        holder.tvPrice.setText(product.getProductPrice() + " đ");
        Glide
                .with(context)
                .load(product.getProductImage())
                .centerCrop()
                .placeholder(R.drawable.logoicon)
                .into(holder.civ);
        holder.cvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                openDialogDelete(position);
                onItemTouchListener.onButtonDelClick(v, position);
            }
        });
        holder.cvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemTouchListener.onButtonEditClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv, cvEdit, cvDel;
        public CircleImageView civ;
        TextView tvPrice, tvName, tvType;
        SwipeRevealLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvDishItem);
            civ = itemView.findViewById(R.id.civDishItem);
            tvName = itemView.findViewById(R.id.nameDishItem);
            tvPrice = itemView.findViewById(R.id.priceDishItem);
            tvType = itemView.findViewById(R.id.typeDishItem);
            cvEdit = itemView.findViewById(R.id.cvEditDishItem);
            cvDel = itemView.findViewById(R.id.cvDelDishItem);
            layout = itemView.findViewById(R.id.swipLayoutDishItem);
        }
    }
}
