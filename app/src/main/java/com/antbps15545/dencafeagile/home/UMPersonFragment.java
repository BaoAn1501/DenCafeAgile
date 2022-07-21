package com.antbps15545.dencafeagile.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.antbps15545.dencafeagile.SplashActivity;
import com.bumptech.glide.Glide;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.login.LoginActivity;
import com.antbps15545.dencafeagile.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UMPersonFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 111;
    View view;
    TextView tvName, tvPhone, tvEmail;
    ImageView ivProfile;
    CardView cvLogout, cvSetAvatar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    private StorageTask storageTask;
    ProgressDialog progressDialog;
    private List<User> list = new ArrayList<>();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    StorageReference storageRef = FirebaseStorage.getInstance().getReference("imageFolder");
    private Uri mImageUri;
    User mUser;
    FloatingActionButton fabInfo, fabPass;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_um_person, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
//        showUserInfo();
        if(currentUser==null){
            mAuth.signOut();
            startActivity(new Intent(getContext(), SplashActivity.class));
        } else {
            getUserInfo();
        }
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser!=null){
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        fabInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogChangeInfo();
            }
        });
        fabPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogChangePass();
            }
        });
    }

    private void init(){
        tvName = view.findViewById(R.id.tvName_UserInfo);
        tvEmail = view.findViewById(R.id.tvEmail_UserInfo);
        tvPhone = view.findViewById(R.id.tvPhone_UserInfo);
        ivProfile = view.findViewById(R.id.ivProfile_UserInfo);
        cvLogout = view.findViewById(R.id.logout_UserInfo);
        fabInfo = view.findViewById(R.id.fabChangeInfoUser);
        fabPass = view.findViewById(R.id.fabChangePassUser);
    }

    private void getUserInfo(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                int pos = 0;
                for(int i=0;i<list.size();i++){
                    if(uid.equals(list.get(i).getUserId())){
                        pos = i;
                    }
                }
                mUser = list.get(pos);
                tvName.setText(mUser.getUserName());
                tvPhone.setText(mUser.getUserPhone());
                tvEmail.setText(mUser.getUserEmail());
                if(mUser.getUserImage().equals("default")){
                    ivProfile.setImageResource(R.drawable.logoicon);
                } else {
                    Glide
                            .with(getContext())
                            .load(mUser.getUserImage())
                            .centerCrop()
                            .placeholder(R.drawable.logoicon)
                            .into(ivProfile);
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            ivProfile.setImageURI(mImageUri);
            Glide.with(this).load(mImageUri).into(ivProfile);
            if(mImageUri!=null){
                changeImageProfile();
            }
        }
    }

    private void changeImageProfile() {
        if (mImageUri != null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Đang xử lý...");
            progressDialog.show();
            if(mUser.getUserImage().equals("default")){
                StorageReference fileReference = storageRef.child("imageProfile/" + uid);
                fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uri.isComplete());
                                Uri url = uri.getResult();
                                String imageUrl = url.toString();
                                userRef.child(mUser.getUserId()).child("userImage").setValue(imageUrl);
                                progressDialog.dismiss();
                            }
                        });
            } else {
                StorageReference fileReference = storageRef.child("imageProfile/" + uid);
                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uri.isComplete()) ;
                                Uri url = uri.getResult();
                                String imageUrl = url.toString();
                                userRef.child(mUser.getUserId()).child("userImage").setValue(imageUrl);
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Lỗi up ảnh: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        } else {
            Toast.makeText(getContext(), "Bạn chưa chọn ảnh cho loại món", Toast.LENGTH_SHORT).show();
        }
    }
    private void showDialogChangePass(){
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        TextInputEditText curEmail = view.findViewById(R.id.edtCurrentEmail);
        TextInputEditText curPass = view.findViewById(R.id.edtCurrentPass);
        TextInputEditText newPass = view.findViewById(R.id.edtNewPass);
        CardView change = view.findViewById(R.id.cvChangePass);
        dialog.setContentView(view);
        dialog.show();
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(curEmail.getText().toString().trim().equals("") || curPass.getText().toString().trim().equals("") || newPass.getText().toString().trim().equals("")){
                    String message = "Vui lòng nhập đủ thông tin";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    Log.e("errorinputchangepass",message);
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(curEmail.getText().toString().trim(), curPass.getText().toString().trim());
                    if(curEmail.getText().toString().trim().equals(currentUser.getEmail())){
                        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    currentUser.updatePassword(newPass.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            String message = "Đổi mật khẩu thành công";
                                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            startActivity(new Intent(getContext(), SplashActivity.class));
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String message = "Tiến trình thất bại";
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        String message = "Đổi mật khẩu thành công";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    private void showDialogChangeInfo(){
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_info, null);
        dialog.setContentView(view);
        dialog.show();

    }
}