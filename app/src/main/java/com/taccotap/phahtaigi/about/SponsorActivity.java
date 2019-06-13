package com.taccotap.phahtaigi.about;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taccotap.phahtaigi.BuildConfig;
import com.taccotap.phahtaigi.R;
import com.taccotap.phahtaigi.putils.IabBroadcastReceiver;
import com.taccotap.phahtaigi.putils.IabHelper;
import com.taccotap.phahtaigi.putils.IabResult;
import com.taccotap.phahtaigi.putils.Inventory;
import com.taccotap.phahtaigi.putils.Purchase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SponsorActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private static final String TAG = SponsorActivity.class.getSimpleName();

    private static final String SPONSOR_PRODUCT_ID = "sponsor_subscription_monthly_99";

    // (arbitrary) request code for the purchase flow
    private static final int RC_REQUEST = 10001;

    @BindView(R.id.paymentButton)
    Button mPaymentButton;

    @BindView(R.id.thankYouTextView)
    TextView mThankYouTextView;

    @BindView(R.id.descTextView)
    TextView mDescTextView;

    // The helper object
    private IabHelper mHelper;

    // Provides purchase notification while this app is running
    private IabBroadcastReceiver mBroadcastReceiver;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Does the user have an active subscription to the infinite gas plan?
    boolean mIsSubscribedToSponsorProgram = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
         * (that you got from the Google Play developer console). This is not your
         * developer public key, it's the *app-specific* public key.
         *
         * Instead of just storing the entire literal string here embedded in the
         * program,  construct the key at runtime from pieces or
         * use bit manipulation (for example, XOR with some other string) to hide
         * the actual key.  The key itself is not secret information, but we don't
         * want to make it easy for an attacker to replace the public key with one
         * of their own and then fake messages from the server.
         */
        if (BuildConfig.DEBUG_LOG) {
            Log.d(TAG, "BuildConfig.PUBLIC_KEY = " + BuildConfig.PUBLIC_KEY);
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, BuildConfig.PUBLIC_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(SponsorActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain("Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query: " + result);
                return;
            }

            Log.d(TAG, "Query was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // First find out which subscription is auto renewing
            Purchase sponsorMonthly = inventory.getPurchase(SPONSOR_PRODUCT_ID);
            if (sponsorMonthly != null && sponsorMonthly.isAutoRenewing()) {
                mAutoRenewEnabled = true;
            } else {
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mIsSubscribedToSponsorProgram = (sponsorMonthly != null);
            Log.d(TAG, "User " + (mIsSubscribedToSponsorProgram ? "HAS" : "DOES NOT HAVE") + " sponsor subscription.");

            updateUi();
            setWaitScreen(false);

            Log.d(TAG, "Initial query finished; enabling main UI.");
        }
    };

    private void updateUi() {
        if (mIsSubscribedToSponsorProgram) {
            mPaymentButton.setVisibility(View.GONE);
            mThankYouTextView.setVisibility(View.VISIBLE);
            mDescTextView.setVisibility(View.VISIBLE);
        } else {
            mPaymentButton.setVisibility(View.VISIBLE);
            mThankYouTextView.setVisibility(View.GONE);
            mDescTextView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.paymentButton)
    void onClickPaymentButton() {
        setWaitScreen(true);

        Log.d(TAG, "Launching purchase flow for sponsor subscription.");
        try {
            mHelper.launchPurchaseFlow(this, SPONSOR_PRODUCT_ID, IabHelper.ITEM_TYPE_SUBS,
                    null, RC_REQUEST, mPurchaseFinishedListener, null);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error launching purchase flow. Another async operation in progress.");
            setWaitScreen(false);
        }
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SPONSOR_PRODUCT_ID)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Sponsor subscription purchased.");

                mIsSubscribedToSponsorProgram = true;
                mAutoRenewEnabled = purchase.isAutoRenewing();

                updateUi();
                setWaitScreen(false);
            }
        }
    };

//    // Called when consumption is complete
//    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
//        public void onConsumeFinished(Purchase purchase, IabResult result) {
//            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
//
//            // if we were disposed of in the meantime, quit.
//            if (mHelper == null) return;
//
//            // We know this is the "gas" sku because it's the only one we consume,
//            // so we don't check which sku was consumed. If you have more than one
//            // sku, you probably should check...
//            if (result.isSuccess()) {
//                // successfully consumed, so we apply the effects of the item in our
//                // game world's logic, which in our case means filling the gas tank a bit
//                Log.d(TAG, "Consumption successful. Provisioning.");
//            } else {
//                complain("Error while consuming: " + result);
//            }
//            updateUi();
//            setWaitScreen(false);
//
//            Log.d(TAG, "End consumption flow.");
//        }
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    void complain(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            complain("Error querying inventory. Another async operation in progress.");
        }
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }
}
