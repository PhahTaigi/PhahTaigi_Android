package com.taccotap.phahtaigi.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.taccotap.phahtaigi.R;

public class BaselineFixedTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final float LEADING_SHIFT_IN_DP = 5.0f;
    private static final float LAST_LINE_DESCENT_SHIFT_IN_DP = 10.5f;

    public static final int DEFAULT_BEHAVIOR = 0;
    private boolean mRequiresAdjustment = false;
    private int mLeading;
    private int mFirstLineLeading;
    private int mLastLineDescent;

    public BaselineFixedTextView(Context context) {
        super(context);
        mLeading = DEFAULT_BEHAVIOR;
        mFirstLineLeading = DEFAULT_BEHAVIOR;
        mLastLineDescent = DEFAULT_BEHAVIOR;

        commonInit(context, null,0,0);
    }

    public BaselineFixedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonInit(context, attrs, 0, 0);
    }

    public BaselineFixedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit(context, attrs, defStyleAttr, 0);
    }

    private void commonInit(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray array = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.BaselineFixedTextView, defStyleAttr, defStyleRes);

        try {
            mLeading = (int) (getTextSize() + getResources().getDisplayMetrics().scaledDensity * LEADING_SHIFT_IN_DP);
            mLastLineDescent = (int) (getResources().getDisplayMetrics().scaledDensity * LAST_LINE_DESCENT_SHIFT_IN_DP);

            mLeading = array.getDimensionPixelSize(R.styleable.BaselineFixedTextView_leading, mLeading);
            mFirstLineLeading = array.getDimensionPixelSize(R.styleable.BaselineFixedTextView_firstLineLeading, mLeading);
            mLastLineDescent = array.getDimensionPixelSize(R.styleable.BaselineFixedTextView_lastLineDescent, mLastLineDescent);
        } finally {
            array.recycle();
        }

        if (mFirstLineLeading != DEFAULT_BEHAVIOR || mLastLineDescent != DEFAULT_BEHAVIOR) {
            setIncludeFontPadding(false);
            mRequiresAdjustment = true;
        } else if (mLeading != DEFAULT_BEHAVIOR) {
            mRequiresAdjustment = true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // todo: don't call this on every measurement pass - only once on init and when text metrics (size etc) is changed
        // (although that would require overriding a ton of methods)
        if (mRequiresAdjustment) {
            adjustMetrics();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getLeading() {
        return mLeading;
    }

    public void setLeading(int leading) {
        if (mLeading == leading) {
            return;
        }

        mLeading = leading;
        refreshRequireAdjustment();
        if (mRequiresAdjustment) {
            adjustMetrics();
        }
        invalidate();
    }

    public int getFirstLineLeading() {
        return mFirstLineLeading;
    }

    public void setFirstLineLeading(int firstLineLeading) {
        if (mFirstLineLeading == firstLineLeading) {
            return;
        }

        mFirstLineLeading = firstLineLeading;
        refreshRequireAdjustment();
        if (mRequiresAdjustment) {
            adjustMetrics();
        }
        invalidate();
    }

    public int getLastLineDescent() {
        return mLastLineDescent;
    }

    public void setLastLineDescent(int lastLineDescent) {
        if (mLastLineDescent == lastLineDescent) {
            return;
        }

        mLastLineDescent = lastLineDescent;
        refreshRequireAdjustment();
        if (mRequiresAdjustment) {
            adjustMetrics();
        }
        invalidate();
    }

    private void refreshRequireAdjustment() {
        mRequiresAdjustment = mLeading != DEFAULT_BEHAVIOR
                || mLastLineDescent != DEFAULT_BEHAVIOR
                || mFirstLineLeading != DEFAULT_BEHAVIOR;
    }

    private void adjustMetrics() {
        Paint.FontMetricsInt metrics = getPaint().getFontMetricsInt();

        // Required extra for interline leading.
        // Own height is measured from ascent to descent (ascent < descent)
        if (mLeading != DEFAULT_BEHAVIOR) {
            final int extra = mLeading - (metrics.descent - metrics.ascent);
            setLineSpacing(extra, 1);
        }

        // Required top padding to set first line leading
        // With setIncludeFontPadding=false, the first baseline is placed -{ascent} pixels from the top
        final int paddingTop;
        if (mFirstLineLeading != DEFAULT_BEHAVIOR) {
            paddingTop = mFirstLineLeading + metrics.ascent;
        } else {
            paddingTop = getPaddingTop();
        }

        // Required bottom padding
        // With setIncludeFontPadding=false, the view's bottom os placed {descent} pixels from the bottom
        setIncludeFontPadding(false);
        final int paddingBottom;
        if (mLastLineDescent != DEFAULT_BEHAVIOR) {
            paddingBottom = mLastLineDescent - metrics.descent;
        } else {
            paddingBottom = getPaddingBottom();
        }

        setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), paddingBottom);
    }
}