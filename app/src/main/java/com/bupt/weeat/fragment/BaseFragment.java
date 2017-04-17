package com.bupt.weeat.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.AsyncHttpClient;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    protected Context context;
    protected View rootView;
    public AsyncHttpClient client;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        //实例化一个对象
        client = AsyncHttpClient.getInstance(context.getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.inject(this, rootView);
        initData();
        setListener();
        return rootView;

    }

    protected abstract int getLayoutId();

    protected void initData() {

    }

    protected void setListener()

    {

    }

}
