package com.spx.scrollertest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

//import com.github.chrisbanes.photoview.PhotoView;

/**
 * Created by shaopengxiang on 2017/11/10.
 */

public class MyPhotoView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "MyPhotoView";
    private Paint mPait = new Paint();

    public MyPhotoView(Context context) {
        super(context);
        init(context);
    }

    public MyPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public MyPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }

    private void init(Context context) {
        setLayerType(ViewCompat.LAYER_TYPE_SOFTWARE, mPait);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        Log.d(TAG, "onDraw: drawable:" + drawable);
        if (drawable instanceof BitmapDrawable) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            Log.d(TAG, "onDraw: intrinsicWidth:" + intrinsicWidth + ", intrinsicHeight:" + intrinsicHeight);

            float scale = 1f;
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            Log.d(TAG, "handleBitmap: screenWidth:" + screenWidth + ",bitmapWidth:" + intrinsicWidth);
            if (screenWidth > intrinsicWidth) {
                scale = 1f * screenWidth / intrinsicWidth;
            }
            Log.d(TAG, "handleBitmap: scale:" + scale);
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            BitmapDrawable drawable1 = (BitmapDrawable) drawable;
            canvas.drawBitmap(drawable1.getBitmap(), matrix, mPait);
        }

//        super.onDraw(canvas);
    }
}
