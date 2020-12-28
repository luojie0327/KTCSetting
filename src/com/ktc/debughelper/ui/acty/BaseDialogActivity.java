package com.ktc.debughelper.ui.acty;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ktc.setting.R;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class BaseDialogActivity<Params, Result> extends BaseActivity {
    static final int PREPARE_LOAD_UI = 0x111;
    static final int LOAD_COMPLETE = 0X112;
    public TextView mActionTv;
    public TextView mDialogTitleTv;
    public FrameLayout mContainerFL;
    public ExecutorService executor = Executors.newSingleThreadExecutor();
    //--------for sync load data to ui---------------

    //    public Result result;
    //    public Params params;
    Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PREPARE_LOAD_UI:
                    asyncLoadData();
                    break;
                case LOAD_COMPLETE:
                    performDataToUi((Result) msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_shown);
        mActionTv = (TextView) findViewById(R.id.mActionTv);
        mContainerFL = (FrameLayout) findViewById(R.id.mContainerFL);
        mDialogTitleTv = (TextView) findViewById(R.id.mDialogTitleTv);

        mActionTv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mActionTv.setTranslationZ(10f);
                } else {
                    mActionTv.setTranslationZ(0f);
                }
            }
        });
        inflaterContainer();
    }

    //for create basic used component--------------
    public RecyclerView createRecycleView() {
        RecyclerView recyclerView = new RecyclerView(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setItemAnimator(null);
        return recyclerView;
    }

    public TextView createCenterTextView() {
        TextView textContent = new TextView(this);
        textContent.setTextSize(20f);
        textContent.setTextColor(Color.WHITE);
        textContent.setGravity(Gravity.CENTER);
        return textContent;
    }

    private void inflaterContainer() {
        if (beforeLoadUi()) {
            Message message = loadHandler.obtainMessage();
            message.what = PREPARE_LOAD_UI;
            loadHandler.sendMessage(message);
        }
    }

    private void asyncLoadData() {
        Future<Result> futureTask = executor.submit(new Callable<Result>() {
            @Override
            public Result call() throws Exception {
                return loadData();
            }
        });
        try {
            Result result = futureTask.get();
            Message message = loadHandler.obtainMessage();
            message.what = LOAD_COMPLETE;
            message.obj = result;
            loadHandler.sendMessage(message);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * load data in new thread
     */
    abstract public Result loadData();

    /**
     * callback after load the data in ui thread
     */
    abstract public void performDataToUi(Result result);

    /**
     * before load data to container ui ,init the other uis
     */
    abstract public Boolean beforeLoadUi();
}