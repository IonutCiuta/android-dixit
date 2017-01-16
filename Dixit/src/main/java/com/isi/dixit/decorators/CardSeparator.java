package com.isi.dixit.decorators;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CardSeparator extends RecyclerView.ItemDecoration{
    private int dist;

    public CardSeparator(int dist) {
        this.dist = dist;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.right = dist;
        outRect.left = dist;
    }
}
