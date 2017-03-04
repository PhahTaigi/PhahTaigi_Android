package com.taccotap.phahtaigi.ime.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.taccotap.phahtaigi.R;

import static android.R.attr.opacity;

public class TaigiKeyboard extends Keyboard {
    private static final String TAG = TaigiKeyboard.class.getSimpleName();

    private static final float SPACEKEY_LANGUAGE_BASELINE = 0.75f;
    private static final int SPACEKEY_TEXT_SIZE = 11;
    private static final int SPACEKEY_ICON_MARGIN_SIZE = 15;

    private Key mEnterKey;
    private Key mSpaceKey;
    private Drawable mSpaceIcon;

    public TaigiKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);

        drawSpaceBarImeText(context);
    }

    public TaigiKeyboard(Context context, int layoutTemplateResId,
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);

        drawSpaceBarImeText(context);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new TaigiKey(res, parent, x, y, parser);
        if (key.codes[0] == 10) {
            mEnterKey = key;
        } else if (key.codes[0] == ' ') {
            mSpaceKey = key;
        }
        return key;
    }

    private void drawSpaceBarImeText(Context context) {
        mSpaceKey.icon = new BitmapDrawable(context.getResources(), getSpaceBarBitmap(context));
    }

    private Bitmap getSpaceBarBitmap(Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Log.d(TAG, "screen density: " + displayMetrics.density);

        mSpaceIcon = resources.getDrawable(R.drawable.btn_keyboard_spacebar_lxx_dark);

        final int width = mSpaceKey.width;
        final int height = mSpaceIcon.getIntrinsicHeight();
        final Bitmap buffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(buffer);
        canvas.drawColor(resources.getColor(android.R.color.transparent), PorterDuff.Mode.CLEAR);

        final Paint paint = new Paint();
        paint.setAlpha(opacity);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize((int) (SPACEKEY_TEXT_SIZE * displayMetrics.density));

        final String language = resources.getString(R.string.spacekey_text);

        final float baseline = height * SPACEKEY_LANGUAGE_BASELINE;
        final float descent = paint.descent();
        paint.setColor(resources.getColor(R.color.spacekey_text_color));

        canvas.drawText(language, width / 2, baseline - descent, paint);

        // Draw the spacebar icon at the bottom
        final int iconWidth = mSpaceKey.width - (int) (SPACEKEY_ICON_MARGIN_SIZE * displayMetrics.density);
        final int iconHeight = mSpaceIcon.getIntrinsicHeight();
        int x = (width - iconWidth) / 2;
        int y = height - iconHeight;
        mSpaceIcon.setBounds(x, y, x + iconWidth, y + iconHeight);
        mSpaceIcon.draw(canvas);

        return buffer;
    }

    /**
     * This looks at the ime options given by the current editor, to set the
     * appropriate label on the keyboard's enter key (if it has one).
     */
    public void setImeOptions(Resources res, int options) {
        if (mEnterKey == null) {
            return;
        }
        switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_search);
                mEnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                mEnterKey.iconPreview = null;
                mEnterKey.icon = null;
                mEnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                mEnterKey.icon = res.getDrawable(R.drawable.sym_keyboard_return);
                mEnterKey.label = null;
                break;
        }
    }

    public void setSpaceIcon(final Drawable icon) {
        if (mSpaceKey != null) {
            mSpaceKey.icon = icon;
        }
    }

    static class TaigiKey extends Key {

        public TaigiKey(Row parent) {
            super(parent);
        }

        public TaigiKey(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }

//        public TaigiKey(Resources res, Row parent, int x, int y,
//                        XmlResourceParser parser) {
//            super(res, parent, x, y, parser);
//        }
//
//        /**
//         * Overriding this method so that we can reduce the target area for the key that
//         * closes the keyboard.
//         */
//        @Override
//        public boolean isInside(int x, int y) {
//            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
//        }
    }
}