package com.ktc.setting.view.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ktc.setting.R;

public class ToastFactory {

    public static void showToast(Context context, String message, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView toastText = (TextView) view.findViewById(R.id.toast_text);
        toastText.setText(message);
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }
}
