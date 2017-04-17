package com.bupt.weeat.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bupt.weeat.R;
import com.bupt.weeat.adapter.FindPagerAdapter;
import com.bupt.weeat.ui.SlidingTabLayout;

//一个动态加载的fragment
public class FindFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.find_fragment_layout, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ViewPager viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        viewpager.setAdapter(new FindPagerAdapter(getChildFragmentManager()));
        SlidingTabLayout tab = (SlidingTabLayout) view.findViewById(R.id.tab);
        tab.setCustomTabView(R.layout.custom_tab_layout, 0);
        tab.setViewPager(viewpager);
        tab.setDistributeEvenly(true);
        tab.setCustomTabColorizer(
                new SlidingTabLayout.TabColorizer() {
                    @Override
                    public int getIndicatorColor(int position) {
                        return getResources().getColor(R.color.primary);
                    }
                }
        );
        tab.setMinimumHeight(20);

    }


}

