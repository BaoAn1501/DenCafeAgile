package com.antbps15545.dencafeagile.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.adapter.LoginPagerAdapter;

public class LoginActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    LoginPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        adapter = new LoginPagerAdapter(this, getSupportFragmentManager(), 2);
        viewPager.setAdapter(adapter);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

    }
}