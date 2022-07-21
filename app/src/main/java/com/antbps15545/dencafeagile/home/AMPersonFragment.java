package com.antbps15545.dencafeagile.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.antbps15545.dencafeagile.SplashActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.login.LoginActivity;
import com.antbps15545.dencafeagile.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AMPersonFragment extends Fragment {
    View view;
    TextView tvName, tvPhone, tvEmail, tvAddress;
    CardView cvLogout;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();
    private List<User> list = new ArrayList<>();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    User mUser;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_am_person, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        if(currentUser==null){
            mAuth.signOut();
            startActivity(new Intent(getContext(), SplashActivity.class));
        } else {
            getUserInfo();
        }
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser!=null){
                    mAuth.signOut();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
    }

    private void init(){
        tvName = view.findViewById(R.id.tvName_AdminInfo);
        tvEmail = view.findViewById(R.id.tvEmail_AdminInfo);
        tvPhone = view.findViewById(R.id.tvPhone_AdminInfo);
        tvAddress = view.findViewById(R.id.tvAddress_AdminInfo);
        cvLogout = view.findViewById(R.id.logout_AdminInfo);
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
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


}