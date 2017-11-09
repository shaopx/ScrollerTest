package com.spx.scrollertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * @auther shaopx
 * @date 2017/11/9.
 */

public class MyLinearLayout extends LinearLayout {
    private static final String TAG = "MyLinearLayout";
    private VelocityTracker mVelocityTracker;

    private Context mContext;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private OverScroller mScroller;

    private Matrix matrix = new Matrix();
    private Bitmap mBitmap;
    private Paint mPait = new Paint();

    public MyLinearLayout(Context context) {
        super(context);
        init(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
//        mOverscrollDistance = configuration.getScaledOverscrollDistance();
//        mOverflingDistance = configuration.getScaledOverflingDistance();
//        mVerticalScrollFactor = configuration.getScaledVerticalScrollFactor();

        setWillNotCacheDrawing(false);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();

        MotionEvent vtev = MotionEvent.obtain(ev);

        final int actionMasked = ev.getActionMasked();


        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                if (getChildCount() == 0) {
                    return false;
                }

            }
            case MotionEvent.ACTION_MOVE:


                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                Log.d(TAG, "onTouchEvent: mMinimumVelocity:" + mMinimumVelocity + ", initialVelocity:" + initialVelocity+", curY:"+mScroller.getCurrY());

                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    fling(-initialVelocity);
                }

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
            case MotionEvent.ACTION_POINTER_DOWN: {

                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                mLastMotionY = (int) ev.getY(ev.findPointerIndex(mActivePointerId));
                break;
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(vtev);
        }
        vtev.recycle();
        return true;
    }

    private void fling(int initialVelocity) {
        Log.d(TAG, "fling: initialVelocity:" + initialVelocity);
        mScroller.fling(0, getScrollY(), 0, initialVelocity, 0, 0, 50, 915);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.d(TAG, "computeScroll: mScroller.getCurrY():" + mScroller.getCurrY());
//            scrollTo(mScroller.getCurrX(),
//                    mScroller.getCurrY());
            matrix.setTranslate(0, -mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.concat(matrix);

        canvas.drawBitmap(mBitmap, matrix, mPait);

        canvas.restore();
    }
}
