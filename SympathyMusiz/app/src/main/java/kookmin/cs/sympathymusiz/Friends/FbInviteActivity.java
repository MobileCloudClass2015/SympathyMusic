package kookmin.cs.sympathymusiz.Friends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;


/**
 * Created by seojunkyo on 2015. 3. 18..
 */
public class FbInviteActivity extends Activity {

    private static final String TAG = "FacebookInvite";
    private static final int INVALID_CHOICE = -1;

    private Fragment fragment;

    private boolean isResumed = false;
    private boolean hasNativeLink = false;
    private CallbackManager callbackManager;
    private GameRequestDialog gameRequestDialog;
    private AppInviteDialog appInviteDialog;

    private AccessTokenTracker accessTokenTracker;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presentAppInviteDialog();

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        FacebookCallback<AppInviteDialog.Result> appInviteCallback =
                new FacebookCallback<AppInviteDialog.Result>() {
                    @Override
                    public void onSuccess(AppInviteDialog.Result result) {
                        Log.d(TAG, "Success!");
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Canceled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, String.format("Error: %s", error.toString()));
                    }
                };

        appInviteDialog = new AppInviteDialog(this);
        appInviteDialog.registerCallback(callbackManager, appInviteCallback);

        hasNativeLink = handleNativeLink();

        gameRequestDialog = new GameRequestDialog(this);
        callbackManager = CallbackManager.Factory.create();
        gameRequestDialog.registerCallback(
                callbackManager,
                new FacebookCallback<GameRequestDialog.Result>() {
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "Canceled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, String.format("Error: %s", error.toString()));
                    }

                    @Override
                    public void onSuccess(GameRequestDialog.Result result) {
                        Log.d(TAG, "Success!");
                        Log.d(TAG, "Request id: " + result.getRequestId());
                        Log.d(TAG, "Recipients:");
                        for (String recipient : result.getRequestRecipients()) {
                            Log.d(TAG, recipient);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[RPS].isVisible()) {
            if (menu.size() == 0) {
                share = menu.add(R.string.share_on_facebook);
                message = menu.add(R.string.send_with_messenger);
                challenge = menu.add(R.string.challenge_friends);
                settings = menu.add(R.string.check_settings);
                invite = menu.add(R.string.invite_friends);
            }
            return true;
        } else {
            menu.clear();
            settings = null;
        }
        return false;
    }
    */

    public void presentAppInviteDialog() {
        AppInviteContent content = new AppInviteContent.Builder()
                .setApplinkUrl("http://www.kookmin.ac.kr")
                .setPreviewImageUrl("https://scontent-hkg3-1.xx.fbcdn.net/hphotos-xft1/v/t1.0-9/11201920_477856305717789_4916180707037746381_n.jpg?oh=23df2fb92e8fb633fa2d5b8991a082a0&oe=5628D8EA")
                .build();
        if (AppInviteDialog.canShow()) {
            appInviteDialog.show(this, content);
        }
    }
    /*
    private void showError(int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.error_dialog_title).
                setMessage(messageId).
                setPositiveButton(R.string.error_ok_button, null);
        builder.show();
    }
    */


    private boolean handleNativeLink() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            AccessToken.createFromNativeLinkingIntent(this.getIntent(),
                    FacebookSdk.getApplicationId(), new AccessToken.AccessTokenCreationCallback() {

                        @Override
                        public void onSuccess(AccessToken token) {
                            AccessToken.setCurrentAccessToken(token);
                        }

                        @Override
                        public void onError(FacebookException error) {

                        }
                    });
        }
        // See if we have a deep link in addition.
        return false;
    }
}
