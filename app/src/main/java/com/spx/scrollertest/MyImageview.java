package com.spx.scrollertest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.OverScroller;

/**
 * Created by shaopengxiang on 2017/11/10.
 */

public class MyImageview extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "MyImageview";

    public static final float MAX_SCALE = 3.0f;
    public static final float DOUBLE_TAB_SCALE = 1.5f;
    private static final long ANIMATE_BACK_DURATION = 200;

    public interface ScrollOutListener {
        void onScrollOut();
    }

    public void setScrollOutListener(ScrollOutListener scrollOutListener) {
        this.scrollOutListener = scrollOutListener;
    }

    private ScrollOutListener scrollOutListener = null;

    private OverScroller mScroller;

    private Bitmap mBitmap;
    private Paint mPait = new Paint();

    private float mCurrentScale = 1f;
    private Matrix mCurrentMatrix;
    private int mTranslateX = 0;
    private float mMidX;
    private float mMidY;
    private int mScrollX;
    private int mScrollY;
    private int mMaxScrollY;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mScrollGestureDetector;

    private Runnable mCheckBottomTask = new Runnable() {
        @Override
        public void run() {
            checkBottom();
        }
    };

    public MyImageview(Context context) {
        super(context);
        init(context);
    }

    public MyImageview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyImageview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mCurrentMatrix = new Matrix();

        setLayerType(ViewCompat.LAYER_TYPE_SOFTWARE, mPait);
        mScroller = new OverScroller(getContext());
        setFocusable(true);

        setWillNotDraw(false);

        GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d(TAG, "onSingleTapConfirmed: ...");
                if (mCurrentScale > 1) {
                    mScrollX = 0;
                    resetScale();
                    return true;
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.d(TAG, "onDoubleTap: ...");
                if (mCurrentScale >= MAX_SCALE) {
                    mScrollX = 0;
                    resetScale();
                    return true;
                }
                setScale(DOUBLE_TAB_SCALE);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d(TAG, "onScroll: mScrollX:" + mScrollX + ", mScrollY:" + mScrollY + ", distanceX:" + distanceX + ", distanceY:" + distanceY);
                if ((mScrollY) > (mBitmap.getHeight() - getHeight() * 2 / 3)) {
                    onScrollOut();
                    return true;
                }


                if(mCurrentScale>1){
//                    RectF rectF = getDisplayRect(mCurrentMatrix);
//                    Log.d(TAG, "onScroll: rectF:" + rectF);
//                    if (rectF.left > 0) {
//                        mScrollX = 0;
//                    } else if (rectF.right < getWidth()*mCurrentScale) {
//                        if (mScrollX < 0) {
//                            mScrollX += getWidth() - rectF.right;
//                        } else {
//                            mScrollX -= getWidth() - rectF.right;
//                        }
//
//                    } else {
//                        if (mScrollX + distanceX < 0) {
//                            mScrollX = 0;
//                        } else {
//                            mScrollX += distanceX;
//                        }
//                    }
                    mScrollX += distanceX;
                } else{
                    mScrollX = 0;
                }



                mScrollY += distanceY;


//                checkBorder();

                invalidate();
                return true;
            }


            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling: mScrollY:" + mScrollY + ", velocityY:" + velocityY);
                removeCallbacks(mCheckBottomTask);
                if ((mScrollY) >= (mBitmap.getHeight() - getHeight())) {
                    if ((mScrollY) < (mBitmap.getHeight() - getHeight() * 2 / 3)) {
                        animateBack();
                    } else {
                        onScrollOut();
                    }
                    return true;
                }

                fling(-velocityY);
                return true;
            }
        };
        mScrollGestureDetector = new GestureDetector(getContext(), gestureListener);


        ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector
                .SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                Log.d(TAG, "onScale: scaleFactor:" + scaleFactor);
                setScale(scaleFactor);

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);

                if (mCurrentScale < 1f) {
                    reset();
                }
            }
        };
        mScaleDetector = new ScaleGestureDetector(getContext(), scaleListener);
    }

    private void resetScale() {
        mCurrentScale = 1f;
        setScale(1f);
    }

    /**
     * 检查图片边界是否移到view以内
     * 目的是让图片边缘不要移动到view里面
     */
    private void checkBorder() {
        RectF rectF = getDisplayRect(mCurrentMatrix);
        Log.d(TAG, "checkBorder: rectF:" + rectF + ", mScrollX:" + mScrollX + ", mScrollY:" + mScrollY);
        boolean reset = false;
        float dx = 0;
        float dy = 0;

        if (rectF.left > 0) {
            dx = getLeft() - rectF.left;
            reset = true;
        }
        if (rectF.top > 0) {
            dy = getTop() - rectF.top;
            reset = true;
        }
        if (rectF.right < getRight()) {
            dx = getRight() - rectF.right;
            reset = true;
        }
//        if (rectF.bottom < getHeight()) {
//            dy = getHeight() - rectF.bottom;
//            reset = true;
//        }
        if (reset) {
            mScrollX = 0;
            invalidate();
        }
    }

    private RectF getDisplayRect(Matrix matrix) {
        RectF rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
        matrix.mapRect(rectF);
        return rectF;
    }

    private void animateBack() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(mScrollY, mMaxScrollY);
        valueAnimator.setDuration(ANIMATE_BACK_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int scrollY = (int) animation.getAnimatedValue();
                mScrollY = scrollY;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void onScrollOut() {
        Log.d(TAG, "onScrollOut: ...");
        if (scrollOutListener != null) {
            scrollOutListener.onScrollOut();
        }
    }

    /**
     * Resets the zoom of the attached image.
     * This has no effect if the image has been destroyed
     */
    private void reset() {
        mCurrentMatrix.reset();
        mCurrentScale = 1f;
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    public void setScale(float scaleFactor) {
        mCurrentScale *= scaleFactor;
        if (mMidX == 0f) {
            mMidX = getWidth() / 2f;
        }
        if (mMidY == 0f) {
            mMidY = getHeight() / 2f;
        }

        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        mMaxScrollY = bitmap.getHeight() - getHeight();
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mScaleDetector.onTouchEvent(ev);
        if (!mScaleDetector.isInProgress()) {
            mScrollGestureDetector.onTouchEvent(ev);
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            postDelayed(mCheckBottomTask, 100);
        }

        return true;
    }

    private void checkBottom() {
        if ((mScrollY) >= (mBitmap.getHeight() - getHeight())) {
            if ((mScrollY) > (mBitmap.getHeight() - getHeight() * 2 / 3)) {
                onScrollOut();
            } else {
                animateBack();
            }

        }
    }


    private void fling(float initialVelocity) {
        Log.d(TAG, "fling: initialVelocity:" + initialVelocity);
        mScroller.fling(0, mScrollY, 0, (int) initialVelocity, 0, 0, 0, mBitmap.getHeight() - getHeight());
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
//            Log.d(TAG, "computeScroll: mScroller.getCurrY():" + mScroller.getCurrY());
            mScrollY = mScroller.getCurrY();
            postInvalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (mBitmap == null) {
            return;
        }
//        Log.d(TAG, "onDraw: mCurrentScale:" + mCurrentScale + ", getHeight:" + getHeight() + ", bitmap.h:" + mBitmap.getHeight());
        int saveCount = canvas.save();

        mCurrentMatrix.reset();
        mCurrentMatrix.postTranslate(-mScrollX, -mScrollY);
        mCurrentMatrix.postScale(mCurrentScale, mCurrentScale, mMidX, mMidY);


        canvas.drawBitmap(mBitmap, mCurrentMatrix, null);

        canvas.restoreToCount(saveCount);
    }
}

