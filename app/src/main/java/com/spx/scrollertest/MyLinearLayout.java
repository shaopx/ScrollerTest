package com.spx.scrollertest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * @auther shaopx
 * @date 2017/11/9.
 */

public class MyLinearLayout extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = "MyLinearLayout";
    /**
     * Sentinel value for no current active pointer.
     * Used by {@link #mActivePointerId}.
     */
    private static final int INVALID_POINTER = -1;


    private VelocityTracker mVelocityTracker;

    private Context mContext;

    private int mTouchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;

    private OverScroller mScroller;

    private Bitmap mBitmap;
    private Paint mPait = new Paint();

    private float mCurrentScale = 1f;
    private Matrix mCurrentMatrix;
    private Matrix mTranslateMatrix;
    private Matrix mScaleMtrix;
    private int mTranslateX = 0;
    private float mMidX;
    private float mMidY;
    private int mScrollY;

    /**
     * Position of the last motion event.
     */
    private int mLastMotionY;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    private boolean mIsBeingDragged = false;

    private ScaleGestureDetector mScaleDetector;

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
        mCurrentMatrix = new Matrix();
        mTranslateMatrix = new Matrix();
        mScaleMtrix = new Matrix();
        setLayerType(ViewCompat.LAYER_TYPE_SOFTWARE, mPait);
        mScroller = new OverScroller(getContext());
        setFocusable(true);
//        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setWillNotDraw(false);
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
//        mOverscrollDistance = configuration.getScaledOverscrollDistance();
//        mOverflingDistance = configuration.getScaledOverflingDistance();
//        mVerticalScrollFactor = configuration.getScaledVerticalScrollFactor();

        setWillNotCacheDrawing(false);
//        mCurrentMatrix.setTranslate(mTranslateX, 0);
//        mCurrentMatrix.postTranslate(mTranslateX, 0);
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timg);

        ScaleGestureDetector.OnScaleGestureListener scaleListener = new ScaleGestureDetector
                .SimpleOnScaleGestureListener() {

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                Log.d(TAG, "onScale: scaleFactor:"+scaleFactor);
                setScale(scaleFactor);

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);

                if (mCurrentScale < 1f) {
                    reset();
                }
//                checkBorder();
            }
        };
        mScaleDetector = new ScaleGestureDetector(getContext(), scaleListener);
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
        Log.d(TAG, "onFinishInflate: width:" + getWidth() + ", height:" + getHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout: width:" + getWidth() + ", height:" + getHeight() + ", r:" + r + ", b:" + b);
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
        invalidate();
    }

    private void initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        Log.d(TAG, "onTouchEvent: ev:"+ev);

        if(ev.getPointerCount()>1){
           return mScaleDetector.onTouchEvent(ev);
        }


        MotionEvent vtev = MotionEvent.obtain(ev);

        final int actionMasked = ev.getActionMasked();


        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {

                if ((mIsBeingDragged = !mScroller.isFinished())) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                }

                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionY = (int) ev.getY();
                mActivePointerId = ev.getPointerId(0);

            }
            case MotionEvent.ACTION_MOVE:


                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
                if (activePointerIndex == -1) {
                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
                    break;
                }

                final int y = (int) ev.getY(activePointerIndex);
                int deltaY = mLastMotionY - y;
                Log.d(TAG, "onTouchEvent: ACTION_MOVE  deltaY:" + deltaY + ", mScrollY:" + mScrollY);

                if (!mIsBeingDragged && Math.abs(deltaY) > mTouchSlop) {
                    final ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(true);
                    }
                    mIsBeingDragged = true;
                    if (deltaY > 0) {
                        deltaY -= mTouchSlop;
                    } else {
                        deltaY += mTouchSlop;
                    }
                }


                if (mIsBeingDragged) {
                    // Scroll to follow the motion event
                    mLastMotionY = y;

                    if (mScrollY <= 0 && deltaY < 0) {
                        mScrollY = 0;
                    } else if (isToEnd(deltaY)) {

                    } else {
                        mScrollY += deltaY;
                    }


                }

                invalidate();

                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getYVelocity();
                Log.d(TAG, "onTouchEvent: mMinimumVelocity:" + mMinimumVelocity + ", initialVelocity:" + initialVelocity + ", curY:" + mScroller.getCurrY());

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

    private boolean isToEnd(int deltaY) {
        if ((mScrollY+deltaY) >= (mBitmap.getHeight() - getHeight()) && deltaY > 0) {
            mScrollY = mBitmap.getHeight() - getHeight();
            return true;
        }
        return false;
    }

    private void fling(int initialVelocity) {
        Log.d(TAG, "fling: initialVelocity:" + initialVelocity);
        mScroller.fling(0, mScrollY, 0, initialVelocity, 0, 0, 0, mBitmap.getHeight()-getHeight());
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            Log.d(TAG, "computeScroll: mScroller.getCurrY():" + mScroller.getCurrY());
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
        Log.d(TAG, "onDraw: mCurrentScale:" + mCurrentScale + ", getHeight:" + getHeight() + ", bitmap.h:" + mBitmap.getHeight());
        int saveCount = canvas.save();

        mCurrentMatrix.reset();
        mCurrentMatrix.postTranslate(mTranslateX, -mScrollY);
        mCurrentMatrix.postScale(mCurrentScale, mCurrentScale, mMidX, mMidY);


        canvas.drawBitmap(mBitmap, mCurrentMatrix, null);

        canvas.restoreToCount(saveCount);
    }
}
