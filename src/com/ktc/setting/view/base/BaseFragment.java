package com.ktc.setting.view.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

    protected BaseActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int res = getRes();
        View view = inflater.inflate(res, container, false);
        mActivity = (BaseActivity) getActivity();
        mActivity.setTitle(getTitle());
        mActivity.setSubTitle("");
        initView(view);
        initData();
        addListener();
        setFocus();
        return view;
    }

    protected abstract int getRes();

    protected abstract int getTitle();

    protected abstract void initView(View view);

    protected abstract void setFocus();

    protected abstract void initData();

    protected abstract void addListener();
}
