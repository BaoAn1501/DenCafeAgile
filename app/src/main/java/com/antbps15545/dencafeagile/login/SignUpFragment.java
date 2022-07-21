package com.antbps15545.dencafeagile.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.antbps15545.dencafeagile.UserMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignUpFragment extends Fragment {
    View view;
    TextInputLayout edPhone, edEmail, edName, edPass;
    CardView cvSignUp;
    public static final String TAG = SignUpFragment.class.getName();
    private ProgressDialog progressDialog;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    List<User> list = new ArrayList<>();
    User mUser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        getListUser();
        validation();
        cvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateName()>0 && validateEmail()>0 && validatePhone()>0 && validatePass()>0){
//                    String id = mAuth.getCurrentUser().getUid();
                    String name = edName.getEditText().getText().toString().trim();
                    String phone = edPhone.getEditText().getText().toString().trim();
                    String email = edEmail.getEditText().getText().toString().trim();
                    String pass = edPass.getEditText().getText().toString().trim();
                    // đăng ký bằng email và password
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                userRef.child(mAuth.getCurrentUser().getUid()).setValue(new User(mAuth.getCurrentUser().getUid(), name, phone, email, "default")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Bạn đã đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                startActivity(new Intent(getActivity(), UserMainActivity.class));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Đăng ký thất bại. Lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void init(){
        edEmail = view.findViewById(R.id.edEmail_SignUp);
        edName = view.findViewById(R.id.edName_SignUp);
        edPhone = view.findViewById(R.id.edPhone_SignUp);
        cvSignUp = view.findViewById(R.id.cvSignUp);
        edPass = view.findViewById(R.id.edPass_SignUp);
    }

    private void validation(){
        // bắt lỗi trực tiếp khi nhập text
        edEmail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateName();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edPass.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePass();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private int validatePass() {
        int result = 1;
        if(edPass.getEditText().getText().toString().trim().equals("")){
            edPass.setError("Mật khẩu không được để trống");
            result = 0;
        } else if(edPass.getEditText().getText().toString().trim().length()<8){
            edPass.setError("Mật khẩu không ít hơn 8 ký tự");
            result = 0;
        } else {
            edPass.setError("");
        }
        return result;
    }

    private int validateEmail(){
        int result = 1;
        String emailReg = "[A-Za-z]*\\w+@gmail.com";
        if(edEmail.getEditText().getText().toString().trim().equals("")){
            edEmail.setError("Email không được để trống");
            result = 0;
        } else if(!edEmail.getEditText().getText().toString().trim().matches(emailReg)){
            edEmail.setError("Email không đúng định dạng");
            result = 0;
        } else if(edEmail.getEditText().getText().toString().trim().equals(getResources().getString(R.string.ADMIN_ACCOUNT))){
            edEmail.setError("Email đã được đăng ký trước đó");
            result = 0;
        } else {
            edEmail.setError("");
        }
        int accCount = 0;
        for(int i=0;i<list.size();i++){
            if(edEmail.getEditText().getText().toString().trim().equals(list.get(i).getUserEmail())){
                accCount++;
            }
        }
        if(accCount>0){
            edEmail.setError("Email đã được đăng ký trước đó");
            result = 0;
        }
        return result;
    }

    private int validatePhone(){
        int result = 1;
        String regphone = "^(0)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";
        if(edPhone.getEditText().getText().toString().trim().equals("")){
            edPhone.setError("Số điện thoại không được để trống");
            result=0;
        } else if(!edPhone.getEditText().getText().toString().trim().matches(regphone)){
            edPhone.setError("Số điện thoại không đúng định dạng");
            result=0;
        } else {
            edPhone.setError("");
        }
        int accCount = 0;
        for(int i=0;i<list.size();i++){
            if(edPhone.getEditText().getText().toString().trim().equals(list.get(i).getUserPhone())){
                accCount++;
            }
        }
        if(accCount>0){
            edPhone.setError("Số điện thoại đã được đăng ký trước đó");
            result = 0;
        }
        return result;
    }
    private int validateName(){
        int result = 1;
        if(edName.getEditText().getText().toString().trim().equals("")){
            edName.setError("Tên không được để trống");
            result = 0;
        } else {
            edName.setError("");
        }
        int accCount = 0;
        for(int i=0;i<list.size();i++){
            if(edName.getEditText().getText().toString().trim().equals(list.get(i).getUserName())){
                accCount++;
            }
        }
        if(accCount>0){
            edName.setError("Tên này đã được đăng ký trước đó");
            result = 0;
        }
        return result;
    }
    // lấy danh sách người dùng từ firebase
    private void getListUser(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
