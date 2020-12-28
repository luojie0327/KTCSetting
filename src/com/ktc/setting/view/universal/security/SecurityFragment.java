package com.ktc.setting.view.universal.security;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.helper.SpaceItemDecoration;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.SettingViewContainer;


public class SecurityFragment extends BaseFragment {

    private RecyclerView securityListView;
    private SettingViewContainer securityContainer;
    private boolean isInit = false;

    @Override
    protected int getRes() {
        return R.layout.fragment_security;
    }

    @Override
    protected int getTitle() {
        return R.string.str_universal_security;
    }

    @Override
    protected void initView(View view) {
        securityContainer = (SettingViewContainer) view.findViewById(R.id.security_container);
        securityListView = (RecyclerView) view.findViewById(R.id.security_recycler);
    }

    @Override
    protected void setFocus() {
        securityListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (!isInit) {
                    securityContainer.setNewFocus(view);
                    isInit = true;
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    @Override
    protected void initData() {
        securityListView.setLayoutManager(new LinearLayoutManager(getContext()));
        securityListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(getContext(), 7)));
        SecurityManager mSecurityManager = new SecurityManager(getContext());
        SecurityRecyclerAdapter adapter = new SecurityRecyclerAdapter(getContext(), mSecurityManager, securityListView);
        securityListView.setAdapter(adapter);
    }

    @Override
    protected void addListener() {

    }

}
