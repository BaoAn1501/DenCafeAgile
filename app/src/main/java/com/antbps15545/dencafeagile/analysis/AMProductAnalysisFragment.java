package com.antbps15545.dencafeagile.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.antbps15545.dencafeagile.R;
import com.antbps15545.dencafeagile.adapter.AMProductAnalysisPagerAdapter;

import org.jetbrains.annotations.NotNull;

public class AMProductAnalysisFragment extends Fragment {
    View view;
    TabLayout tabLayout;
    ViewPager viewPager;
    AMProductAnalysisPagerAdapter adapter;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_analysis_admin, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tabLayoutAMPAF);
        viewPager = view.findViewById(R.id.viewPagerAMPAF);
        adapter = new AMProductAnalysisPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
