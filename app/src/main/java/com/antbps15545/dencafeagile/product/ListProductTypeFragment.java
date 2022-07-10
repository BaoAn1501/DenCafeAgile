package com.antbps15545.dencafeagile.product;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antbps15545.dencafeagile.adapter.TypeListAdapter;
import com.antbps15545.dencafeagile.home.AMProductFragment;
import com.antbps15545.dencafeagile.model.Product;
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
import com.antbps15545.dencafeagile.adapter.ProductTypeAdapter;
import com.antbps15545.dencafeagile.model.ProductType;
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

public class ListProductTypeFragment extends Fragment {
    View view;
    RecyclerView rcv;
    FloatingActionButton fab;
    List<ProductType> list = new ArrayList<>();
    List<Product> plist = new ArrayList<>();
    private Uri mImageUri;
    private static final int PICK_IMAGE_REQUEST = 123;
    public CircleImageView civ;
    ProgressDialog progressDialog;
    StorageReference storageRef;
    private StorageTask storageTask;
    private DatabaseReference databaseRef;
    TypeListAdapter adapter;
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list_producttype, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rcv = view.findViewById(R.id.rcvLPTF);
        fab = view.findViewById(R.id.fabLPTF);
        storageRef = FirebaseStorage.getInstance().getReference("imageFolder");
        databaseRef = FirebaseDatabase.getInstance().getReference("list_product_type");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rcv.setLayoutManager(manager);
        getTypesFromFirebase();
        getProductsFromFirebase();
        TypeListAdapter.OnItemTouchListener itemTouchListener = new TypeListAdapter.OnItemTouchListener() {
            @Override
            public void onButtonEditClick(View view, int position) {
                showEditTypeDialog(position);
            }

            @Override
            public void onButtonDelClick(View view, int position) {
                openDialogDelete(position);
            }
        };
        adapter = new TypeListAdapter(getContext(), list, itemTouchListener);
        rcv.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddTypeDialog();
            }
        });
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

    private void getTypesFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product_type");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ProductType type = postSnapshot.getValue(ProductType.class);
                    type.setTypeId(postSnapshot.getKey());
                    list.add(type);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void getProductsFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_product");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                plist.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Product product = postSnapshot.getValue(Product.class);
                    product.setProductId(postSnapshot.getKey());
                    plist.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    // mở dialog xử lý thêm loại món.
    private void showAddTypeDialog(){
        mImageUri = null;
        // Tạo bottom sheet dialog
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product_type, null);
        dialog.setContentView(view);
        civ = view.findViewById(R.id.ivAddType);
        TextInputLayout name = view.findViewById(R.id.tilNameAddType);
        CardView cv = view.findViewById(R.id.cvAddType);
        TextView tv = view.findViewById(R.id.tvCardAddType);
        TextInputEditText edtName = view.findViewById(R.id.tietNameAddType);
        // gán tag ban đầu cho circle imageview
        Object oldImage = civ.getTag();
        // nhấn vào circle imageview để chọn ảnh
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        // nhấn vào nút thêm
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // báo lỗi chưa nhập tên
                if(edtName.getText().toString().trim().equals("")){
                    Toast.makeText(getContext(), "Bạn chưa nhập tên loại", Toast.LENGTH_SHORT).show();
                } else {
                    // khởi tạo tên loại món ban đầu là chưa tồn tại
                    boolean non_existed = true;
                    // xét list loại món, nếu tên loại món đã tồn tại thì báo lỗi tên loại đã tồn tại
                    if(list.size()>0){
                        for(int i=0;i<list.size();i++){
                            if(edtName.getText().toString().equals(list.get(i).getTypeName())){
                                non_existed = false;
                                Toast.makeText(getContext(), "Tên loại đã tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    // xét trường hợp loại món chưa tồn tại.
                    if(non_existed==true){
                        // xét trường hợp không thay đổi ảnh loại món.
                        if(civ.getTag()==oldImage){
                            // thì lấy ảnh mặc định làm uri ảnh.
                            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.logoicon);
                            mImageUri = uri;
                        } else {
                            // ngược lại thì không lấy gì.
                        }
                        // tạo progress dialog.
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        progressDialog.setMessage("Đang xử lý...");
                        progressDialog.setProgressStyle(R.style.ProgressDialog);
                        progressDialog.show();
                        // tạo key ngẫu nhiên.
                        String key = UUID.randomUUID().toString();
                        // địa chỉ lưu ảnh loại món.
                        StorageReference fileReference = storageRef.child("imageType/" + key);
                        // xử lý thêm ảnh loại món.
                         storageTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uri.isComplete());
                                Uri url = uri.getResult();
                                String imageUrl = url.toString();
                                // tạo ra loại món từ tên món và ảnh món.
                                final ProductType type = new ProductType(
                                        edtName.getText().toString().trim(),
                                        imageUrl);
                                // lưu loại món mới lên firebase
                                databaseRef.child(key).setValue(type).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialog.cancel();
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Đã thêm loại món", Toast.LENGTH_SHORT).show();
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
    // mở dialog xoá loại món
    private void openDialogDelete(int position){
        // tạo alert dialog
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
                dialog.dismiss();
                typeDelete(position);
            }
        }).show();
    }

    private void typeDelete(int position){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang xoá...");
        progressDialog.show();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        String key = list.get(position).getTypeId();
        String name_del = list.get(position).getTypeName();
        DatabaseReference pdb = FirebaseDatabase.getInstance().getReference("list_product");
        for (int i = 0; i < plist.size(); i++) {
            if(plist.get(i).getProductType().equals(key)){
                StorageReference storageRef = firebaseStorage.getReferenceFromUrl(plist.get(i).getProductImage());
                storageRef.delete();
                pdb.child(plist.get(i).getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Đã xoá món trong loại " + list.get(position).getTypeName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(list.get(position).getTypeImage());
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseRef.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.cancel();
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), "Đã xoá loại món " + name_del, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Xoá món thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showEditTypeDialog(int pos){
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product_type, null);
        dialog.setContentView(view);
        civ = view.findViewById(R.id.ivAddType);
        TextInputLayout name = view.findViewById(R.id.tilNameAddType);
        CardView cv = view.findViewById(R.id.cvAddType);
        TextView tv = view.findViewById(R.id.tvCardAddType);
        TextInputEditText edtName = view.findViewById(R.id.tietNameAddType);
        tv.setText("Cập nhật");
        edtName.setText(list.get(pos).getTypeName());
        Glide
                .with(getContext())
                .load(list.get(pos).getTypeImage())
                .centerCrop()
                .into(civ);
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
                    Toast.makeText(getContext(), "Bạn chưa nhập tên loại món", Toast.LENGTH_SHORT).show();
                } else {
                    boolean non_existed = true;
                    for(int i=0;i<list.size();i++){
                        if(edtName.getText().toString().equals(list.get(i).getTypeName()) && !edtName.getText().toString().equals(list.get(pos).getTypeName())){
                            non_existed=false;
                            Toast.makeText(getContext(), "Tên loại đã tồn tại", Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                    if(non_existed==true){
                        if(civ.getTag()==oldImage){
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            progressDialog.setMessage("Đang xử lý...");
                            progressDialog.show();
                            Map<String, Object> newType = new HashMap<>();
                            newType.put("typeName", edtName.getText().toString());
                            databaseRef.child(list.get(pos).getTypeId()).updateChildren(newType).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "Đã cập nhật loại món", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Cập nhật loại món lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setCancelable(false);
                            progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            progressDialog.setMessage("Đang xử lý...");
                            progressDialog.show();
                            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                            StorageReference imageRef = firebaseStorage.getReferenceFromUrl(list.get(pos).getTypeImage());
                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    StorageReference fileReference = storageRef.child("imageType/"+list.get(pos).getTypeId());
                                    storageTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                            while(!uri.isComplete());
                                            Uri url = uri.getResult();
                                            String imageUrl = url.toString();
                                            Map<String, Object> newType = new HashMap<>();
                                            newType.put("typeName", edtName.getText().toString());
                                            newType.put("typeImage", imageUrl);
                                            databaseRef.child(list.get(pos).getTypeId()).updateChildren(newType).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialog.dismiss();
                                                    Toast.makeText(getContext(), "Đã cập nhật loại món", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Cập nhật loại món lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                }
            }
        });
        dialog.show();
    }
}
