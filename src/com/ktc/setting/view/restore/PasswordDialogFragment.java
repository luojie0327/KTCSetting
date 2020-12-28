package com.ktc.setting.view.restore;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ktc.setting.R;
import com.ktc.setting.helper.DestinyUtil;
import com.ktc.setting.view.custom.ToastFactory;

public class PasswordDialogFragment extends DialogFragment implements View.OnClickListener {

    private Button okButton;
    private Button cancelButton;
    private Button clearButton;
    private EditText passwordEditText;
    private String password;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_fragment_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        addListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = DestinyUtil.dp2px(getContext(), 630);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
    }

    private void initView(View view) {
        okButton = (Button) view.findViewById(R.id.dialog_password_ok_button);
        cancelButton = (Button) view.findViewById(R.id.dialog_password_cancel_button);
        clearButton = (Button) view.findViewById(R.id.dialog_password_clear_button);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
    }

    private void initData(){
        password = PasswordManager.getInstance(getContext()).getCurrentPassword();
    }

    private void addListener() {
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_password_ok_button:
                if (passwordEditText.getText().toString().trim()
                        .equals(password)) {
                    FragmentManager fragmentManager = getFragmentManager();
                    new RestoreDialogFragment().show(fragmentManager, "restore_fragment");
                    dismiss();
                } else {
                    ToastFactory.showToast(getContext(), getString(R.string.str_password_error_tip), Toast.LENGTH_SHORT);
                }
                break;
            case R.id.dialog_password_cancel_button:
                dismiss();
                break;
            case R.id.dialog_password_clear_button:
                passwordEditText.setText("");
                break;
        }
    }
}
