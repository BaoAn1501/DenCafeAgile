package com.antbps15545.dencafeagile.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;


import com.antbps15545.dencafeagile.product.ListProductFragment;
import com.antbps15545.dencafeagile.product.ListProductTypeFragment;

public class AddProductPagerAdapter extends FragmentStatePagerAdapter {
    public AddProductPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if(i == 0){
            return new ListProductFragment();
        }
        else if(i == 1){
            return new ListProductTypeFragment();
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
                return "Món";
            case 1:
                return "Loại món";
        }
        return null;
    }
}
