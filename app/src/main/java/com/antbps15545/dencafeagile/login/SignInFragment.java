package com.antbps15545.dencafeagile.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.antbps15545.dencafeagile.AdminMainActivity;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.UserMainActivity;
import com.antbps15545.dencafeagile.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignInFragment extends Fragment {
    View view;
    public static final int RESET_PASSWORD_CODE = 111;
    TextInputLayout edEmail, edPass;
    CardView cvSignIn;
    List<User> list = new ArrayList<>();
    ProgressDialog progressDialog;
    TextView tvForgot;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        animated();
        getListUser();
        cvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getEditText().getText().toString().trim();
                String pass = edPass.getEditText().getText().toString().trim();
                if(email.equals("") || pass.equals("")){
                    String message = "Bạn chưa nhập email hoặc password";
                    Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                } else {
                    if(email.equals(getResources().getString(R.string.ADMIN_ACCOUNT))){
                        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                            @Override
                            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if(isNewUser){
                                    mAuth.createUserWithEmailAndPassword(email, getResources().getString(R.string.ADMIN_PASSWORD)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("list_user");
                                                String uid = task.getResult().getUser().getUid();
                                                databaseRef.child(uid).setValue(new User(uid,"Default","Default","admin@gmail.com","Default")).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if(task.isSuccessful()){
                                                                    startActivity(new Intent(getActivity(), AdminMainActivity.class));
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if(task.isSuccessful()){
                                                String message = "Đăng nhập thành công vào cửa hàng";
                                                Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                                                startActivity(new Intent(getActivity(), AdminMainActivity.class));
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("error login: ",e.getMessage());
                                            String message = "Đăng nhập thất bại";
                                            Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(), "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                    if(!email.equals(getResources().getString(R.string.ADMIN_ACCOUNT))){
                                        startActivity(new Intent(getActivity(), UserMainActivity.class));
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                String message = "Đăng nhập thất bại";
                                Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                                Log.e("error login: ",e.getMessage());
                            }
                        });
                    }
                }

            }
        });
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edEmail.getEditText().getText().toString().trim();
                if(!email.equals("")){
                    if(email.equals(getResources().getString(R.string.ADMIN_ACCOUNT))){
                        String message = "Chức năng này không hỗ trợ cho email mặc định.";
                        Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                    } else {
                        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    String message = "Kiểm tra email để đổi mật khẩu";
                                    Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    String message = "Bạn chưa nhập email";
                    Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void init(){
        edEmail = view.findViewById(R.id.edEmail_SignIn);
        edPass = view.findViewById(R.id.edPass_SignIn);
        cvSignIn = view.findViewById(R.id.cvSignIn);
        tvForgot = view.findViewById(R.id.tvForgotPass);
        
    }
    public void animated(){
        edEmail.setAlpha(0);
        edPass.setAlpha(0);
        tvForgot.setAlpha(0);
        cvSignIn.setAlpha(0);
        edEmail.setTranslationX(700);
        edPass.setTranslationX(700);
        tvForgot.setTranslationX(700);
        cvSignIn.setTranslationX(700);
        edEmail.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(350).start();
        edPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(400).start();
        tvForgot.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(450).start();
        cvSignIn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
    }

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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    public void updateUI(FirebaseUser currentUser) {
        if(currentUser != null && !currentUser.isAnonymous()){
            try {
                if(currentUser.getEmail().equals(getResources().getString(R.string.ADMIN_ACCOUNT))){
                    startActivity(new Intent(getActivity(), AdminMainActivity.class));
                } else {
                    startActivity(new Intent(getActivity(), UserMainActivity.class));
                }
            } catch (Exception ex){

            }
        }else {
            Toast.makeText(getContext(), "Mời đăng nhập để tiếp tục.", Toast.LENGTH_SHORT).show();
        }
    }
}
