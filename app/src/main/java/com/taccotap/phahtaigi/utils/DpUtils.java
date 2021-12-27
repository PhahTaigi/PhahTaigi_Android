package com.taccotap.phahtaigi.utils;

import android.content.Context;

public class DpUtils {
    public static int dpToPixels(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale);
    }
}
