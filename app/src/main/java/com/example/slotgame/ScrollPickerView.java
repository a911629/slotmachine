package com.example.slotgame;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

import com.example.slotgame.R;

/**
 * 滚动选择器,带惯性滑动
 *
 * @author huangziwei
 * @date 2016.1.11
 */
public abstract class ScrollPickerView<T> extends View {

    private int mVisibleItemCount = 3; // 可见的item数量

    private boolean mIsInertiaScroll = true;
    private boolean mIsCirculation = true;

    private boolean mDisallowInterceptTouch = true;

    private int mSelected;
    private List<T> mData;
    private int mItemHeight = 0;
    private int mItemWidth = 0;
    private int mItemSize;
    private int mCenterPosition = -1;
    private int mCenterY;
    private int mCenterX;
    private int mCenterPoint;
    private float mLastMoveY;
    private float mLastMoveX;

    private float mMoveLength = 0;

    private GestureDetector mGestureDetector;
    private OnSelectedListener mListener;

    private Scroller mScroller;
    private boolean mIsFling;
    private boolean mIsMovingCenter;
    private int mLastScrollY = 0;
    private int mLastScrollX = 0;

    private boolean mDisallowTouch = false;

    private Paint mPaint;
    private Drawable mCenterItemBackground = null;

    private boolean mCanTap = true;

    private boolean mIsHorizontal = false;

    private boolean mDrawAllItem = false;

    private boolean mHasCallSelectedListener = false;

    public ScrollPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollPickerView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(getContext(),
                new FlingOnGestureListener());
        mScroller = new Scroller(getContext());
        mAutoScrollAnimator = ValueAnimator.ofInt(0, 0);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        init(attrs);

    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.ScrollPickerView);

            if (typedArray.hasValue(R.styleable.ScrollPickerView_spv_center_item_background)) {
                setCenterItemBackground(typedArray.getDrawable(R.styleable.ScrollPickerView_spv_center_item_background));
            }
            setVisibleItemCount(typedArray.getInt(
                    R.styleable.ScrollPickerView_spv_visible_item_count,
                    getVisibleItemCount()));
            setCenterPosition(typedArray.getInt(
                    R.styleable.ScrollPickerView_spv_center_item_position,
                    getCenterPosition()));
            setIsCirculation(typedArray.getBoolean(R.styleable.ScrollPickerView_spv_is_circulation, isIsCirculation()));
            setDisallowInterceptTouch(typedArray.getBoolean(R.styleable.ScrollPickerView_spv_disallow_intercept_touch, isDisallowInterceptTouch()));
            setHorizontal(typedArray.getInt(R.styleable.ScrollPickerView_spv_orientation, mIsHorizontal ? 1 : 2) == 1);
            typedArray.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mData == null || mData.size() <= 0) {
            return;
        }

        if (mCenterItemBackground != null) {
            mCenterItemBackground.draw(canvas);
        }

        int length = Math.max(mCenterPosition + 1, mVisibleItemCount - mCenterPosition);
        int position;
        int start = Math.min(length, mData.size());
        if (mDrawAllItem) {
            start = mData.size();
        }

        for (int i = start; i >= 1; i--) { // 先从远离中间位置的item绘制，当item内容偏大时，较近的item覆盖在较远的上面

            if (mDrawAllItem || i <= mCenterPosition + 1) {  // 上面的items,相对位置为 -i
                position = mSelected - i < 0 ? mData.size() + mSelected - i
                        : mSelected - i;
                if (mIsCirculation) {
                    drawItem(canvas, mData, position, -i, mMoveLength, mCenterPoint + mMoveLength - i * mItemSize);
                } else if (mSelected - i >= 0) {
                    drawItem(canvas, mData, position, -i, mMoveLength, mCenterPoint + mMoveLength - i * mItemSize);
                }
            }
            if (mDrawAllItem || i <= mVisibleItemCount - mCenterPosition) {  // 下面的items,相对位置为 i
                position = mSelected + i >= mData.size() ? mSelected + i
                        - mData.size() : mSelected + i;
                if (mIsCirculation) {
                    drawItem(canvas, mData, position, i, mMoveLength, mCenterPoint + mMoveLength + i * mItemSize);
                } else if (mSelected + i < mData.size()) { // 非循环滚动
                    drawItem(canvas, mData, position, i, mMoveLength, mCenterPoint + mMoveLength + i * mItemSize);
                }
            }
        }
        drawItem(canvas, mData, mSelected, 0, mMoveLength, mCenterPoint + mMoveLength);
    }

    public abstract void drawItem(Canvas canvas, List<T> data, int position, int relative, float moveLength, float top);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        reset();
    }

    private void reset() {
        if (mCenterPosition < 0) {
            mCenterPosition = mVisibleItemCount / 2;
        }

        if (mIsHorizontal) {
            mItemHeight = getMeasuredHeight();
            mItemWidth = getMeasuredWidth() / mVisibleItemCount;

            mCenterY = 0;
            mCenterX = mCenterPosition * mItemWidth;

            mItemSize = mItemWidth;
            mCenterPoint = mCenterX;
        } else {
            mItemHeight = getMeasuredHeight() / mVisibleItemCount;
            mItemWidth = getMeasuredWidth();

            mCenterY = mCenterPosition * mItemHeight;
            mCenterX = 0;

            mItemSize = mItemHeight;
            mCenterPoint = mCenterY;
        }

        if (mCenterItemBackground != null) {
            mCenterItemBackground.setBounds(mCenterX, mCenterY, mCenterX + mItemWidth, mCenterY + mItemHeight);
        }

    }

    private int mSelectedOnTouch;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDisallowTouch) {
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mSelectedOnTouch = mSelected;
                break;
        }

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:

                if (mIsHorizontal) {
                    if (Math.abs(event.getX() - mLastMoveX) < 0.1f) {
                        return true;
                    }
                    mMoveLength += event.getX() - mLastMoveX;
                } else {
                    if (Math.abs(event.getY() - mLastMoveY) < 0.1f) {
                        return true;
                    }
                    mMoveLength += event.getY() - mLastMoveY;
                }
                mLastMoveY = event.getY();
                mLastMoveX = event.getX();
                checkCirculation();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mLastMoveY = event.getY();
                mLastMoveX = event.getX();
                if (mMoveLength == 0) {
                    if (mSelectedOnTouch != mSelected) { //前后发生变化
                        notifySelected();
                    }
                } else {
                    moveToCenter(); // 滚动到中间位置
                }
                break;
        }
        return true;
    }

    private void computeScroll(int curr, int end, float rate) {
        if (rate < 1) {
            if (mIsHorizontal) {
                mMoveLength = mMoveLength + curr - mLastScrollX;
                mLastScrollX = curr;
            } else {
                mMoveLength = mMoveLength + curr - mLastScrollY;
                mLastScrollY = curr;
            }
            checkCirculation();
            invalidate();
        } else { // 滚动完毕
            mIsMovingCenter = false;
            mLastScrollY = 0;
            mLastScrollX = 0;

            if (mMoveLength > 0) {
                if (mMoveLength < mItemSize / 2) {
                    mMoveLength = 0;
                } else {
                    mMoveLength = mItemSize;
                }
            } else {
                if (-mMoveLength < mItemSize / 2) {
                    mMoveLength = 0;
                } else {
                    mMoveLength = -mItemSize;
                }
            }
            checkCirculation();
            notifySelected();
            invalidate();
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mIsHorizontal) {
                mMoveLength = mMoveLength + mScroller.getCurrX() - mLastScrollX;
            } else {
                mMoveLength = mMoveLength + mScroller.getCurrY() - mLastScrollY;
            }
            mLastScrollY = mScroller.getCurrY();
            mLastScrollX = mScroller.getCurrX();
            checkCirculation();
            invalidate();
        } else {
            if (mIsFling) {
                mIsFling = false;
                if (mMoveLength == 0) {
                    notifySelected();
                } else {
                    moveToCenter();
                }
            } else if (mIsMovingCenter) {
                notifySelected();
            }
        }
    }

    public void cancelScroll() {
        mLastScrollY = 0;
        mLastScrollX = 0;
        mIsFling = mIsMovingCenter = false;
        mScroller.abortAnimation();
        stopAutoScroll();
    }

    private void checkCirculation() {
        if (mMoveLength >= mItemSize) {
            int span = (int) (mMoveLength / mItemSize);
            mSelected -= span;
            if (mSelected < 0) {
                if (mIsCirculation) {
                    do {
                        mSelected = mData.size() + mSelected;
                    } while (mSelected < 0);
                    mMoveLength = (mMoveLength - mItemSize) % mItemSize;
                } else {
                    mSelected = 0;
                    mMoveLength = mItemSize;
                    if (mIsFling) {
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) {
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = (mMoveLength - mItemSize) % mItemSize;
            }

        } else if (mMoveLength <= -mItemSize) {
            int span = (int) (-mMoveLength / mItemSize);
            mSelected += span;
            if (mSelected >= mData.size()) {
                if (mIsCirculation) {
                    do {
                        mSelected = mSelected - mData.size();
                    } while (mSelected >= mData.size());
                    mMoveLength = (mMoveLength + mItemSize) % mItemSize;
                } else {
                    mSelected = mData.size() - 1;
                    mMoveLength = -mItemSize;
                    if (mIsFling) {
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) {
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = (mMoveLength + mItemSize) % mItemSize;
            }
        }
    }

    private void moveToCenter() {

        if (!mScroller.isFinished() || mIsFling || mMoveLength == 0) {
            return;
        }
        cancelScroll();

        if (mMoveLength > 0) {
            if (mIsHorizontal) {
                if (mMoveLength < mItemWidth / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, mItemWidth);
                }
            } else {
                if (mMoveLength < mItemHeight / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, mItemHeight);
                }
            }
        } else {
            if (mIsHorizontal) {
                if (-mMoveLength < mItemWidth / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, -mItemWidth);
                }
            } else {
                if (-mMoveLength < mItemHeight / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, -mItemHeight);
                }
            }
        }
    }

    private void scroll(float from, int to) {
        if (mIsHorizontal) {
            mLastScrollX = (int) from;
            mIsMovingCenter = true;
            mScroller.startScroll((int) from, 0, 0, 0);
            mScroller.setFinalX(to);
        } else {
            mLastScrollY = (int) from;
            mIsMovingCenter = true;
            mScroller.startScroll(0, (int) from, 0, 0);
            mScroller.setFinalY(to);
        }
        invalidate();
    }

    private void fling(float from, float vel) {
        if (mIsHorizontal) {
            mLastScrollX = (int) from;
            mIsFling = true;
            mScroller.fling((int) from, 0, (int) vel, 0, -10 * mItemWidth,
                    10 * mItemWidth, 0, 0);
        } else {
            mLastScrollY = (int) from;
            mIsFling = true;
            mScroller.fling(0, (int) from, 0, (int) vel, 0, 0, -10 * mItemHeight,
                    10 * mItemHeight);
        }
        invalidate();
    }

    private void notifySelected() {
        mMoveLength = 0;
        cancelScroll();
        if (mListener != null) {
            mListener.onSelected(ScrollPickerView.this, mSelected);
        }
    }

    private boolean mIsAutoScrolling = false;
    private ValueAnimator mAutoScrollAnimator;
    private final static SlotInterpolator sAutoScrollInterpolator = new SlotInterpolator();

    public void autoScrollFast(final int position, long duration, float speed, final Interpolator interpolator) {
        if (mIsAutoScrolling || !mIsCirculation) {
            return;
        }
        cancelScroll();
        mIsAutoScrolling = true;

        int length = (int) (speed * duration);
        int circle = (int) (length * 1f / (mData.size() * mItemSize) + 0.5f);
        circle = circle <= 0 ? 1 : circle;

        int aPlan = circle * (mData.size()) * mItemSize + (mSelected - position) * mItemSize;
        int bPlan = aPlan + (mData.size()) * mItemSize;
        final int end = Math.abs(length - aPlan) < Math.abs(length - bPlan) ? aPlan : bPlan;

        mAutoScrollAnimator.cancel();
        mAutoScrollAnimator.setIntValues(0, end);
        mAutoScrollAnimator.setInterpolator(interpolator);
        mAutoScrollAnimator.setDuration(duration);
        mAutoScrollAnimator.removeAllUpdateListeners();
        if (end != 0) {
            mAutoScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = 0;
                    rate = animation.getCurrentPlayTime() * 1f / animation.getDuration();
                    computeScroll((int) animation.getAnimatedValue(), end, rate);
                }
            });
            mAutoScrollAnimator.removeAllListeners();
            mAutoScrollAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsAutoScrolling = false;
                }
            });
            mAutoScrollAnimator.start();
        } else {
            computeScroll(end, end, 1);
            mIsAutoScrolling = false;
        }
    }

    public void autoScrollFast(final int position, long duration) {
        float speed = dip2px(0.6f);
        autoScrollFast(position, duration, speed, sAutoScrollInterpolator);
    }

    public void autoScrollFast(final int position, long duration, float speed) {
        autoScrollFast(position, duration, speed, sAutoScrollInterpolator);
    }

    public void autoScrollToPosition(int toPosition, long duration, final Interpolator interpolator) {
        toPosition = toPosition % mData.size();
        final int endY = (mSelected - toPosition) * mItemHeight;
        autoScrollTo(endY, duration, interpolator, false);
    }

    public void autoScrollTo(final int endY, long duration, final Interpolator interpolator, boolean canIntercept) {
        if (mIsAutoScrolling) {
            return;
        }
        final boolean temp = mDisallowTouch;
        mDisallowTouch = !canIntercept;
        mIsAutoScrolling = true;
        mAutoScrollAnimator.cancel();
        mAutoScrollAnimator.setIntValues(0, endY);
        mAutoScrollAnimator.setInterpolator(interpolator);
        mAutoScrollAnimator.setDuration(duration);
        mAutoScrollAnimator.removeAllUpdateListeners();
        mAutoScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rate = 0;
                rate = animation.getCurrentPlayTime() * 1f / animation.getDuration();
                computeScroll((int) animation.getAnimatedValue(), endY, rate);
            }
        });
        mAutoScrollAnimator.removeAllListeners();
        mAutoScrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAutoScrolling = false;
                mDisallowTouch = temp;
            }
        });
        mAutoScrollAnimator.start();
    }

    public void stopAutoScroll() {
        mIsAutoScrolling = false;
        mAutoScrollAnimator.cancel();
    }

    private static class SlotInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        }
    }

    private class FlingOnGestureListener extends SimpleOnGestureListener {

        private boolean mIsScrollingLastTime = false;

        public boolean onDown(MotionEvent e) {
            if (mDisallowInterceptTouch) {
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            mIsScrollingLastTime = isScrolling();
            cancelScroll();
            mLastMoveY = e.getY();
            mLastMoveX = e.getX();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               final float velocityY) {
            // 惯性滑动
            if (mIsInertiaScroll) {
                cancelScroll();
                if (mIsHorizontal) {
                    fling(mMoveLength, velocityX);
                } else {
                    fling(mMoveLength, velocityY);
                }
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mLastMoveY = e.getY();
            mLastMoveX = e.getX();
            float lastMove = 0;
            if (isHorizontal()) {
                mCenterPoint = mCenterX;
                lastMove = mLastMoveX;
            } else {
                mCenterPoint = mCenterY;
                lastMove = mLastMoveY;
            }
            if (mCanTap && !mIsScrollingLastTime) {
                if (lastMove >= mCenterPoint && lastMove <= mCenterPoint + mItemSize) {
                    performClick();
                } else if (lastMove < mCenterPoint) {
                    int move = mItemSize;
                    autoScrollTo(move, 150, sAutoScrollInterpolator, false);
                } else {
                    int move = -mItemSize;
                    autoScrollTo(move, 150, sAutoScrollInterpolator, false);
                }
            } else {
                moveToCenter();
            }
            return true;
        }
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<? extends T> data) {
        if (data == null) {
            mData = new ArrayList<T>();
        } else {
            this.mData = (List<T>) data;
        }
        mSelected = mData.size() / 2;
        invalidate();
    }


    public T getSelectedItem() {
        return mData.get(mSelected);
    }

    public int getSelectedPosition() {
        return mSelected;
    }

    public void setSelectedPosition(int position) {
        if (position < 0 || position > mData.size() - 1
                || (position == mSelected && mHasCallSelectedListener)) {
            return;
        }

        mHasCallSelectedListener = true;
        mSelected = position;
        invalidate();
        notifySelected();
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mListener = listener;
    }

    public OnSelectedListener getListener() {
        return mListener;
    }

    public boolean isInertiaScroll() {
        return mIsInertiaScroll;
    }

    public void setInertiaScroll(boolean inertiaScroll) {
        this.mIsInertiaScroll = inertiaScroll;
    }

    public boolean isIsCirculation() {
        return mIsCirculation;
    }

    public void setIsCirculation(boolean isCirculation) {
        this.mIsCirculation = isCirculation;
    }

    public boolean isDisallowInterceptTouch() {
        return mDisallowInterceptTouch;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public void setVisibleItemCount(int visibleItemCount) {
        mVisibleItemCount = visibleItemCount;
        reset();
        invalidate();
    }

    public void setDisallowInterceptTouch(boolean disallowInterceptTouch) {
        mDisallowInterceptTouch = disallowInterceptTouch;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public int getItemWidth() {
        return mItemWidth;
    }

    public int getItemSize() {
        return mItemSize;
    }

    public int getCenterX() {
        return mCenterX;
    }

    public int getCenterY() {
        return mCenterY;
    }

    public int getCenterPoint() {
        return mCenterPoint;
    }

    public boolean isDisallowTouch() {
        return mDisallowTouch;
    }

    public void setDisallowTouch(boolean disallowTouch) {
        mDisallowTouch = disallowTouch;
    }

    public void setCenterPosition(int centerPosition) {
        if (centerPosition < 0) {
            mCenterPosition = 0;
        } else if (centerPosition >= mVisibleItemCount) {
            mCenterPosition = mVisibleItemCount - 1;
        } else {
            mCenterPosition = centerPosition;
        }
        mCenterY = mCenterPosition * mItemHeight;
        invalidate();
    }

    public int getCenterPosition() {
        return mCenterPosition;
    }

    public void setCenterItemBackground(Drawable centerItemBackground) {
        mCenterItemBackground = centerItemBackground;
        mCenterItemBackground.setBounds(mCenterX, mCenterY, mCenterX + mItemWidth, mCenterY + mItemHeight);
        invalidate();
    }

    public void setCenterItemBackground(int centerItemBackgroundColor) {
        mCenterItemBackground = new ColorDrawable(centerItemBackgroundColor);
        mCenterItemBackground.setBounds(mCenterX, mCenterY, mCenterX + mItemWidth, mCenterY + mItemHeight);
        invalidate();
    }

    public Drawable getCenterItemBackground() {
        return mCenterItemBackground;
    }

    public boolean isScrolling() {
        return mIsFling || mIsMovingCenter || mIsAutoScrolling;
    }

    public boolean isFling() {
        return mIsFling;
    }

    public boolean isMovingCenter() {
        return mIsMovingCenter;
    }

    public boolean isAutoScrolling() {
        return mIsAutoScrolling;
    }

    public boolean isCanTap() {
        return mCanTap;
    }

    public void setCanTap(boolean canTap) {
        mCanTap = canTap;
    }

    public boolean isHorizontal() {
        return mIsHorizontal;
    }

    public boolean isVertical() {
        return !mIsHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        if (mIsHorizontal == horizontal) {
            return;
        }
        mIsHorizontal = horizontal;
        reset();
        if (mIsHorizontal) {
            mItemSize = mItemWidth;
        } else {
            mItemSize = mItemHeight;
        }
        invalidate();
    }

    public void setVertical(boolean vertical) {
        if (mIsHorizontal == !vertical) {
            return;
        }
        mIsHorizontal = !vertical;
        reset();
        if (mIsHorizontal) {
            mItemSize = mItemWidth;
        } else {
            mItemSize = mItemHeight;
        }
        invalidate();
    }

    public boolean isDrawAllItem() {
        return mDrawAllItem;
    }

    public void setDrawAllItem(boolean drawAllItem) {
        mDrawAllItem = drawAllItem;
    }

    public interface OnSelectedListener {
        void onSelected(ScrollPickerView scrollPickerView, int position);
    }

    public int dip2px(float dipVlue) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float sDensity = metrics.density;
        return (int) (dipVlue * sDensity + 0.5F);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            moveToCenter();
        }
    }

}
