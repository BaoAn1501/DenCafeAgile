package com.antbps15545.dencafeagile.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.antbps15545.dencafeagile.analysis.AMProductAmountFragment;
import com.antbps15545.dencafeagile.analysis.AMProductRatingFragment;

public class AMProductAnalysisPagerAdapter extends FragmentStatePagerAdapter {
    public AMProductAnalysisPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new AMProductAmountFragment();
        }
        else if(i == 1){
            return new AMProductRatingFragment();
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
                return "Lượng mua / tháng";
            case 1:
                return "Đánh giá";
        }
        return null;
    }
}
