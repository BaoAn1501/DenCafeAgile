package com.antbps15545.dencafeagile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.antbps15545.dencafeagile.model.User;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.antbps15545.dencafeagile.home.UMChatFragment;
import com.antbps15545.dencafeagile.home.UMOrderFragment;
import com.antbps15545.dencafeagile.home.UMPersonFragment;
import com.antbps15545.dencafeagile.home.UMProductFragment;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class UserMainActivity extends AppCompatActivity {
    private MeowBottomNavigation bnv_Main;
    String uid = FirebaseAuth.getInstance().getUid();
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
    List<User> list = new ArrayList<>();
    int fromac;
    int accCount=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        getListUser();
        Log.e("uid",uid);

//        if(checkUserExist()==0){
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(UserMainActivity.this, SplashActivity.class));
//        }
        bnv_Main = findViewById(R.id.bnv_UserMain);
        bnv_Main.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_receipt_long_24));
        bnv_Main.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_local_library_24));
        bnv_Main.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_chat_24));
        bnv_Main.add(new MeowBottomNavigation.Model(4,R.drawable.ic_baseline_person_24_red));
        fromac = getIntent().getIntExtra("fromac",0);
        if(fromac==0){
            bnv_Main.show(2,true);
            replace(new UMProductFragment());
        } else {
            bnv_Main.show(3, true);
            replace(new UMChatFragment());
        }
        bnv_Main.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()){
                    case 1:
                        replace(new UMOrderFragment());
                        break;
                    case 2:
                        replace(new UMProductFragment());
                        break;
                    case 3:
                        replace(new UMChatFragment());
                        break;
                    case 4:
                        replace(new UMPersonFragment());
                        break;
                }
                return null;
            }
        });
    }

    private void replace(Fragment fragment) {
        FragmentTransaction transacion = getSupportFragmentManager().beginTransaction();
        transacion.replace(R.id.frame_UserMain,fragment);
        transacion.commit();
    }

    private void status(boolean status){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("list_user");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        userRef.child(uid).updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        status(false);
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
                for(int i=0;i<list.size();i++){
                    if(uid.equals(list.get(i).getUserId())){
                        accCount++;
                    }
                }
                if(accCount<1){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UserMainActivity.this, SplashActivity.class));
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private int checkUserExist(){
        int uCount = 0;
        if(list.size()>0){
            for(int i=0;i<list.size();i++){
                if(uid.equals(list.get(i).getUserId())){
                    uCount++;
                }
            }
        }
        return uCount;
    }

}