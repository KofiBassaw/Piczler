package com.piczler.piczler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by matiyas on 12/4/15.
 */
public class MySurfaceView extends SurfaceView {


    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(1200, widthMeasureSpec);
        int height = getDefaultSize(700, heightMeasureSpec);

        // Do not change w & h for screen fill
        setMeasuredDimension(width, height);
    }
}
