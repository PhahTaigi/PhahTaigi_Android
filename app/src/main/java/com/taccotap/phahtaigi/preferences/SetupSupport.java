package com.taccotap.phahtaigi.preferences;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public class SetupSupport {
    private static final String TAG = SetupSupport.class.getSimpleName();

    public static boolean isThisKeyboardSetAsDefaultIME(Context context) {
        final String defaultIME = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        return isThisKeyboardSetAsDefaultIME(defaultIME, context.getPackageName());
    }

    @VisibleForTesting
    /*package*/ static boolean isThisKeyboardSetAsDefaultIME(String defaultIME, String myPackageName) {
        if (TextUtils.isEmpty(defaultIME))
            return false;

        ComponentName defaultInputMethod = ComponentName.unflattenFromString(defaultIME);
        if (defaultInputMethod.getPackageName().equals(myPackageName)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isThisKeyboardEnabled(@NonNull Context context) {
        final String enabledIMEList = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS);
        return isThisKeyboardEnabled(enabledIMEList, context.getPackageName());
    }

    @VisibleForTesting
    /*package*/ static boolean isThisKeyboardEnabled(String enabledIMEList, String myPackageName) {
        if (TextUtils.isEmpty(enabledIMEList))
            return false;

        String[] enabledIMEs = enabledIMEList.split(":");
        for (String enabledIMEId : enabledIMEs) {
            Log.d(TAG, "enabledIMEId: " + enabledIMEId);

            ComponentName enabledIME = ComponentName.unflattenFromString(enabledIMEId);
            if (enabledIME != null && enabledIME.getPackageName().equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }
}
