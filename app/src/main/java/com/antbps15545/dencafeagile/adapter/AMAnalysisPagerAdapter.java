package com.antbps15545.dencafeagile.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.antbps15545.dencafeagile.analysis.AMProductAnalysisFragment;
import com.antbps15545.dencafeagile.analysis.AMRevenueFragment;


public class AMAnalysisPagerAdapter extends FragmentStatePagerAdapter {
    public AMAnalysisPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new AMRevenueFragment();
        }
        else if(i == 1){
            return new AMProductAnalysisFragment();
        }
        else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:
                return "Doanh thu";
            case 1:
                return "Sản phẩm";
        }
        return null;
    }
}
