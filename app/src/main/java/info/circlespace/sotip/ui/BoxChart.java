/*
 * Copyright (C) 2016 Dylan Chu
 */
package info.circlespace.sotip.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import info.circlespace.sotip.R;
import info.circlespace.sotip.SotipApp;

/**
 * Custom view that shows a rectangular chart-like representation of the performance of a group of IT projects.
 *
 * There are two rows in the chart.  The first row has 3 boxes and the second row has 2 boxes.
 */
public class BoxChart extends View {
    // the index of the first box in the second row.
    public static final int SECOND_ROW = 3;

    private List<DataItem> mData = new ArrayList<DataItem>();

    private float mTotal = 0.0f;
    private int mWidth = 0;
    private int mHeight = 0;

    private int mTextColor;
    private float mTextSize = 0.0f;
    private Paint mBoxPaint;
    private Paint mTextPaint;

    private OnItemSelectedListener mListener = null;


    /**
     * Interface definition for a callback to be invoked when the current item changes.
     */
    public interface OnItemSelectedListener {
        public void onItemSelected(int itemNdx);
    }


    /**
     * Register a callback to be invoked when the currently selected item changes.
     */
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        mListener = listener;
    }


    public BoxChart(Context context) {
        super(context);
        init();
    }


    public BoxChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BoxChart,
                0, 0
        );

        try {
            mTextColor = a.getColor(R.styleable.BoxChart_labelColour, 0xff000000);
            mTextSize = a.getDimension(R.styleable.BoxChart_labelSize, 24.0f);
        } finally {
            a.recycle();
        }

        init();
    }


    /**
     * Initialize the control. This code is in a separate method so that it can be
     * called from both constructors.
     */
    private void init() {

        // Set up the paint for the labels
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Set up the paint for the boxes
        mBoxPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoxPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = getMeasuredWidth();

        // set the height to be 2/3 of the width (a number chosen arbitrarily)
        int h = (int) (2f/3 * w);

        mWidth = w;
        mHeight = h;
        setMeasuredDimension(w, h);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mData.size(); i++) {
            DataItem di = mData.get(i);
            mBoxPaint.setColor(di.mColor);
            // draw the box
            canvas.drawRect(di.mStartX, di.mStartY, di.mEndX, di.mEndY, mBoxPaint);
            // draw the label
            canvas.drawText(di.mLabel, (di.mStartX + di.mEndX) / 2, (di.mStartY + di.mEndY) / 2, mTextPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent evt) {

        // check that the user stopped pressing
        if (evt.getAction() == MotionEvent.ACTION_UP) {
            float x = evt.getX();
            float y = evt.getY();

            for (int i = 0; i < SotipApp.NUM_PERF_CATEGS; i++) {
                DataItem di = mData.get(i);

                // determine which box was 'pressed'
                if (x >= di.mStartX && x <= di.mEndX) {
                    if (y >= di.mStartY && y <= di.mEndY) {
                        if (mListener != null) {
                            mListener.onItemSelected(di.mNdx);
                            break;
                        }
                    }
                }
            }
        }

        return true;
    }


    private class DataItem {
        public int mNdx;
        public int mValue;
        public String mLabel;
        public int mColor;

        // the corners of the box
        public float mStartX;
        public float mEndX;
        public float mStartY;
        public float mEndY;
    }


    public void addItem(int ndx, int value, String label, int color) {
        DataItem itm = new DataItem();
        itm.mNdx = ndx;
        itm.mValue = value;
        itm.mLabel = label;
        itm.mColor = color;

        mTotal += value;
        mData.add(itm);
    }


    public void setItems(List<DataItem> data) {
        clearItems();

        for (DataItem item : data) {
            mData.add(item);
        }

        onDataChanged();
    }


    public void clearItems() {
        mData.clear();
        mTotal = 0.0f;
    }


    public void onDataChanged() {
        calcBoxSizes();
        invalidate();
    }


    /**
     * Calculates the size of each box in the box chart.
     */
    private void calcBoxSizes() {
        if (mData.size() != SotipApp.NUM_PERF_CATEGS)
            return;

        float thirdWidth = mWidth / 3;
        float halfWidth = mWidth / 2;
        float halfHeight = mHeight / 2;

        float startX = 0;
        float startY = 0;
        float endX = 0;
        float endY = halfHeight;

        for (int i = 0; i < mData.size(); i++) {
            DataItem di = mData.get(i);

            if (i == SECOND_ROW) {
                startX = 0;
                startY = halfHeight;
                endY += halfHeight;
            }

            if (i < SECOND_ROW) {
                endX = startX + thirdWidth;
            } else {
                endX = startX + halfWidth;
            }

            di.mStartX = startX;
            di.mStartY = startY;
            di.mEndX = endX;
            di.mEndY = endY;

            startX = endX;
        }
    }
}
