package com.ktc.setting.view.restore;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import java.io.File;
import java.util.List;
import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.view.restore.restoreTool.IRestore;
import com.ktc.setting.view.restore.restoreTool.MtkRestoreTool;
import com.ktc.setting.view.restore.restoreTool.RestoreFactory;
import com.mediatek.twoworlds.factory.MtkTvFApiSystem;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import java.lang.ref.WeakReference;

public class RestoreDialogFragment extends DialogFragment implements View.OnClickListener {

    private Button restoreButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_fragment_restore, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        addListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = DestinyUtil.dp2px(getContext(), 667);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {
        restoreButton = (Button) view.findViewById(R.id.restore_now_button);
    }

    private void addListener() {
        restoreButton.setOnClickListener(this);
    }
	
	private void restorelogo() {
        String defaultFilePath = new String();
        File defaultFile;
        defaultFilePath = "/vendor/tvconfig/config/customer/bootlogo.jpg";
        defaultFile = new File(defaultFilePath);
        if (!defaultFile.exists()) {
            return ;
        }
        int result = MtkTvFApiSystem.getInstance().changeBootlogo(defaultFilePath);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restore_now_button:
                while(MtkTvRecord.getInstance().getBookingList().size()>0)
                {
                    MtkTvRecord.getInstance().deleteAllBooking();
                }
                RestoreAsyncTask restoreAsyncTask = new RestoreAsyncTask(getContext());
                restoreAsyncTask.execute();
                break;
        }
    }

    class RestoreAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> mContextWeakReference;
        private Context mContext;
        private IRestore abstractRestore;

        RestoreAsyncTask(Context context) {
            mContextWeakReference = new WeakReference<>(context);
            mContext = mContextWeakReference.get();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog.show(mContext, "", mContext.getString(R.string.str_update_restore_content)
                    , false, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
			//restorelogo();
            abstractRestore = RestoreFactory.createRestore(MtkRestoreTool.class);
            abstractRestore.restoreTvData(mContext);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            abstractRestore.restoreAndroid(mContext);
            super.onPostExecute(aVoid);
        }
    }
}
