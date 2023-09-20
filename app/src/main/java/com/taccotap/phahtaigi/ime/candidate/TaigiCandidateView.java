package com.taccotap.phahtaigi.ime.candidate;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;

import com.taccotap.phahtaigi.AppPrefs;
import com.taccotap.phahtaigi.BuildConfig;
import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.imedict.ImeDictModel;
import com.taccotap.phahtaigi.utils.StoppableRunnable;

import java.util.ArrayList;

public class TaigiCandidateView extends View {
    private static final String TAG = TaigiCandidateView.class.getSimpleName();

//    private static final String ACTION_SEARCH_FROM_PHAHTAIGI = "com.taccotap.taigidict.search.from.phahtaigi";
//    private static final String EXTRA_TAILO_SEARCH_KEYWORD = "EXTRA_TAILO_SEARCH_KEYWORD";
//    private static final String EXTRA_TAILO_HANJI_SEARCH_KEYWORD = "EXTRA_TAILO_HANJI_SEARCH_KEYWORD";

    private static final int OUT_OF_BOUNDS = -1;

    private static final int SCROLL_PIXELS = 20;
    private static final int X_GAP = 20;
    private static final int Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION = 0;
    private static final int MIN_WORD_WIDTH = 120;

    private static final float Y_RAW_INPUT_HEIGHT_DIFF_MULTIPLY = 1.5f;
    private static final float Y_MAIN_SUGGESTION_HEIGHT_DIFF_MULTIPLY = 0.8f;

    private static int Y_MAIN_SUGGESTION_HEIGHT_DIFF;

    private Context mContext;

    private Vibrator mVibrator;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TaigiIme mService;
    private ArrayList<ImeDictModel> mSuggestions = new ArrayList<>();

    private boolean mIsMainCandidateLomaji = true;

    private int mSelectedIndex;
    private int mTouchX = OUT_OF_BOUNDS;
    private Drawable mSelectionHighlightDrawable;

    private Rect mPadding = new Rect(10, 5, 10, 5);
    private Rect mBgPadding;

    private int mMeasuredWidth;

    private int mColorNormal;
    private int mColorRecommended;
    private int mVerticalPadding;

    private Paint mSuggestionsMainPaint;
    private Paint mSuggestionsMainFirstLomajiPaint;
    private Paint mSuggestionsHintPaint;
    private Paint mWordSeperatorLinePaint;

    private float mSuggestionsMainTextHeight;
    private float mSuggestionsHintTextHeight;
    private float mMainSuggestionHeightForPaint;
    private float mMainSuggestionFirstLomajiHeightForPaint;
    private float mHintSuggestionHeightForPaint;

    private boolean mScrolled;
    private int mTargetScrollX;
    private int mTotalWidth;

    private StoppableRunnable mLongTouchTask = new StoppableRunnable() {
        @Override
        public void stoppableRun() {
            onLongTouched();
        }
    };

    private GestureDetectorCompat mGestureDetector;

    private int mDesiredHeight;
    private Typeface mLomajiTypeface;
    private Typeface mHanjiTypeface;
    private int mCurrentInputLomajiMode;
    private boolean mIsVibration;

    public TaigiCandidateView(Context context) {
        super(context);
        init(context);
    }

    public TaigiCandidateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaigiCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TaigiCandidateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setVibrator(Vibrator mVibrator) {
        this.mVibrator = mVibrator;
    }

    private void init(Context context) {
        mContext = context;

        final Resources resources = mContext.getResources();

        Y_MAIN_SUGGESTION_HEIGHT_DIFF = -(int) (resources.getDimensionPixelSize(R.dimen.candidate_main_font_lomaji_height) * Y_MAIN_SUGGESTION_HEIGHT_DIFF_MULTIPLY);

        mSelectionHighlightDrawable = resources.getDrawable(android.R.drawable.list_selector_background);

        mSelectionHighlightDrawable.setState(new int[]{
                android.R.attr.state_enabled,
                android.R.attr.state_focused,
                android.R.attr.state_window_focused,
                android.R.attr.state_pressed
        });
        // Get the desired height of the icon menu view (last row of items does
        // not have a divider below)
        mSelectionHighlightDrawable.getPadding(mPadding);

        mVerticalPadding = resources.getDimensionPixelSize(R.dimen.candidate_vertical_padding);

        setBackgroundColor(resources.getColor(R.color.candidate_suggestions_background));

        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                if (mTotalWidth < mMeasuredWidth) {
                    return false;
                }

                stopTriggeredLongTouchEvent();

                mScrolled = true;
                int sx = getScrollX();
                sx += distanceX;
                if (sx < 0) {
                    sx = 0;
                }
                if (sx + getWidth() > mTotalWidth) {
                    sx -= distanceX;
                }
                mTargetScrollX = sx;
                scrollTo(sx, getScrollY());
                invalidate();
                return true;
            }
        });
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);

        initTextPaintAndTextHeightCalculation();
    }

    public void setIsVibration(boolean isVibration) {
        mIsVibration = isVibration;
    }

    public void resetTextSettings() {
        initTextPaintAndTextHeightCalculation();
    }

    private void initTextPaintAndTextHeightCalculation() {
        final Resources resources = mContext.getResources();

        mColorNormal = resources.getColor(R.color.candidate_normal);
        mColorRecommended = resources.getColor(R.color.candidate_recommended);

        mLomajiTypeface = ResourcesCompat.getFont(mContext, R.font.fontfamily_genyomin_m);
        mHanjiTypeface = ResourcesCompat.getFont(mContext, R.font.fontfamily_genyomin_m);

        mSuggestionsMainFirstLomajiPaint = new Paint();
        mSuggestionsMainFirstLomajiPaint.setColor(mColorRecommended);
        mSuggestionsMainFirstLomajiPaint.setAntiAlias(true);
        mSuggestionsMainFirstLomajiPaint.setTextSize(resources.getDimensionPixelSize(R.dimen.candidate_main_font_lomaji_height));
        mSuggestionsMainFirstLomajiPaint.setStrokeWidth(0);
        mSuggestionsMainFirstLomajiPaint.setTypeface(mLomajiTypeface);
        final Paint.FontMetrics suggestionsMainFirstLomajiPaintFontMetrics = mSuggestionsMainFirstLomajiPaint.getFontMetrics();
        mMainSuggestionFirstLomajiHeightForPaint = -suggestionsMainFirstLomajiPaintFontMetrics.top + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION + Y_MAIN_SUGGESTION_HEIGHT_DIFF;

        mWordSeperatorLinePaint = new Paint();
        mWordSeperatorLinePaint.setColor(mColorNormal);
        mWordSeperatorLinePaint.setAntiAlias(true);
        mWordSeperatorLinePaint.setStrokeWidth(0);

        updateTextPaintAndTextHeightCalculation();
    }

    private void updateTextPaintAndTextHeightCalculation() {
        mSuggestionsMainPaint = new Paint();
        mSuggestionsMainPaint.setColor(mColorRecommended);
        mSuggestionsMainPaint.setAntiAlias(true);
        mSuggestionsMainPaint.setStrokeWidth(0);
        if (mIsMainCandidateLomaji) {
            mSuggestionsMainPaint.setTypeface(mLomajiTypeface);
            mSuggestionsMainPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.candidate_main_font_lomaji_height));
        } else {
            mSuggestionsMainPaint.setTypeface(mHanjiTypeface);
            mSuggestionsMainPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.candidate_main_font_hanji_height));
        }
        final Paint.FontMetrics suggestionsMainPaintFontMetrics = mSuggestionsMainPaint.getFontMetrics();
        mSuggestionsMainTextHeight = suggestionsMainPaintFontMetrics.bottom - suggestionsMainPaintFontMetrics.top + suggestionsMainPaintFontMetrics.leading + Y_MAIN_SUGGESTION_HEIGHT_DIFF;

        mMainSuggestionHeightForPaint = -suggestionsMainPaintFontMetrics.top + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION + suggestionsMainPaintFontMetrics.leading + Y_MAIN_SUGGESTION_HEIGHT_DIFF;

        mSuggestionsHintPaint = new Paint();
        mSuggestionsHintPaint.setColor(mColorRecommended);
        mSuggestionsHintPaint.setAntiAlias(true);
        mSuggestionsHintPaint.setStrokeWidth(0);
        if (mIsMainCandidateLomaji) {
            mSuggestionsHintPaint.setTypeface(mHanjiTypeface);
            mSuggestionsHintPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.candidate_hint_font_hanji_height));
        } else {
            mSuggestionsHintPaint.setTypeface(mLomajiTypeface);
            mSuggestionsHintPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.candidate_hint_font_lomaji_height));
        }
        final Paint.FontMetrics suggestionsHintPaintFontMetrics = mSuggestionsHintPaint.getFontMetrics();
        mSuggestionsHintTextHeight = suggestionsHintPaintFontMetrics.bottom - suggestionsHintPaintFontMetrics.top + suggestionsHintPaintFontMetrics.leading + Y_MAIN_SUGGESTION_HEIGHT_DIFF;

        mHintSuggestionHeightForPaint = mSuggestionsMainTextHeight
                - suggestionsHintPaintFontMetrics.top + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION + suggestionsHintPaintFontMetrics.leading + Y_MAIN_SUGGESTION_HEIGHT_DIFF;

        mDesiredHeight = (int) (mSuggestionsMainTextHeight
                + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION
                + mSuggestionsHintTextHeight
                + mVerticalPadding + mPadding.top + mPadding.bottom);

        invalidate();
    }

    public void setIsMainCandidateLomaji(boolean isMainCandidateLomaji) {
        mIsMainCandidateLomaji = isMainCandidateLomaji;

        updateTextPaintAndTextHeightCalculation();
    }

    public void setService(TaigiIme listener) {
        mService = listener;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return mTotalWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasuredWidth = resolveSize(50, widthMeasureSpec);

        // Maximum possible width and desired height
        int measuredHeight = resolveSize(mDesiredHeight, heightMeasureSpec);
        setMeasuredDimension(mMeasuredWidth, measuredHeight);
    }

    /**
     * If the canvas is null, then only touch calculations are performed to pick the target
     * candidate.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas != null) {
            super.onDraw(canvas);
        }

        drawSuggestions(canvas);
    }

    private void drawSuggestions(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        mTotalWidth = 0;

        final int count = mSuggestions.size();
        if (count == 0) {
            return;
        }

        if (mBgPadding == null) {
            mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(mBgPadding);
            }
        }

        int x = 0;
        final int touchX = mTouchX;
        final int scrollX = getScrollX();
        final boolean scrolled = mScrolled;

//        for (int i = 0; i < count; i++) {
//            ImeDict imeDict = mSuggestions.get(i);
//            Log.e(TAG, "hanji=" + imeDict.getHanji() + ", tailo=" + imeDict.getTailo() + ", poj=" + imeDict.getPoj());
//        }

        for (int i = 0; i < count; i++) {
            ImeDictModel imeDictModel = mSuggestions.get(i);

            String mainCandidate = "";
            String hintCandidate = "";

            if (i == 0) {
                if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                    mainCandidate = imeDictModel.getKip();
                } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                    mainCandidate = imeDictModel.getPoj();
                }
                hintCandidate = imeDictModel.getHanji();
            } else {
                if (mIsMainCandidateLomaji || (!mIsMainCandidateLomaji && imeDictModel.getSrcDict() > 1)) {
                    if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                        mainCandidate = imeDictModel.getKip();
                    } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                        mainCandidate = imeDictModel.getPoj();
                    }
                    hintCandidate = imeDictModel.getHanji();
                } else {
                    mainCandidate = imeDictModel.getHanji();
                    if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                        hintCandidate = imeDictModel.getKip();
                    } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                        hintCandidate = imeDictModel.getPoj();
                    }
                }
            }

//            if (BuildConfig.DEBUG_LOG) {
//                Log.d(TAG, "mainCandidate=" + mainCandidate + ", hintCandidate=" + hintCandidate);
//            }

            final float mainTextWidth = mSuggestionsMainPaint.measureText(mainCandidate);
            final float hintTextWidth = mSuggestionsHintPaint.measureText(hintCandidate);
            final int mainWordWidth = (int) mainTextWidth;
            final int hintWordWidth = (int) hintTextWidth;
            final int wordWidth = (mainWordWidth > hintWordWidth ? mainWordWidth : hintWordWidth);
            int fullWordWidth = wordWidth + X_GAP * 2;
            if (fullWordWidth < MIN_WORD_WIDTH) {
                fullWordWidth = MIN_WORD_WIDTH;
            }

            // draw touched effect
            if (touchX + scrollX >= x && touchX + scrollX < x + fullWordWidth && !scrolled) {
                if (mTouchX != OUT_OF_BOUNDS) {
                    canvas.translate(x, 0);
                    mSelectionHighlightDrawable.setBounds(0, 0,
                            fullWordWidth, mDesiredHeight);
                    mSelectionHighlightDrawable.draw(canvas);
                    canvas.translate(-x, 0);
                }
                mSelectedIndex = i;
            }

            if (i == 0) {
                // draw parsed raw input
                canvas.drawText(mainCandidate, x + X_GAP, mMainSuggestionFirstLomajiHeightForPaint, mSuggestionsMainFirstLomajiPaint);

                // draw line between words
                String lomaji = "";
                if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                    lomaji = imeDictModel.getKip();
                } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                    lomaji = imeDictModel.getPoj();
                }
                int fullFirstLomajiWidth = (int) (mSuggestionsMainFirstLomajiPaint.measureText(lomaji) + X_GAP * 2);
                if (fullFirstLomajiWidth < MIN_WORD_WIDTH) {
                    fullFirstLomajiWidth = MIN_WORD_WIDTH;
                }
                canvas.drawLine(x + fullFirstLomajiWidth, 0,
                        x + fullFirstLomajiWidth, (float) mDesiredHeight, mWordSeperatorLinePaint);

                x += fullFirstLomajiWidth;
            } else {
                // draw main candidate
                canvas.drawText(mainCandidate, x + X_GAP, mMainSuggestionHeightForPaint, mSuggestionsMainPaint);

                // draw hint candidate
                canvas.drawText(hintCandidate, x + X_GAP, mHintSuggestionHeightForPaint, mSuggestionsHintPaint);

                // draw line between words
                canvas.drawLine(x + fullWordWidth, 0,
                        x + fullWordWidth, (float) mDesiredHeight, mWordSeperatorLinePaint);

                x += fullWordWidth;
            }
        }

        mTotalWidth = x;

        if (mTargetScrollX != getScrollX()) {
            scrollToTarget();
        }
    }

    private void scrollToTarget() {
        int sx = getScrollX();
        if (mTargetScrollX > sx) {
            sx += SCROLL_PIXELS;
            if (sx >= mTargetScrollX) {
                sx = mTargetScrollX;
                requestLayout();
            }
        } else {
            sx -= SCROLL_PIXELS;
            if (sx <= mTargetScrollX) {
                sx = mTargetScrollX;
                requestLayout();
            }
        }

        scrollTo(sx, getScrollY());

        invalidate();
    }

    protected void setSuggestions(ArrayList<ImeDictModel> suggestions, int currentInputLomajiMode) {
        mCurrentInputLomajiMode = currentInputLomajiMode;

        mSuggestions.clear();
        if (suggestions != null && suggestions.size() > 0) {
            mSuggestions.addAll(suggestions);
        }

        scrollTo(0, 0);
        mTargetScrollX = 0;
        mTouchX = OUT_OF_BOUNDS;
        mSelectedIndex = -1;

        invalidate();
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (mGestureDetector.onTouchEvent(me)) {
            return true;
        }

        int action = me.getAction();
        int x = (int) me.getX();
        int y = (int) me.getY();

        mTouchX = (int) me.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrolled = false;
                invalidate();

                triggerLongTouchEvent();

                break;
//            case MotionEvent.ACTION_MOVE:
//                if (y <= 0) {
//                    // Fling up!?
//                    if (mSelectedIndex >= 0) {
//                        mService.pickSuggestionManually(mSelectedIndex);
//                        mSelectedIndex = -1;
//                    }
//                }
//                invalidate();
//                break;
            case MotionEvent.ACTION_UP:
                stopTriggeredLongTouchEvent();

                if (!mScrolled) {
                    if (mSelectedIndex >= 0) {
                        if (mIsVibration) {
                            mVibrator.vibrate(TaigiIme.KEY_VIBRATION_MILLISECONDS);
                        }

                        final ImeDictModel imeDictModel = mSuggestions.get(mSelectedIndex);
                        if (mSelectedIndex == 0 || mIsMainCandidateLomaji) {
                            if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                                mService.commitPickedSuggestion(imeDictModel.getKip());

                                if (BuildConfig.DEBUG_LOG) {
                                    Log.d(TAG, "Selected output: " + imeDictModel.getKip());
                                }
                            } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                                mService.commitPickedSuggestion(imeDictModel.getPoj());

                                if (BuildConfig.DEBUG_LOG) {
                                    Log.d(TAG, "Selected output: " + imeDictModel.getPoj());
                                }
                            }
                        } else {
                            if (imeDictModel.getSrcDict() > 1) {
                                if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_KIP) {
                                    mService.commitPickedSuggestion(imeDictModel.getKip());

                                    if (BuildConfig.DEBUG_LOG) {
                                        Log.d(TAG, "Selected output: " + imeDictModel.getKip());
                                    }
                                } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                                    mService.commitPickedSuggestion(imeDictModel.getPoj());

                                    if (BuildConfig.DEBUG_LOG) {
                                        Log.d(TAG, "Selected output: " + imeDictModel.getPoj());
                                    }
                                }
                            } else {
                                mService.commitPickedSuggestion(imeDictModel.getHanji());
                            }

                            if (BuildConfig.DEBUG_LOG) {
                                Log.d(TAG, "Selected output: " + imeDictModel.getHanji());
                                int count = imeDictModel.getHanji().length();
                                for (int i = 0; i < count; i++) {
                                    final char charAt = imeDictModel.getHanji().charAt(i);
                                    Log.d(TAG, "Selected hanji code: " + String.format("\\u%04x", (int) charAt));
                                }
                            }
                        }
                    }
                }
                mSelectedIndex = -1;
                removeHighlight();
                requestLayout();
                break;
        }
        return true;
    }

    private void triggerLongTouchEvent() {
        stopTriggeredLongTouchEvent();
        mHandler.postDelayed(mLongTouchTask, 600);
    }

    private void stopTriggeredLongTouchEvent() {
        mLongTouchTask.stop();
        mHandler.removeCallbacks(mLongTouchTask);
    }

    private void onLongTouched() {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "onLongTouched()");
        }

        // TODO: go ChhoeTaigi website instead: check TaigiDict install
//        PackageManager pm = mContext.getPackageManager();
//        boolean isInstalled = isPackageInstalled("com.taccotap.taigidict", pm);
//        if (!isInstalled) {
//            Toast.makeText(mContext, "若欲 chhōe 字詞 ài 先安裝辭典 ê APP。", Toast.LENGTH_LONG).show();
//
//            String appPackageName = "com.taccotap.taigidict";
//            try {
//                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//            } catch (android.content.ActivityNotFoundException anfe) {
//                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//            }
//
//            return;
//        }
//
//        final ImeDictModel imeDictModel = mSuggestions.get(mSelectedIndex);
//
//        final Intent intent = new Intent(ACTION_SEARCH_FROM_PHAHTAIGI);
//        if (mSelectedIndex == 0 || mIsMainCandidateLomaji) {
//            intent.putExtra(EXTRA_TAILO_SEARCH_KEYWORD, imeDictModel.getKip());
//        } else {
//            intent.putExtra(EXTRA_TAILO_HANJI_SEARCH_KEYWORD, imeDictModel.getHanji());
//        }
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

//    /**
//     * For flick through from keyboard, call this method with the x coordinate of the flick
//     * gesture.
//     *
//     * @param x
//     */
//    public void takeSuggestionAt(float x) {
//        mTouchX = (int) x;
//        // To detect candidate
//        if (mSelectedIndex >= 0) {
//            mService.pickSuggestionManually(mSelectedIndex);
//        }
//        invalidate();
//    }

    private void removeHighlight() {
        mTouchX = OUT_OF_BOUNDS;
        invalidate();
    }
}