package com.taccotap.phahtaigi;

public class AppPrefs {
    public static final String PREFS_KEY_CURRENT_INPUT_MODE = "PREFS_KEY_CURRENT_INPUT_MODE";
    public static final String PREFS_KEY_CURRENT_INPUT_LOMAJI_MODE = "PREFS_KEY_CURRENT_INPUT_LOMAJI_MODE";
    public static final String PREFS_KEY_HAS_SHOW_SETTING_FIRST_TIME_V1_3_2 = "PREFS_KEY_HAS_SHOW_SETTING_FIRST_TIME_V1_3_2";
    public static final String PREFS_KEY_IS_SHOW_SETTING = "PREFS_KEY_IS_SHOW_SETTING";

    public static final int INPUT_MODE_LOMAJI = 0;
    public static final int INPUT_MODE_HANJI = 1;

    public static final int INPUT_LOMAJI_MODE_ENGLISH = -1;
    public static final int INPUT_LOMAJI_MODE_TAILO = 0;
    public static final int INPUT_LOMAJI_MODE_POJ = 1;
    public static final int INPUT_LOMAJI_MODE_APP_DEFAULT = INPUT_LOMAJI_MODE_TAILO;

    public static final String PREFS_KEY_HANJI_FONT_TYPE = "PREFS_KEY_HANJI_FONT_TYPE";
    public static final int HANJI_FONT_TYPE_SYSTEM_DEFAULT = 0;
    public static final int HANJI_FONT_TYPE_MINGLIUB = 1;
    public static final int HANJI_FONT_TYPE_MOEDICT = 2;
    public static final int HANJI_FONT_TYPE_APP_DEFAULT = HANJI_FONT_TYPE_MINGLIUB;

    public static final String PREFS_KEY_IS_VIBRATION = "PREFS_KEY_IS_VIBRATION";
    public static final boolean PREFS_KEY_IS_VIBRATION_YES = true;
    public static final boolean PREFS_KEY_IS_VIBRATION_NO = false;
}
