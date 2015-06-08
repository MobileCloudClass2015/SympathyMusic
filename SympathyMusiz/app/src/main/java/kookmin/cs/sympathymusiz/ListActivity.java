package kookmin.cs.sympathymusiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by seojunkyo on 2015. 6. 4..
 */
public class ListActivity extends FragmentActivity{

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_list);

        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        List permissionNeeds = Arrays.asList("publish_actions");

        ImageButton fb_share = (ImageButton)findViewById(R.id.fb_logo);

        fb_share.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "URLyouWantToShare");
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });
    }
}
