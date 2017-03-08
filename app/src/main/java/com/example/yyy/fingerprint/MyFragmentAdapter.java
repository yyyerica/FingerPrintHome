package com.example.yyy.fingerprint;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;//保存用于滑动的Fragment对象

    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);

        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int arg0) { //根据传来的参数arg0来返回当前 要显示的fragment
        return fragmentList.get(arg0);
    }

    @Override
    public int getCount() { //返回用于滑动的fragment总数；
        return fragmentList.size();
    }
}
