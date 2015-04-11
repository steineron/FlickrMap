package com.flickrmap.flickrmap.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ron on 4/10/15.
 */
public class HorizontalSpaceDecorator extends RecyclerView.ItemDecoration {

    private int space;

    /**
     * @param space - HALF the space to use when decorating the offsets.
     */
    public HorizontalSpaceDecorator(int space) {

        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        // Add left margin only for the first item to avoid double space between items
        outRect.left = parent.getChildPosition(view) == 0 ?
                       2 * space :
                       space;
        // Add right margin only for the last item to avoid double space between items
        outRect.right = parent.getChildPosition(view) == parent.getChildCount() - 1 ?
                        2 * space :
                        space;
        outRect.bottom = 2 * space;

        outRect.top = 2 * space;
    }
}
