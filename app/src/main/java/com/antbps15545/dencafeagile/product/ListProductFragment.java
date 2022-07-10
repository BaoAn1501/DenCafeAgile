package com.antbps15545.dencafeagile.product;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antbps15545.dencafeagile.adapter.DishListAdapter;
import com.antbps15545.dencafeagile.adapter.TypeListAdapter;
import com.antbps15545.dencafeagile.model.ProductRating;
import com.antbps15545.dencafeagile.model.ProductType;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.adapter.ProductAdapter;
import com.antbps15545.dencafeagile.model.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListProductFragment extends Fragment {
    View view;
    RecyclerView rcv;
    FloatingActionButton fab;
    List<Product> list = new ArrayList<>();
    List<ProductType> tlist = new ArrayList<>();
    DishListAdapter adapter;
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 123;
    public CircleImageView civ;
    ProgressDialog progressDialog;
    StorageReference storageRef;
    private DatabaseReference databaseRef;
    int posType;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_product, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvLPF);
        fab = view.findViewById(R.id.fabLPF);
        storageRef = FirebaseStorage.getInstance().getReference("imageFolder");
        databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });
        DishListAdapter.OnItemTouchListener itemTouchListener = new DishListAdapter.OnItemTouchListener() {
            @Override
            public void onButtonEditClick(View view, int position) {
                showEditProductDialog(position);
                Toast.makeText(getContext(), "id: "+list.get(position).getProductId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onButtonDelClick(View view, int position) {
                openDialogDelete(position);
            }
        };
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        getProductsFromFirebase();
        getTypesFromFirebase();
        adapter = new DishListAdapter(getContext(), list, tlist, itemTouchListener);
        rcv.setAdapter(adapter);
    }

    private void chooseImage() {
        Intent cameraIntent = new Intent(Intent.ACTION_GET_CONTENT);
        cameraIntent.setType("image/*");
        startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            civ.setImageURI(mImageUri);
            civ.setTag("changed");
            Glide.with(this).load(mImageUri).into(civ);
        }
    }

    private void getProductsFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setProductId(postSnapshot.getKey());
                    list.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void getTypesFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product_type");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tlist.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ProductType type = postSnapshot.getValue(ProductType.class);
                    type.setTypeId(postSnapshot.getKey());
                    tlist.add(type);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void showAddProductDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product, null);
        dialog.setContentView(view);
        civ = view.findViewById(R.id.civAddProduct);
        TextInputLayout name = view.findViewById(R.id.tilNameAddProduct);
        TextInputLayout price = view.findViewById(R.id.tilPriceAddProduct);
        TextInputEditText edtName = view.findViewById(R.id.edtNameAddProduct);
        Spinner spnType = view.findViewById(R.id.spnTypeAddProduct);
        CardView cv = view.findViewById(R.id.cvAddProduct);
        ArrayAdapter<ProductType> adapter = new ArrayAdapter<ProductType>(getActivity(), android.R.layout.simple_list_item_1, tlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
        price.getEditText().setText("0");
        Object oldImage = civ.getTag();
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getEditText().getText().toString().trim().equals("")){
                    Toast.makeText(getContext(), "Bạn chưa nhập tên món", Toast.LENGTH_SHORT).show();
                } else if(price.getEditText().getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), "Bạn chưa nhập giá món", Toast.LENGTH_SHORT).show();
                } else {
                    boolean non_existed = true;
                    if(list.size()>0){
                        for(int i=0;i<list.size();i++){
                            if(edtName.getText().toString().equals(list.get(i).getProductName())){
                                non_existed = false;
                                Toast.makeText(getContext(), "Tên món đã tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    if(non_existed==true){
                        if(civ.getTag()==oldImage){
                            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.logoicon);
                            mImageUri = uri;
                        } else {

                        }
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressDialog.setMessage("Đang xử lý...");
                        progressDialog.show();
                        String key = UUID.randomUUID().toString();
                        StorageReference fileReference = storageRef.child("imageProduct/" + key);
                        fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uri.isComplete());
                                Uri url = uri.getResult();
                                String imageUrl = url.toString();
//                                Map<String, Object> newDish = new HashMap<>();
//                                newDish.put("productName", edtName.getText().toString());
//                                newDish.put("productPrice", Integer.valueOf(price.getEditText().getText().toString()));
//                                newDish.put("productImage", imageUrl);
                                for(int i=0;i<tlist.size();i++){
                                    if(tlist.get(i).getTypeName().equals(spnType.getSelectedItem().toString())){
                                        posType = i;
                                    }
                                }
//                                newDish.put("productType",tlist.get(posType).getTypeId());
                                final Product newProduct = new Product(edtName.getText().toString(), Integer.valueOf(price.getEditText().getText().toString()), imageUrl, tlist.get(posType).getTypeId());
                                databaseRef.child(key).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DatabaseReference ratingRef = FirebaseDatabase.getInstance().getReference("rating");
                                        ProductRating productRating = new ProductRating(key, 0, 0, 0);
                                        ratingRef.child(key).setValue(productRating);
                                        progressDialog.dismiss();
                                        adapter.notifyDataSetChanged();
                                        dialog.cancel();
                                        Toast.makeText(getContext(), "Đã thêm món thành công", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Thêm món thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {

                            }
                        });
                    }
                }
            }
        });
        dialog.show();
    }

    public void showEditProductDialog(int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product, null);
        dialog.setContentView(view);
        TextInputLayout name = view.findViewById(R.id.tilNameAddProduct);
        TextInputLayout price = view.findViewById(R.id.tilPriceAddProduct);
        TextInputEditText edtName = view.findViewById(R.id.edtNameAddProduct);
        Spinner spnType = view.findViewById(R.id.spnTypeAddProduct);
        CardView cv = view.findViewById(R.id.cvAddProduct);
        TextView tv = view.findViewById(R.id.tvCardAddProduct);
        civ = view.findViewById(R.id.civAddProduct);
        tv.setText("Cập nhật");
        Object oldImage = civ.getTag();
        ArrayAdapter<ProductType> adapter = new ArrayAdapter<ProductType>(getActivity(), android.R.layout.simple_list_item_1, tlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(adapter);
        for(int i=0;i<tlist.size();i++){
            if(tlist.get(i).getTypeId().equals(list.get(position).getProductType())){
                posType = i;
            }
        }
        for(int i =0;i<spnType.getCount();i++){
            if(spnType.getItemAtPosition(i).toString().equals(tlist.get(posType).getTypeName())){
                spnType.setSelection(i);
            }
        }
        price.getEditText().setText(""+list.get(position).getProductPrice());
        edtName.setText(list.get(position).getProductName());
        Glide
                .with(getContext())
                .load(list.get(position).getProductImage())
                .centerCrop()
                .into(civ);
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getEditText().getText().toString().trim().equals("")){
                    Toast.makeText(getContext(), "Bạn chưa nhập tên món", Toast.LENGTH_SHORT).show();
                } else if(price.getEditText().getText().toString().trim().equals("")) {
                    Toast.makeText(getContext(), "Bạn chưa nhập giá món", Toast.LENGTH_SHORT).show();
                } else {
                    if(civ.getTag()==oldImage){
                        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressDialog.setMessage("Đang xử lý...");
                        progressDialog.show();
                        Map<String, Object> newDish = new HashMap<>();
                        newDish.put("productName", edtName.getText().toString());
                        newDish.put("productPrice", Integer.valueOf(price.getEditText().getText().toString()));
                        for(int i=0;i<tlist.size();i++){
                            if(tlist.get(i).getTypeName().equals(spnType.getSelectedItem().toString())){
                                posType = i;
                            }
                        }
                        newDish.put("productType",tlist.get(posType).getTypeId());
                        databaseRef.child(list.get(position).getProductId()).updateChildren(newDish).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Đã cập nhật món thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Cập nhật thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressDialog.setMessage("Đang xử lý...");
                        progressDialog.show();
                        String key = UUID.randomUUID().toString();
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(list.get(position).getProductImage());
                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                StorageReference fileReference = storageRef.child("imageProduct/" + key);
                                fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                        while(!uri.isComplete());
                                        Uri url = uri.getResult();
                                        String imageUrl = url.toString();
                                        Map<String, Object> newDish = new HashMap<>();
                                        newDish.put("productName", edtName.getText().toString());
                                        newDish.put("productPrice", Integer.valueOf(price.getEditText().getText().toString()));
                                        newDish.put("productImage", imageUrl);
                                        for(int i=0;i<tlist.size();i++){
                                            if(tlist.get(i).getTypeName().equals(spnType.getSelectedItem().toString())){
                                                posType = i;
                                            }
                                        }
                                        newDish.put("productType",tlist.get(posType).getTypeId());
                                        databaseRef.child(list.get(position).getProductId()).updateChildren(newDish).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                dialog.dismiss();
                                                Toast.makeText(getContext(), "Đã cập nhật món thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Cập nhật thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(getContext(), "Thêm ảnh mới của món thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getContext(), "Xoá ảnh cũ của món thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        });
        dialog.show();
    }

    private void openDialogDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setMessage("Bạn có muốn xoá loại sản phẩm này không?");
        builder.setCancelable(false);
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dishDelete(position);
                dialog.dismiss();
            }
        }).show();
    }

    private void dishDelete(int position){
        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setMessage("Đang xoá...");
        progressDialog.show();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(list.get(position).getProductImage());
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseRef.child(list.get(position).getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), "Đã xoá món thành công", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Xoá món thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "xoá ảnh món thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
