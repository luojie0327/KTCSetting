package com.ktc.debughelper.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ktc.debughelper.ui.adapter.RowDataAdapter;
import com.ktc.debughelper.ui.adapter.RvBaseAdapter;

public class TvScrollView extends ScrollView {
    LinearLayout llContentView;

    public TvScrollView(Context context) {
        this(context, null);
    }

    public TvScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TvScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        llContentView = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llContentView.setLayoutParams(layoutParams);
        llContentView.setOrientation(LinearLayout.VERTICAL);
        this.addView(llContentView);
    }

    public void addRow(RowView<RowDataAdapter> rowView) {
        llContentView.addView(rowView);
    }

    public void setOnItemClickListener(RvBaseAdapter.OnItemClickListener listener) {
        int count = llContentView.getChildCount();
        for (int i = 0; i < count; i++) {
            RowView rowView = (RowView) llContentView.getChildAt(i);
            ((RvBaseAdapter) rowView.getAdapter()).setOnItemClickListener(listener);
        }
    }
}