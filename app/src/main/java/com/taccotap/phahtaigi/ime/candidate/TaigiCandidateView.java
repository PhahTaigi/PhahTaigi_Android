package com.taccotap.phahtaigi.ime.candidate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;
import com.taccotap.phahtaigi.AppPrefs;
import com.taccotap.phahtaigi.BuildConfig;
import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.dictmodel.ImeDict;
import com.taccotap.phahtaigi.ime.TaigiIme;
import com.taccotap.phahtaigi.utils.StoppableRunnable;

import java.util.ArrayList;

import static com.taccotap.phahtaigi.AppPrefs.HANJI_FONT_TYPE_APP_DEFAULT;

@SuppressLint("ViewConstructor")
public class TaigiCandidateView extends View {
    private static final String TAG = TaigiCandidateView.class.getSimpleName();

    private static final String ACTION_SEARCH_FROM_PHAHTAIGI = "com.taccotap.taigidict.search.from.phahtaigi";
    private static final String EXTRA_TAILO_SEARCH_KEYWORD = "EXTRA_TAILO_SEARCH_KEYWORD";
    private static final String EXTRA_TAILO_HANJI_SEARCH_KEYWORD = "EXTRA_TAILO_HANJI_SEARCH_KEYWORD";

    private static final int OUT_OF_BOUNDS = -1;

    private static final int SCROLL_PIXELS = 20;
    private static final int X_GAP = 20;
    private static final int Y_GAP_BETWEEN_RAW_INPUT_AND_SUGGESTIONS = 5;
    private static final int Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION = 2;

    private final Context mContext;
    private final Vibrator mVibrator;
    private final Handler mHandler;

    private TaigiIme mService;
    private ArrayList<ImeDict> mSuggestions = new ArrayList<>();

    private boolean mIsMainCandidateLomaji = true;

    private int mSelectedIndex;
    private int mTouchX = OUT_OF_BOUNDS;
    private Drawable mSelectionHighlightDrawable;

    private Rect mPadding = new Rect(10, 10, 10, 10);
    private Rect mBgPadding;

    private int mMeasuredWidth;

    private int mColorNormal;
    private int mColorRecommended;
    private int mVerticalPadding;

    private Paint mRawInputPaint;
    private Paint mSuggestionsMainPaint;
    private Paint mSuggestionsMainFirstLomajiPaint;
    private Paint mSuggestionsHintPaint;
    private Paint mWordSeperatorLinePaint;

    private float mRawInputPaintHeight;
    private float mSuggestionsMainTextHeight;
    private float mSuggestionsHintTextHeight;
    private float mMainSuggestionHeightForPaint;
    private float mMainSuggestionFirstLomajiHeightForPaint;
    private float mHintSuggestionHeightForPaint;
    private float mWordSeperatorLineYForPaint;

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
    private String mRawInput;
    private int mDesiredHeight;
    private Typeface mLomajiTypeface;
    private Typeface mHanjiTypeface;
    private int mCurrentInputLomajiMode;

    public TaigiCandidateView(Context context, Vibrator vibrator, android.os.Handler handler) {
        super(context);
        mContext = context;
        mVibrator = vibrator;
        mHandler = handler;
        init();
    }

    private void init() {
        final Resources resources = mContext.getResources();

        //noinspection deprecation
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

        //noinspection deprecation
        setBackgroundColor(resources.getColor(R.color.candidate_background));

        mGestureDetector = new GestureDetectorCompat(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                    float distanceX, float distanceY) {
                if (mTotalWidth < mMeasuredWidth) {
                    return false;
                }

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

    public void resetTextSettings() {
        initTextPaintAndTextHeightCalculation();
    }

    private void initTextPaintAndTextHeightCalculation() {
        final Resources resources = mContext.getResources();

        //noinspection deprecation
        mColorNormal = resources.getColor(R.color.candidate_normal);
        //noinspection deprecation
        mColorRecommended = resources.getColor(R.color.candidate_recommended);

        mLomajiTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/twu3.ttf");

        final int hanjiType = Prefs.getInt(AppPrefs.PREFS_KEY_HANJI_FONT_TYPE, HANJI_FONT_TYPE_APP_DEFAULT);
        if (hanjiType == AppPrefs.HANJI_FONT_TYPE_MINGLIUB) {
            mHanjiTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/mingliub.ttc");
            Log.i(TAG, "Hanji font: mingliub");
        } else if (hanjiType == AppPrefs.HANJI_FONT_TYPE_MOEDICT) {
            mHanjiTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/moedict.ttf");
            Log.i(TAG, "Hanji font: moedict");
        } else {
            mHanjiTypeface = Typeface.DEFAULT;
            Log.i(TAG, "Hanji font: system default");
        }

        mRawInputPaint = new Paint();
        mRawInputPaint.setColor(mColorNormal);
        mRawInputPaint.setAntiAlias(true);
        mRawInputPaint.setTextSize(resources.getDimensionPixelSize(R.dimen.candidate_raw_input_font_height));
        mRawInputPaint.setStrokeWidth(0);
        mRawInputPaint.setTypeface(mLomajiTypeface);
        final Paint.FontMetrics rawInputPaintFontMetrics = mRawInputPaint.getFontMetrics();
        mRawInputPaintHeight = rawInputPaintFontMetrics.bottom - rawInputPaintFontMetrics.top + rawInputPaintFontMetrics.leading;

        mSuggestionsMainFirstLomajiPaint = new Paint();
        mSuggestionsMainFirstLomajiPaint.setColor(mColorRecommended);
        mSuggestionsMainFirstLomajiPaint.setAntiAlias(true);
        mSuggestionsMainFirstLomajiPaint.setTextSize(resources.getDimensionPixelSize(R.dimen.candidate_main_font_height));
        mSuggestionsMainFirstLomajiPaint.setStrokeWidth(0);
        mSuggestionsMainFirstLomajiPaint.setTypeface(mLomajiTypeface);
        final Paint.FontMetrics suggestionsMainFirstLomajiPaintFontMetrics = mSuggestionsMainFirstLomajiPaint.getFontMetrics();
        mMainSuggestionFirstLomajiHeightForPaint = mRawInputPaintHeight - suggestionsMainFirstLomajiPaintFontMetrics.top + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION;

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
        mSuggestionsMainPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.candidate_main_font_height));
        mSuggestionsMainPaint.setStrokeWidth(0);
        if (mIsMainCandidateLomaji) {
            mSuggestionsMainPaint.setTypeface(mLomajiTypeface);
        } else {
            mSuggestionsMainPaint.setTypeface(mHanjiTypeface);
        }
        final Paint.FontMetrics suggestionsMainPaintFontMetrics = mSuggestionsMainPaint.getFontMetrics();
        mSuggestionsMainTextHeight = suggestionsMainPaintFontMetrics.bottom - suggestionsMainPaintFontMetrics.top + suggestionsMainPaintFontMetrics.leading;

        mMainSuggestionHeightForPaint = mRawInputPaintHeight - suggestionsMainPaintFontMetrics.top + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION + suggestionsMainPaintFontMetrics.leading;

        mSuggestionsHintPaint = new Paint();
        mSuggestionsHintPaint.setColor(mColorRecommended);
        mSuggestionsHintPaint.setAntiAlias(true);
        mSuggestionsHintPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.candidate_hint_font_height));
        mSuggestionsHintPaint.setStrokeWidth(0);
        if (mIsMainCandidateLomaji) {
            mSuggestionsHintPaint.setTypeface(mHanjiTypeface);
        } else {
            mSuggestionsHintPaint.setTypeface(mLomajiTypeface);
        }
        final Paint.FontMetrics suggestionsHintPaintFontMetrics = mSuggestionsHintPaint.getFontMetrics();
        mSuggestionsHintTextHeight = suggestionsHintPaintFontMetrics.bottom - suggestionsHintPaintFontMetrics.top + suggestionsHintPaintFontMetrics.leading;

        mHintSuggestionHeightForPaint = mRawInputPaintHeight + Y_GAP_BETWEEN_RAW_INPUT_AND_SUGGESTIONS + mSuggestionsMainTextHeight
                - suggestionsHintPaintFontMetrics.top + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION + suggestionsHintPaintFontMetrics.leading;

        mWordSeperatorLineYForPaint = mRawInputPaintHeight + Y_GAP_BETWEEN_RAW_INPUT_AND_SUGGESTIONS + suggestionsHintPaintFontMetrics.bottom;

        mDesiredHeight = (int) (mRawInputPaintHeight
                + Y_GAP_BETWEEN_RAW_INPUT_AND_SUGGESTIONS
                + mSuggestionsMainTextHeight
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

        drawRawInput(canvas);
        drawSuggestions(canvas);
    }

    private void drawRawInput(Canvas canvas) {
        if (TextUtils.isEmpty(mRawInput)) return;

        if (canvas != null) {
            final int scrollX = getScrollX();
            canvas.drawText(mRawInput, scrollX + X_GAP, mRawInputPaint.getTextSize(), mRawInputPaint);
        }
    }

    private void drawSuggestions(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        mTotalWidth = 0;

        if (TextUtils.isEmpty(mRawInput)) return;
        if (mSuggestions == null) return;

        if (mBgPadding == null) {
            mBgPadding = new Rect(0, 0, 0, 0);
            if (getBackground() != null) {
                getBackground().getPadding(mBgPadding);
            }
        }

        int x = 0;
        final int count = mSuggestions.size();
        final int fullWordHeight = (int) (mSuggestionsMainTextHeight + Y_GAP_BETWEEN_MAIN_SUGGESTION_AND_HINT_SUGGESTION + mSuggestionsHintTextHeight);
        final int touchX = mTouchX;
        final int scrollX = getScrollX();
        final boolean scrolled = mScrolled;

//        for (int i = 0; i < count; i++) {
//            ImeDict imeDict = mSuggestions.get(i);
//            Log.e(TAG, "hanji=" + imeDict.getHanji() + ", tailo=" + imeDict.getTailo() + ", poj=" + imeDict.getPoj());
//        }

        for (int i = 0; i < count; i++) {
            ImeDict imeDict = mSuggestions.get(i);

            String mainCandidate = "";
            String hintCandidate = "";

            if (i == 0) {
                if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_TAILO) {
                    mainCandidate = imeDict.getTailo();
                } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                    mainCandidate = imeDict.getPoj();
                }
                hintCandidate = imeDict.getHanji();
            } else {
                if (mIsMainCandidateLomaji) {
                    if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_TAILO) {
                        mainCandidate = imeDict.getTailo();
                    } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                        mainCandidate = imeDict.getPoj();
                    }
                    hintCandidate = imeDict.getHanji();
                } else {
                    mainCandidate = imeDict.getHanji();
                    if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_TAILO) {
                        hintCandidate = imeDict.getTailo();
                    } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                        hintCandidate = imeDict.getPoj();
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
            final int fullWordWidth = (mainWordWidth > hintWordWidth ? mainWordWidth : hintWordWidth) + X_GAP * 2;

            // draw touched effect
            if (touchX + scrollX >= x && touchX + scrollX < x + fullWordWidth && !scrolled) {
                if (mTouchX != OUT_OF_BOUNDS) {
                    canvas.translate(x, 0);
                    mSelectionHighlightDrawable.setBounds(0, (int) mWordSeperatorLineYForPaint,
                            fullWordWidth, (int) (mWordSeperatorLineYForPaint + fullWordHeight));
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
                if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_TAILO) {
                    lomaji = imeDict.getTailo();
                } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                    lomaji = imeDict.getPoj();
                }
                final float fullFirstLomajiWidth = mSuggestionsMainFirstLomajiPaint.measureText(lomaji) + X_GAP * 2;
                canvas.drawLine(x + fullFirstLomajiWidth, mWordSeperatorLineYForPaint,
                        x + fullFirstLomajiWidth, mWordSeperatorLineYForPaint + fullWordHeight, mWordSeperatorLinePaint);

                x += fullFirstLomajiWidth;
            } else {
                // draw main candidate
                canvas.drawText(mainCandidate, x + X_GAP, mMainSuggestionHeightForPaint, mSuggestionsMainPaint);

                // draw hint candidate
                canvas.drawText(hintCandidate, x + X_GAP, mHintSuggestionHeightForPaint, mSuggestionsHintPaint);

                // draw line between words
                canvas.drawLine(x + fullWordWidth, mWordSeperatorLineYForPaint,
                        x + fullWordWidth, mWordSeperatorLineYForPaint + fullWordHeight, mWordSeperatorLinePaint);

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

    protected void setSuggestions(String rawInput, ArrayList<ImeDict> suggestions, int currentInputLomajiMode) {
        clear();
        mRawInput = rawInput;
        if (suggestions != null) {
            mSuggestions.clear();
            mSuggestions.addAll(suggestions);
        }
        mCurrentInputLomajiMode = currentInputLomajiMode;

        scrollTo(0, 0);
        mTargetScrollX = 0;
        invalidate();
        requestLayout();
    }

    public void clear() {
        mRawInput = null;
        mSuggestions.clear();
        mTouchX = OUT_OF_BOUNDS;
        mSelectedIndex = -1;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (mGestureDetector.onTouchEvent(me)) {
            return true;
        }

        int action = me.getAction();
//        int x = (int) me.getX();
//        int y = (int) me.getY();

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
                        mVibrator.vibrate(TaigiIme.KEY_VIBRATION_MILLISECONDS);

                        final ImeDict imeDict = mSuggestions.get(mSelectedIndex);
                        if (mSelectedIndex == 0 || mIsMainCandidateLomaji) {
                            if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_TAILO) {
                                mService.commitPickedSuggestion(imeDict.getTailo());

                                if (BuildConfig.DEBUG_LOG) {
                                    Log.d(TAG, "Selected output: " + imeDict.getTailo());
                                }
                            } else if (mCurrentInputLomajiMode == AppPrefs.INPUT_LOMAJI_MODE_POJ) {
                                mService.commitPickedSuggestion(imeDict.getPoj());

                                if (BuildConfig.DEBUG_LOG) {
                                    Log.d(TAG, "Selected output: " + imeDict.getPoj());
                                }
                            }
                        } else {
                            mService.commitPickedSuggestion(imeDict.getHanji());

                            if (BuildConfig.DEBUG_LOG) {
                                Log.d(TAG, "Selected output: " + imeDict.getHanji());
                                int count = imeDict.getHanji().length();
                                for (int i = 0; i < count; i++) {
                                    final char charAt = imeDict.getHanji().charAt(i);
                                    Log.d(TAG, "Selected hanji code: " + String.format("\\u%04x", (int) charAt));
                                }

                                Log.d(TAG, "gau5: \uD842\uDC95");
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
        mHandler.postDelayed(mLongTouchTask, 500);
    }

    private void stopTriggeredLongTouchEvent() {
        mLongTouchTask.stop();
        mHandler.removeCallbacks(mLongTouchTask);
    }

    private void onLongTouched() {
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "onLongTouched()");
        }

        // check TaigiDict install
        PackageManager pm = mContext.getPackageManager();
        boolean isInstalled = isPackageInstalled("com.taccotap.taigidict", pm);
        if (!isInstalled) {
            Toast.makeText(mContext, "若欲 tshiau-tshuē 字詞 ài 先安裝辭典 ê APP。", Toast.LENGTH_LONG).show();

            String appPackageName = "com.taccotap.taigidict";
            try {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

            return;
        }

        final ImeDict imeDict = mSuggestions.get(mSelectedIndex);

        final Intent intent = new Intent(ACTION_SEARCH_FROM_PHAHTAIGI);
        if (mSelectedIndex == 0 || mIsMainCandidateLomaji) {
            intent.putExtra(EXTRA_TAILO_SEARCH_KEYWORD, imeDict.getTailo());
        } else {
            intent.putExtra(EXTRA_TAILO_HANJI_SEARCH_KEYWORD, imeDict.getHanji());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mContext.startActivity(intent);
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