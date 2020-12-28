package com.ktc.debughelper.view;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class KItemDecoration extends RecyclerView.ItemDecoration {
    int top;
    int left;
    int right;
    int bottom;

    public KItemDecoration(int top, int left, int bottom, int right) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(left, top, right, bottom);
    }
}