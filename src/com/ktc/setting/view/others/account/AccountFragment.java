package com.ktc.setting.view.others.account;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.helper.SpaceItemDecoration;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.ToastFactory;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AccountFragment extends BaseFragment implements View.OnClickListener
        , AccountAdapter.OnItemSelectListener {

    private static final String TAG = AccountFragment.class.getSimpleName();
    private RecyclerView accountListView;
    private SettingViewContainer accountContainer;
    private Button deleteBtn;
    private LinearLayout hintView;
    private AccountAdapter mAccountAdapter;
    private List<AccountBean> mAccountBeans;
    private List<AccountBean> mAccountBeanNeedRemove;

    private static final int REMOVE_START = 1;
    private static final int REMOVE_SUCCESS = 2;
    private static final int REMOVE_FAIL = 3;
    private RemoveHandler mRemoveHandler;
    private int currentRemove = 0;
    private boolean isInit = false;

    @Override
    protected int getRes() {
        return R.layout.fragment_account;
    }

    @Override
    protected int getTitle() {
        return R.string.str_others_account_title;
    }

    @Override
    protected void initView(View view) {
        accountContainer = (SettingViewContainer) view.findViewById(R.id.account_container);
        accountListView = (RecyclerView) view.findViewById(R.id.account_list_view);
        deleteBtn = (Button) view.findViewById(R.id.account_delete_btn);
        hintView = (LinearLayout) view.findViewById(R.id.hint_no_account);
    }

    @Override
    protected void setFocus() {
        accountListView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (!isInit) {
                    accountContainer.setNewFocus(view);
                    isInit = true;
                    accountListView.removeOnChildAttachStateChangeListener(this);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });
    }

    @Override
    protected void initData() {
        mRemoveHandler = new RemoveHandler(this);
        mAccountBeans = AccountManager.getAccounts(getContext());
        refreshUI();
        mAccountBeanNeedRemove = new ArrayList<>();
        accountListView.setLayoutManager(new LinearLayoutManager(getContext()));
        accountListView.addItemDecoration(new SpaceItemDecoration(DestinyUtil.dp2px(getContext(), 6.7f)));
        mAccountAdapter = new AccountAdapter(getContext(), mAccountBeans);
        mAccountAdapter.setOnItemSelectListener(this);
        accountListView.setAdapter(mAccountAdapter);
    }

    @Override
    protected void addListener() {
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_delete_btn:
                removeAccount();
                break;
        }
    }

    private void removeAccount() {
        if (mAccountBeanNeedRemove.size() > 0) {
            mRemoveHandler.sendEmptyMessage(REMOVE_START);
        } else {
            ToastFactory.showToast(getContext(), getResources().getString(R.string.str_others_account_remove_tip)
                    , Toast.LENGTH_SHORT);
        }
    }

    private void refreshUI() {
        if (mAccountBeans != null && mAccountBeans.size() > 0) {
            accountContainer.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
            hintView.setVisibility(View.GONE);
            accountListView.requestFocus();
        } else {
            accountContainer.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            hintView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onItemSelect(boolean isCheck) {
        AccountBean accountBean = mAccountBeans.get(getCurrentPosition());
        if (isCheck) {
            mAccountBeanNeedRemove.add(accountBean);
        } else {
            if (mAccountBeanNeedRemove.contains(accountBean)) {
                mAccountBeanNeedRemove.remove(accountBean);
            }
        }
    }

    public int getCurrentPosition() {
        for (int i = 0; i < accountListView.getChildCount(); i++) {
            if (accountListView.getChildAt(i).hasFocus()) {
                return i;
            }
        }
        return 0;
    }

    private AccountManagerCallback<Boolean> mCallback = new AccountManagerCallback<Boolean>() {
        @Override
        public void run(AccountManagerFuture<Boolean> future) {
            try {
                if (!future.getResult()) {
                    ToastFactory.showToast(getContext(), getResources().getString(R.string.str_others_account_remove_fail)
                            , Toast.LENGTH_SHORT);
                    mRemoveHandler.sendEmptyMessage(REMOVE_FAIL);
                } else {
                    mRemoveHandler.sendEmptyMessage(REMOVE_SUCCESS);
                }
            } catch (OperationCanceledException | AuthenticatorException | IOException e) {
                e.printStackTrace();
            }
        }
    };

    static class RemoveHandler extends Handler {

        WeakReference<AccountFragment> mWeakReference;
        AccountFragment mAccountFragment;
        android.accounts.AccountManager manager;
        List<AccountBean> needRemove;

        RemoveHandler(AccountFragment accountFragment) {
            mWeakReference = new WeakReference<>(accountFragment);
            mAccountFragment = mWeakReference.get();
            manager = android.accounts.AccountManager.get(mAccountFragment.getContext());
            needRemove = new ArrayList<>();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REMOVE_START:
                    mAccountFragment.currentRemove = 0;
                    AccountBean accountBean = mAccountFragment.mAccountBeanNeedRemove.get(0);
                    Log.d(TAG, "REMOVE_START name : " + accountBean.getAccount().name);
                    manager.removeAccount(accountBean.getAccount(), mAccountFragment.mCallback, new Handler());
                    break;
                case REMOVE_SUCCESS:
                    needRemove.add(mAccountFragment.mAccountBeanNeedRemove.get(mAccountFragment.currentRemove));
                    mAccountFragment.currentRemove++;
                    if (mAccountFragment.currentRemove < mAccountFragment.mAccountBeanNeedRemove.size()) {
                        AccountBean accountBeanSu = mAccountFragment.mAccountBeanNeedRemove.get(mAccountFragment.currentRemove);
                        Log.d(TAG, "REMOVE_SUCCESS name : " + accountBeanSu.getAccount().name);
                        manager.removeAccount(accountBeanSu.getAccount(), mAccountFragment.mCallback, new Handler());
                    } else {
                        clearDataAndRefreshUI();
                    }
                    break;
                case REMOVE_FAIL:
                    mAccountFragment.currentRemove++;
                    if (mAccountFragment.currentRemove < mAccountFragment.mAccountBeanNeedRemove.size()) {
                        AccountBean accountBeanFa = mAccountFragment.mAccountBeanNeedRemove.get(mAccountFragment.currentRemove);
                        Log.d(TAG, "REMOVE_FAIL name : " + accountBeanFa.getAccount().name);
                        manager.removeAccount(accountBeanFa.getAccount(), mAccountFragment.mCallback, new Handler());
                    } else {
                        clearDataAndRefreshUI();
                    }
                    break;
            }
        }

        void clearDataAndRefreshUI() {
            for (int i = 0; i < needRemove.size(); i++) {
                if (mAccountFragment.mAccountBeans.contains(needRemove.get(i))) {
                    mAccountFragment.mAccountBeans.remove(needRemove.get(i));
                }
                if (mAccountFragment.mAccountBeanNeedRemove.contains(needRemove.get(i))) {
                    mAccountFragment.mAccountBeanNeedRemove.remove(needRemove.get(i));
                }
            }
            if (needRemove.size() > 0) {
                mAccountFragment.mAccountAdapter.notifyDataSetChanged();
            }
            mAccountFragment.refreshUI();
        }
    }
}
