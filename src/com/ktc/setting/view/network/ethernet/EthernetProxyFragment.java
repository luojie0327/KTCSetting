package com.ktc.setting.view.network.ethernet;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.NetworkHelper;
import com.ktc.setting.view.base.BaseFragment;
import com.ktc.setting.view.custom.ButtonSettingView;
import com.ktc.setting.view.custom.EditButton;
import com.ktc.setting.view.custom.EditDialog;
import com.ktc.setting.view.custom.MaxHeightRecyclerView;
import com.ktc.setting.view.custom.SettingViewContainer;
import com.ktc.setting.view.custom.ToastFactory;

import java.util.ArrayList;
import java.util.List;

public class EthernetProxyFragment extends BaseFragment implements ViewTreeObserver.OnGlobalLayoutListener {

    private NetworkHelper mNetworkHelper;
    private List<String> mDomainList;
    private DomainAdapter mAdapter;

    private SettingViewContainer mProxyContainer;
    private EditButton mHostButton;
    private EditButton mPortButton;
    private MaxHeightRecyclerView mDomainContainer;
    private ButtonSettingView mAddButton;
    private Button mSaveButton;
    private Button mClearButton;

    @Override
    protected int getRes() {
        return R.layout.fragment_ethernet_proxy;
    }

    @Override
    protected int getTitle() {
        return R.string.title_proxy;
    }

    @Override
    protected void initView(View view) {
        mProxyContainer = (SettingViewContainer) view.findViewById(R.id.proxy_container);
        mHostButton = (EditButton) view.findViewById(R.id.edit_proxy_host);
        mPortButton = (EditButton) view.findViewById(R.id.edit_proxy_port);
        mDomainContainer = (MaxHeightRecyclerView) view.findViewById(R.id.list_domain);
        mAddButton = (ButtonSettingView) view.findViewById(R.id.add_domain);
        mSaveButton = (Button) view.findViewById(R.id.button_save);
        mClearButton = (Button) view.findViewById(R.id.button_clear);

        mPortButton.setValueInputType(InputType.TYPE_CLASS_NUMBER);
        mDomainContainer.setLayoutManager(new LinearLayoutManager(mActivity));
        mDomainContainer.addItemDecoration(new SpaceItemDecoration(
                (int) (getResources().getDimension(R.dimen.space_recycler_item))));
        mDomainContainer.setAnimation(null);
        mDomainContainer.setClipToPadding(false);

        mHostButton.setHint("proxy.example.com");
        mPortButton.setHint("8080");
    }

    @Override
    protected void setFocus() {
        mProxyContainer.setNewFocus(mHostButton);
        //mHostButton.requestFocus();
        //mHostButton.requestFocusFromTouch();
    }

    @Override
    protected void initData() {
        mNetworkHelper = NetworkHelper.getInstance(mActivity);

        mHostButton.setValue(mNetworkHelper.getEthernetProxyHost());
        mPortButton.setValue(mNetworkHelper.getEthernetProxyPort());

        mDomainList = new ArrayList<String>();
        String[] list = mNetworkHelper.getEthernetProxyExcludeList();
        if (list != null && list.length > 0) {
            mDomainList.addAll(java.util.Arrays.asList(list));
            mDomainContainer.setVisibility(View.VISIBLE);
        }
        mAdapter = new DomainAdapter(mDomainList);
        mDomainContainer.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void addListener() {
        mAddButton.setOnButtonClickListener(new ButtonSettingView.OnButtonClickListener() {
            @Override
            public void onClick(View view) {
                final EditDialog dialog = new EditDialog(mActivity);
                dialog.setTitle(getString(R.string.title_edit_domain));
                dialog.setContent(getString(R.string.title_domain));
                dialog.setLeftButtonText(getString(R.string.network_save));
                dialog.setLeftButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDomainList.add(dialog.getValue());
                        mAddButton.getViewTreeObserver().addOnGlobalLayoutListener(EthernetProxyFragment.this);
                        mAdapter.notifyItemInserted(mDomainList.size());
                        mDomainContainer.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mAddButton.getViewTreeObserver().addOnGlobalLayoutListener(this);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkHelper.validateProxy(
                        mHostButton.getValueString(), mPortButton.getValueString(), mDomainList)) {
                    mNetworkHelper.setEthernetProxy(
                            mHostButton.getValueString(), mPortButton.getValueString(), mDomainList);
                } else {
                    ToastFactory.showToast(mActivity, mActivity.getString(R.string.network_invalid_input)
                            , Toast.LENGTH_SHORT);
                }
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHostButton.setValue("");
                mPortButton.setValue("");
                mDomainList.clear();
                mAdapter.notifyDataSetChanged();
                mDomainContainer.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onGlobalLayout() {
        View focus = mActivity.getCurrentFocus();
        if (focus == mAddButton) {
            mProxyContainer.onGlobalFocusChanged(null, focus);
        }
        mAddButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAddButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private class DomainListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String mDomainItem;
        private int mPosition;
        private TextView mName;

        public DomainListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.view_item_domain, parent, false));
            mName = (TextView) itemView.findViewById(R.id.domain_name);
            itemView.setOnClickListener(this);
            //            itemView.setOnHoverListener(new View.OnHoverListener() {
            //                @Override
            //                public boolean onHover(View view, MotionEvent motionEvent) {
            //                    itemView.requestFocus();
            //                    itemView.requestFocusFromTouch();
            //                    return true;
            //                }
            //            });
        }

        public void bind(String item, int position) {
            mDomainItem = item;
            mPosition = position;
            mName.setText(item);
        }

        @Override
        public void onClick(View view) {
            final EditDialog dialog = new EditDialog(mActivity);
            dialog.setTitle(getString(R.string.title_edit_domain));
            dialog.setContent(getString(R.string.title_domain));
            dialog.setValue(mDomainItem);

            dialog.setLeftButtonText(getString(R.string.network_save));
            dialog.setLeftButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mDomainItem.equals(dialog.getValue())) {
                        mDomainList.set(mPosition, dialog.getValue());
                        mAdapter.notifyItemChanged(mPosition);
                    }
                    dialog.dismiss();
                }
            });

            dialog.setRightButtonText(getString(R.string.network_delete));
            dialog.setRightButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDomainList.remove(mPosition);
                    mAdapter.notifyItemRemoved(mPosition);
                    mAdapter.notifyItemRangeChanged(0, mDomainList.size());
                    if (mDomainList.size() == 0) {
                        mDomainContainer.setVisibility(View.INVISIBLE);
                    }
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    private class DomainAdapter extends RecyclerView.Adapter<DomainListHolder> {

        List<String> mItems;

        public DomainAdapter(List<String> items) {
            mItems = items;
            setHasStableIds(true);
        }

        @Override
        public DomainListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DomainListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DomainListHolder holder, int position) {
            String item = mItems.get(position);
            holder.bind(item, position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = mSpace;
        }
    }
}
