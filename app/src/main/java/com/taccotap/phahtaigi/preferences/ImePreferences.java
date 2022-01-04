package com.taccotap.phahtaigi.preferences;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.taccotap.phahtaigi.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImePreferences extends AppCompatActivity {
    private static final int KEY_MESSAGE_UNREGISTER_LISTENER = 447;
    private static final int KEY_MESSAGE_RETURN_TO_APP = 446;

    @BindView(R.id.step1Button)
    Button mStep1Button;

    @BindView(R.id.step2Button)
    Button mStep2Button;

    @BindView(R.id.supportButton)
    Button mSupportButton;

    @BindView(R.id.feedbackButton)
    Button mFeedbackButton;

    private Context mBaseContext = null;
    private Intent mReLaunchTaskIntent = null;
    private Context mAppContext;

    @SuppressWarnings("HandlerLeak"/*I want this fragment to stay in memory as long as possible*/)
    private Handler mGetBackHereHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KEY_MESSAGE_RETURN_TO_APP:
                    if (mReLaunchTaskIntent != null && mBaseContext != null) {
                        mBaseContext.startActivity(mReLaunchTaskIntent);
                        mReLaunchTaskIntent = null;
                    }
                    break;
                case KEY_MESSAGE_UNREGISTER_LISTENER:
                    unregisterSettingsObserverNow();
                    break;
            }
        }
    };
    private final ContentObserver mSecureSettingsChanged = new ContentObserver(null) {
        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean selfChange) {
            if (isStepCompleted(mAppContext)) {
                //should we return to this task?
                //this happens when the user is asked to enable Keyboard, which is done on a different UI activity (outside of my App).
                mGetBackHereHandler.removeMessages(KEY_MESSAGE_RETURN_TO_APP);
                mGetBackHereHandler.sendMessageDelayed(mGetBackHereHandler.obtainMessage(KEY_MESSAGE_RETURN_TO_APP), 50/*enough for the user to see what happened.*/);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        ButterKnife.bind(this);

        mBaseContext = getBaseContext();
        mReLaunchTaskIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        mReLaunchTaskIntent.addFlags(FLAG_ACTIVITY_CLEAR_TASK);
        mAppContext = getApplicationContext();

        PackageInfo pInfo = null;
        try {
            pInfo = mBaseContext.getPackageManager().getPackageInfo(mBaseContext.getPackageName(), 0);
            String versionName = pInfo.versionName;
            setTitle("PhahTaigi " + versionName + " pán");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        init();
    }

    private void init() {
        mAppContext.getContentResolver().registerContentObserver(Settings.Secure.CONTENT_URI, true, mSecureSettingsChanged);
        //but I don't want to listen for changes for ever!
        //If the user is taking too long to change one checkbox, I say forget about it.
        mGetBackHereHandler.removeMessages(KEY_MESSAGE_UNREGISTER_LISTENER);
        mGetBackHereHandler.sendMessageDelayed(mGetBackHereHandler.obtainMessage(KEY_MESSAGE_UNREGISTER_LISTENER),
                45 * 1000/*45 seconds to change a checkbox is enough. After that, I wont listen to changes anymore.*/);

    }

    @OnClick(R.id.step1Button)
    void onClickStep1Button() {
        Intent startSettings = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        startSettings.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(startSettings);
    }

    @OnClick(R.id.step2Button)
    void onClickStep2Button() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showInputMethodPicker();
    }

    @OnClick(R.id.supportButton)
    void onClickOtherSettingButton() {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zeczec.com/projects/taibun-kesimi"));
        startActivity(myIntent);
    }

    @OnClick(R.id.feedbackButton)
    void onClickFeedbackButton() {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/PhahTaigi"));
        startActivity(myIntent);
    }

    protected boolean isStepCompleted(@NonNull Context context) {
        return SetupSupport.isThisKeyboardEnabled(context);
    }

    private void unregisterSettingsObserverNow() {
        mGetBackHereHandler.removeMessages(KEY_MESSAGE_UNREGISTER_LISTENER);
        if (mAppContext != null) {
            mAppContext.getContentResolver().unregisterContentObserver(mSecureSettingsChanged);
            mAppContext = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterSettingsObserverNow();
    }
}
