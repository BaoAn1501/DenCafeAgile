//package com.antbps15545.dencafeagile.product;
//
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.ArrayAdapter;
//import android.widget.DatePicker;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AlertDialog;
//import androidx.cardview.widget.CardView;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.antbps15545.dencafeagile.R;
//import com.antbps15545.dencafeagile.model.ProductType;
//import com.bumptech.glide.Glide;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.android.material.textfield.TextInputLayout;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.StorageTask;
//import com.google.firebase.storage.UploadTask;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class ProductAdminFragment extends Fragment{
//    View view;
//    FloatingActionsMenu fam;
//    RecyclerView rcv;
//    private static final int PICK_IMAGE_REQUEST = 123;
//    private Uri mImageUri;
//    public CircleImageView civ;
//    CardView cv;
//    ProgressDialog progressDialog;
//    StorageReference storageRef;
//    private StorageTask storageTask;
//    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    List<ProductType> mTypes = new ArrayList<>();
//    List<Discount> mDiscounts = new ArrayList<>();
//    List<Dish> mDishes = new ArrayList<>();
//    DishListAdapter adapter;
//    SimpleDateFormat sdf;
//    int posType, posDisc;
//    SwipeRefreshLayout srl;
//    int receiver;
//    @Nullable
//    @org.jetbrains.annotations.Nullable
//    @Override
//    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.fragment_admin_product, container, false);
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        init();
////        mTypes = new ArrayList<ProductType>();
////        mDiscounts = new ArrayList<Discount>();
////        mDishes = new ArrayList<Dish>();
//        getListTypeFromCloud();
//        getListDishesFromCloud();
//        getListDiscountFromCloud();
//        fap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mTypes.size()>0){
//                    showAddProductDialog();
//                    fam.collapse();
//                } else {
//                    Toast.makeText(getContext(), "Chưa có loại món, hãy thêm loại món trước", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        fat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddTypeDialog();
//                fam.collapse();
//            }
//        });
//        fst.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replace(new TypeAdminFragment());
//            }
//        });
//        fad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showAddDiscountDialog();
//                fam.collapse();
//            }
//        });
//        fsd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                replace(new DiscountAdminFragment());
//            }
//        });
//        DishListAdapter.OnItemTouchListener itemTouchListener = new DishListAdapter.OnItemTouchListener() {
//            @Override
//            public void onButtonEditClick(View view, int position) {
//                showEditDishDialog(position);
//            }
//
//            @Override
//            public void onButtonDelClick(View view, int position) {
//                openDialogDelete(position);
//            }
//        };
//        adapter = new DishListAdapter(getContext(), mDishes, itemTouchListener);
//        LinearLayoutManager manager = new LinearLayoutManager(getContext());
//        rcv.setLayoutManager(manager);
//        rcv.setAdapter(adapter);
//        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                getListDishesFromCloud();
//                getListDiscountFromCloud();
//                getListTypeFromCloud();
//                adapter.notifyDataSetChanged();
//                srl.setRefreshing(false);
//            }
//        });
//
//        Bundle bundle = getArguments();
//        if(bundle!=null){
//            receiver = bundle.getInt("sent");
//            if(receiver==1){
//                int pos = bundle.getInt("dishPos");
//                Toast.makeText(getContext(), "dish pos: "+pos, Toast.LENGTH_SHORT).show();
//            }
//        }
//
////        touchRcvItem();
//    }
//
//    private void init(){
//        fam = view.findViewById(R.id.fabProduct);
//        fap = view.findViewById(R.id.fabAddProduct);
//        fat = view.findViewById(R.id.fabAddType);
//        fst = view.findViewById(R.id.fabShowType);
//        fad = view.findViewById(R.id.fabAddDiscount);
//        fsd = view.findViewById(R.id.fabShowDiscount);
//        rcv = view.findViewById(R.id.rcvManageProduct);
//        storageRef = FirebaseStorage.getInstance().getReference("image");
//        srl = view.findViewById(R.id.srlProduct);
//        sdf = new SimpleDateFormat("dd-MM-yyyy");
//    }
//
//    private void showPickDateDialog(Context context, TextInputEditText edt){
//        Calendar c = Calendar.getInstance();
//        DatePickerDialog dpd = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                c.set(year, month, dayOfMonth);
//                String dateString = sdf.format(c.getTime());
//                edt.setText(dateString);
//            }
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        dpd.show();
//    }
//
//    private void replace(Fragment fragment){
//        FragmentTransaction transacion = getFragmentManager().beginTransaction();
//        transacion.replace(R.id.frameProductAdmin, fragment);
//        transacion.addToBackStack(null);
//        transacion.commit();
//    }
//
//    private void showAddDiscountDialog(){
//        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_discount, null);
//        dialog.setContentView(view);
//        TextInputLayout id = view.findViewById(R.id.edtIdDiscountDialog);
//        TextInputLayout tilPercent = view.findViewById(R.id.tilPercentDiscountDialog);
//        TextInputLayout tilBegin = view.findViewById(R.id.tilBeginDateDiscountDialog);
//        TextInputLayout tilEnd = view.findViewById(R.id.tilEndDateDiscountDialog);
//        TextInputEditText edtPercent = view.findViewById(R.id.edtPercentDiscountDialog);
//        TextInputEditText edtBegin = view.findViewById(R.id.edtBeginDateDiscountDialog);
//        TextInputEditText edtEnd = view.findViewById(R.id.edtEndDateDiscountDialog);
//        CardView cvAdd = view.findViewById(R.id.cvAddDiscountDialog);
//        String key = UUID.randomUUID().toString();
//        id.getEditText().setText(key.substring(key.length()-12));
//        dialog.show();
//        edtBegin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPickDateDialog(getContext(), edtBegin);
//            }
//        });
//        edtEnd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPickDateDialog(getContext(), edtEnd);
//            }
//        });
//        cvAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(invalidDisCount(id, tilPercent, tilBegin, tilEnd)>0){
//                    Map<String, Object> newDiscount = new HashMap<>();
//                    newDiscount.put("id", id.getEditText().getText().toString());
//                    newDiscount.put("percentage", Integer.valueOf(edtPercent.getText().toString()));
//                    newDiscount.put("beginDate", edtBegin.getText().toString());
//                    newDiscount.put("endDate", edtEnd.getText().toString());
//                    db.collection("users").document(mAuth.getCurrentUser().getUid())
//                            .collection("discount").document(id.getEditText().getText().toString()).set(newDiscount).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(getContext(),"Thêm mã giảm giá thành công", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull @NotNull Exception e) {
//                            Toast.makeText(getContext(), "Thêm mã giảm giá thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                        }
//                    });
//                }
//            }
//        });
//
//    }
//
//    private void showAddProductDialog() {
//        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product, null);
//        dialog.setContentView(view);
//        civ = view.findViewById(R.id.civAddProduct);
//        TextInputLayout name = view.findViewById(R.id.tilNameAddProduct);
//        TextInputLayout price = view.findViewById(R.id.tilPriceAddProduct);
//        TextInputEditText edtName = view.findViewById(R.id.edtNameAddProduct);
//        Spinner spnType = view.findViewById(R.id.spnTypeAddProduct);
//        cv = view.findViewById(R.id.cvAddProduct);
//        TextView tv = view.findViewById(R.id.tvCardAddProduct);
//        ArrayAdapter<ProductType> adapter = new ArrayAdapter<ProductType>(getActivity(), android.R.layout.simple_list_item_1, mTypes);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnType.setAdapter(adapter);
//        price.getEditText().setText("0");
//        Object oldImage = civ.getTag();
//        civ.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
//        cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(name.getEditText().getText().toString().trim().equals("")){
//                    Toast.makeText(getContext(), "Bạn chưa nhập tên món", Toast.LENGTH_SHORT).show();
//                } else if(price.getEditText().getText().toString().trim().equals("")) {
//                    Toast.makeText(getContext(), "Bạn chưa nhập giá món", Toast.LENGTH_SHORT).show();
//                } else {
//                    boolean non_existed = true;
//                    if(mDishes.size()>0){
//                        for(int i=0;i<mDishes.size();i++){
//                            if(edtName.getText().toString().equals(mDishes.get(i).getName())){
//                                non_existed = false;
//                                Toast.makeText(getContext(), "Tên món đã tồn tại", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                    if(non_existed==true){
//                        if(civ.getTag()==oldImage){
//                            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.logoicon);
//                            mImageUri = uri;
//                        } else {
//
//                        }
//                        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
//                        progressDialog.setCancelable(false);
//                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        progressDialog.setMessage("Đang xử lý...");
//                        progressDialog.show();
//                        String key = UUID.randomUUID().toString();
//                        StorageReference fileReference = storageRef.child("users").child(mAuth.getCurrentUser().getUid()).child("dish/" + key);
//                        storageTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                                while(!uri.isComplete());
//                                Uri url = uri.getResult();
//                                String imageUrl = url.toString();
//                                Map<String, Object> newDish = new HashMap<>();
//                                newDish.put("id", key);
//                                newDish.put("name", edtName.getText().toString());
//                                newDish.put("price", Integer.valueOf(price.getEditText().getText().toString()));
//                                newDish.put("image", imageUrl);
//                                for(int i=0;i<mTypes.size();i++){
//                                    if(mTypes.get(i).getName().equals(spnType.getSelectedItem().toString())){
//                                        posType = i;
//                                    }
//                                }
//                                newDish.put("type",mTypes.get(posType).getId());
//                                newDish.put("ownerid", mAuth.getCurrentUser().getUid());
//                                newDish.put("discountId", "");
//                                db.collection("users").document(mAuth.getCurrentUser().getUid())
//                                        .collection("dish").document(key).set(newDish).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        progressDialog.dismiss();
//                                        dialog.cancel();
////                                        replace(new ProductAdminFragment());
//                                        getListDishesFromCloud();
//                                        adapter.notifyDataSetChanged();
//                                        Toast.makeText(getContext(), "Đã thêm món thành công", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull @NotNull Exception e) {
//                                        Toast.makeText(getContext(), "errors: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull @NotNull Exception e) {
//                                Toast.makeText(getContext(), "errors: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//            }
//        });
//        dialog.show();
//    }
//
//    public void showEditDishDialog(int position) {
//        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product_update, null);
//        dialog.setContentView(view);
//        civ = view.findViewById(R.id.civUpdateProduct);
//        TextInputLayout name = view.findViewById(R.id.tilNameUpdateProduct);
//        TextInputLayout price = view.findViewById(R.id.tilPriceUpdateProduct);
//        TextInputEditText edtName = view.findViewById(R.id.edtNameUpdateProduct);
//        Spinner spnType = view.findViewById(R.id.spnTypeUpdateProduct);
//        Spinner spnDisc = view.findViewById(R.id.spnDiscountUpdateProduct);
//        cv = view.findViewById(R.id.cvUpdateProduct);
//        Object oldImage = civ.getTag();
//        ArrayAdapter<ProductType> adapter = new ArrayAdapter<ProductType>(getActivity(), android.R.layout.simple_list_item_1, mTypes);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnType.setAdapter(adapter);
//        for(int i=0;i<mTypes.size();i++){
//            if(mTypes.get(i).getId().equals(mDishes.get(position).getType())){
//                posType = i;
//            }
//        }
//        for(int i =0;i<spnType.getCount();i++){
//            if(spnType.getItemAtPosition(i).toString().equals(mTypes.get(posType).getName())){
//                spnType.setSelection(i);
//            }
//        }
//        price.getEditText().setText(""+mDishes.get(position).getPrice());
//        edtName.setText(mDishes.get(position).getName());
//        Glide
//                .with(getContext())
//                .load(mDishes.get(position).getImage())
//                .centerCrop()
//                .into(civ);
//                DiscountSpinnerAdapter adapter1 = new DiscountSpinnerAdapter(getContext(), R.layout.item_discount, mDiscounts);
//                spnDisc.setBackgroundColor(getResources().getColor(R.color.fui_transparent));
//                spnDisc.setAdapter(adapter1);
//                posDisc = 0;
//                for(int i=1;i<mDiscounts.size();i++){
//                    if(mDiscounts.get(i).getId().equals(mDishes.get(position).getDiscountId())){
//                        posDisc = i;
//                    }
//                }
//                spnDisc.setSelection(posDisc);
//
//        civ.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
//        cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(name.getEditText().getText().toString().trim().equals("")){
//                    Toast.makeText(getContext(), "Bạn chưa nhập tên món", Toast.LENGTH_SHORT).show();
//                } else if(price.getEditText().getText().toString().trim().equals("")) {
//                    Toast.makeText(getContext(), "Bạn chưa nhập giá món", Toast.LENGTH_SHORT).show();
//                } else {
//                    if(civ.getTag()==oldImage){
//                        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
//                        progressDialog.setCancelable(false);
//                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        progressDialog.setMessage("Đang xử lý...");
//                        progressDialog.show();
//                        Map<String, Object> newDish = new HashMap<>();
//                        newDish.put("name", edtName.getText().toString());
//                        newDish.put("price", Integer.valueOf(price.getEditText().getText().toString()));
//                        for(int i=0;i<mTypes.size();i++){
//                            if(mTypes.get(i).getName().equals(spnType.getSelectedItem().toString())){
//                                posType = i;
//                            }
//                        }
//                        newDish.put("type",mTypes.get(posType).getId());
//                        if(spnDisc.getSelectedItemPosition()==0){
//                            newDish.put("discountId", "");
//                        } else {
//                            Discount discount = (Discount) spnDisc.getSelectedItem();
//                            newDish.put("discountId", discount.getId());
//                        }
//                        db.collection("users").document(mAuth.getCurrentUser().getUid())
//                                .collection("dish").document(mDishes.get(position).getId())
//                                .update(newDish)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        progressDialog.dismiss();
//                                        dialog.dismiss();
//                                        getListDishesFromCloud();
//                                        adapter.notifyDataSetChanged();
//                                        Toast.makeText(getContext(), "Đã cập nhật món thành công", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull @NotNull Exception e) {
//                                Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
//                        progressDialog.setCancelable(false);
//                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        progressDialog.setMessage("Đang xử lý...");
//                        progressDialog.show();
//                        String key = UUID.randomUUID().toString();
//                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//                        StorageReference imageRef = firebaseStorage.getReferenceFromUrl(mDishes.get(position).getImage());
//                        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                StorageReference fileReference = storageRef.child("users").child(mAuth.getCurrentUser().getUid()).child("dish/" + key);
//                                storageTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                    @Override
//                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                                        while(!uri.isComplete());
//                                        Uri url = uri.getResult();
//                                        String imageUrl = url.toString();
//                                        Map<String, Object> newDish = new HashMap<>();
//                                        newDish.put("id", key);
//                                        newDish.put("name", edtName.getText().toString());
//                                        newDish.put("price", Integer.valueOf(price.getEditText().getText().toString()));
//                                        newDish.put("image", imageUrl);
//                                        for(int i=0;i<mTypes.size();i++){
//                                            if(mTypes.get(i).getName().equals(spnType.getSelectedItem().toString())){
//                                                posType = i;
//                                            }
//                                        }
//                                        newDish.put("type",mTypes.get(posType).getId());
//                                        db.collection("users").document(mAuth.getCurrentUser().getUid())
//                                                .collection("dish").document(mDishes.get(position).getId()).update(newDish)
//                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void unused) {
//                                                        progressDialog.dismiss();
//                                                        dialog.dismiss();
//                                                        Toast.makeText(getContext(), "Đã cập nhật món thành công", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull @NotNull Exception e) {
//                                                Toast.makeText(getContext(), "Cập nhật món thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull @NotNull Exception e) {
//                                        Toast.makeText(getContext(), "Thêm ảnh mới của món thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull @NotNull Exception e) {
//                                Toast.makeText(getContext(), "Xoá ảnh cũ của món thất bại: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//
//                }
//            }
//        });
//        dialog.show();
//    }
//
//
//
//    private void showAddTypeDialog(){
//        mImageUri = null;
//        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product_type, null);
//        dialog.setContentView(view);
//        civ = view.findViewById(R.id.ivAddType);
//        TextInputLayout name = view.findViewById(R.id.tilNameAddType);
//        CardView cv = view.findViewById(R.id.cvAddType);
//        TextView tv = view.findViewById(R.id.tvCardAddType);
//        TextInputEditText edtName = view.findViewById(R.id.tietNameAddType);
//        Object oldImage = civ.getTag();
//        civ.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseImage();
//            }
//        });
//
//        cv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(edtName.getText().toString().trim().equals("")){
//                    Toast.makeText(getContext(), "Bạn chưa nhập tên loại", Toast.LENGTH_SHORT).show();
//                } else {
//                    boolean non_existed = true;
//                    if(mTypes.size()>0){
//                        for(int i=0;i<mTypes.size();i++){
//                            if(edtName.getText().toString().equals(mTypes.get(i).getName())){
//                                non_existed = false;
//                                Toast.makeText(getContext(), "Tên loại đã tồn tại", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                    if(non_existed==true){
//                        if(civ.getTag()==oldImage){
//                            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.drawable.logoicon);
//                            mImageUri = uri;
//                        } else {
//
//                        }
//                        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
//                        progressDialog.setCancelable(false);
//                        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        progressDialog.setMessage("Đang xử lý...");
//                        progressDialog.show();
//                        String key = UUID.randomUUID().toString();
//                        StorageReference fileReference = storageRef.child("users").child(mAuth.getCurrentUser().getUid()).child("type/" + key);
//                        storageTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
//                                while(!uri.isComplete());
//                                Uri url = uri.getResult();
//                                String imageUrl = url.toString();
//                                Map<String, Object> newType = new HashMap<>();
//                                newType.put("id", key);
//                                newType.put("name", edtName.getText().toString());
//                                newType.put("image", imageUrl);
//                                newType.put("ownerid", mAuth.getCurrentUser().getUid());
//
//                                db.collection("users").document(mAuth.getCurrentUser().getUid())
//                                        .collection("types").document(key).set(newType).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        progressDialog.dismiss();
//                                        dialog.cancel();
//                                        Toast.makeText(getContext(), "Đã thêm loại món thành công", Toast.LENGTH_SHORT).show();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull @NotNull Exception e) {
//
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull @NotNull Exception e) {
//
//                            }
//                        });
//                    }
//                }
//            }
//        });
//
//        dialog.show();
//    }
//
//    private void chooseImage() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_IMAGE_REQUEST
//                && data != null && data.getData() != null) {
//            mImageUri = data.getData();
//            civ.setImageURI(mImageUri);
//            civ.setTag("changed");
//            Glide.with(this).load(mImageUri).into(civ);
//        }
//    }
//
//    private void getListTypeFromCloud(){
//        db.collection("users").document(mAuth.getCurrentUser().getUid())
//                .collection("types").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                mTypes.clear();
//                for(QueryDocumentSnapshot documents : queryDocumentSnapshots){
//                    ProductType type = documents.toObject(ProductType.class);
//                    mTypes.add(type);
//                }
//            }
//        });
//    }
//
//    private void getListDishesFromCloud(){
//        db.collection("users").document(mAuth.getCurrentUser().getUid())
//                .collection("dish")
//                .orderBy("type").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                mDishes.clear();
//                for(QueryDocumentSnapshot documents : queryDocumentSnapshots){
//                    Dish dish = documents.toObject(Dish.class);
//                    mDishes.add(dish);
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private void getListDiscountFromCloud(){
//        db.collection("users").document(mAuth.getCurrentUser().getUid())
//                .collection("discount")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        mDiscounts.clear();
//                        for(QueryDocumentSnapshot documents : queryDocumentSnapshots){
//                            Discount discount = documents.toObject(Discount.class);
//                            mDiscounts.add(discount);
//                        }
//                        Collections.sort(mDiscounts, new Comparator<Discount>(){
//                            @Override
//                            public int compare(Discount o1, Discount o2) {
//                                try {
//                                    if(sdf.parse(o1.getBeginDate()).after(sdf.parse(o2.getBeginDate()))){
//                                        return -1;
//                                    }
//                                } catch (Exception e){
//                                    Toast.makeText(getContext(), "sort failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                                return 1;
//                            }
//                        });
//
//
//                    }
//                });
//    }
//
//    private int invalidDisCount(TextInputLayout tilId, TextInputLayout tilPercent, TextInputLayout tilBegin, TextInputLayout tilEnd){
//        int invalid = 1;
//        if(tilPercent.getEditText().getText().toString().trim().equals("")){
//            tilPercent.setError("* Giảm giá không được rỗng");
//            invalid = 0;
//        } else {
//            tilPercent.setError("");
//        }
//        if(!tilPercent.getEditText().getText().toString().trim().equals("")){
//            if(Integer.valueOf(tilPercent.getEditText().getText().toString().trim())<1 || Integer.valueOf(tilPercent.getEditText().getText().toString().trim())>100) {
//                tilPercent.setError("* Giảm giá từ 1 đến 100");
//                invalid = 0;
//            } else {
//                tilPercent.setError("");
//            }
//            int o=0;
//            for (Discount dis:mDiscounts) {
//                if(dis.getPercentage()==Integer.valueOf(tilPercent.getEditText().getText().toString().trim())
//                        && dis.getBeginDate().equals(tilBegin.getEditText().getText().toString().trim())
//                        && dis.getEndDate().equals(tilEnd.getEditText().getText().toString().trim())){
//                    o++;
//                }
//            }
//            if(o>0){
//                Toast.makeText(getContext(), "Mã giảm giá đã tồn tại", Toast.LENGTH_SHORT).show();
//                invalid = 0;
//            }
//        }
//        int i=0;
//        for (Discount dis:mDiscounts) {
//            if(dis.getId().equals(tilId.getEditText().getText().toString().trim())){
//                i++;
//            }
//        }
//        if(i!=0){
//            tilId.setError("* Mã giảm giá đã tổn tại");
//            invalid = 0;
//        } else {
//            tilId.setError("");
//        }
//        if(tilBegin.getEditText().getText().toString().trim().equals("")){
//            tilBegin.setError("* Ngày bắt đầu giảm giá không được rỗng");
//            invalid = 0;
//        } else {
//            tilBegin.setError("");
//        }
//        if(tilEnd.getEditText().getText().toString().trim().equals("")){
//            tilEnd.setError("* Ngày kết thúc giảm giá không được rỗng");
//            invalid = 0;
//        } else {
//            tilEnd.setError("");
//        }
//        try {
//            Date beginDate = sdf.parse(tilBegin.getEditText().getText().toString().trim());
//            Date endDate = sdf.parse(tilEnd.getEditText().getText().toString().trim());
//            if(beginDate.after(endDate)){
//                tilEnd.setError("* Ngày kết thúc phải sau ngày bắt đầu");
//                invalid = 0;
//            } else {
//                tilEnd.setError("");
//            }
//        } catch (Exception e){
//            Toast.makeText(getContext(), "error parse date: "+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        return invalid;
//    }
//
////    private void touchRcvItem() {
////        rcv.addOnItemTouchListener(new DishListAdapter(getContext(), rcv, new DishListAdapter.ClickListener() {
////            @Override
////            public void onClick(View view, int position) {
////                // Write your code here
////                openDialogChoose(position);
////            }
////
////            @Override
////            public void onLongClick(View view, int position) {
////
////            }
////        }));
////    }
//
////    private void openDialogChoose(int position){
////        BottomSheetDialog dialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
////        LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
////        View view = inflater.inflate(R.layout.dialog_bottom_choose, null);
////        dialog.setContentView(view);
////        CardView cvEdit = (CardView) view.findViewById(R.id.cvEditChoose);
////        CardView cvRemove = (CardView) view.findViewById(R.id.cvDelChoose);
////        ImageView ivEdit = (ImageView) view.findViewById(R.id.ivEditChoose);
////        ImageView ivDel = (ImageView) view.findViewById(R.id.ivDelChoose);
////        TextView tvEdit = (TextView) view.findViewById(R.id.tvEditChoose);
////        TextView tvDel = (TextView) view.findViewById(R.id.tvDelChoose);
////        ivEdit.setColorFilter(getContext().getResources().getColor(R.color.DefaultGrey));
////        ivDel.setColorFilter(getContext().getResources().getColor(R.color.DefaultGrey));
////        tvEdit.setTextColor(getContext().getResources().getColor(R.color.DefaultGrey));
////        tvDel.setTextColor(getContext().getResources().getColor(R.color.DefaultGrey));
////        cvEdit.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ivEdit.setColorFilter(getContext().getResources().getColor(R.color.Green));
////                tvEdit.setTextColor(getContext().getResources().getColor(R.color.Green));
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        showEditDishDialog(position);
////                        dialog.dismiss();
////                    }
////                }, 100);
////
////            }
////        });
////        cvRemove.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                ivDel.setColorFilter(getContext().getResources().getColor(R.color.Green));
////                tvDel.setTextColor(getContext().getResources().getColor(R.color.Green));
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        openDialogDelete(position);
////                        dialog.cancel();
////                    }
////                }, 100);
////            }
////        });
////        dialog.show();
////    }
////
//    private void openDialogDelete(int position){
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
//        final AlertDialog dialog = builder.create();
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        builder.setMessage("Bạn có muốn xoá loại sản phẩm này không?");
//        builder.setCancelable(false);
//        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).setPositiveButton("Có", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dishDelete(position);
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private void dishDelete(int position){
//        progressDialog = new ProgressDialog(getContext(), R.style.ProgressDialog);
//        progressDialog.setCancelable(false);
//        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        progressDialog.setMessage("Đang xoá...");
//        progressDialog.show();
//        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
//        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(mDishes.get(position).getImage());
//        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                db.collection("users").document(mAuth.getCurrentUser().getUid())
//                        .collection("dish").document(mDishes.get(position).getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        mDishes.remove(position);
//                        progressDialog.dismiss();
//                        adapter.notifyItemRemoved(position);
//                        Toast.makeText(getContext(), "Đã xoá món thành công", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull @NotNull Exception e) {
//                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull @NotNull Exception e) {
//                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
//
//
//
