package kookmin.cs.sympathymusiz;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.facebook.Profile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import kookmin.cs.sympathymusiz.Friends.FbInviteActivity;
import kookmin.cs.sympathymusiz.usersettings.UserSettingsFragment;

/**
 * Created by seojunkyo on 2015. 6. 5..
 */
public class ModeActivity extends FragmentActivity {

    ListView list;
    Cursor cursor;

    private UserSettingsFragment userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mode);


        ImageButton upload = (ImageButton)findViewById(R.id.upload);
        ImageButton friends = (ImageButton)findViewById(R.id.friends);
        ImageButton share = (ImageButton)findViewById(R.id.friends_fb);
        ImageButton recomd = (ImageButton)findViewById(R.id.friends_recomd);

        friends.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                    userFragment = new UserSettingsFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(android.R.id.content, userFragment)
                            .commit();
            }
        });
        upload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);
            }
        });

        recomd.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FriendsList.class);
                startActivityForResult(intent, 0);
            }
        });

        share.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FbInviteActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                if (cursor.moveToFirst()) {
                    uri = Uri.parse(cursor.getString(0));
                }

                File myFile = new File(uri.getPath());
                myFile.getAbsolutePath();
                Log.d("test", myFile.toString());
                uploadFile(uri.getPath().toString());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadFile(String filepath){

        File myFile = new File(filepath);

        RequestParams params = new RequestParams();
        try {
            params.put("file", myFile);
            Profile profile = Profile.getCurrentProfile();


            params.put("user_id",profile.getName());
        } catch(FileNotFoundException e) {}

        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://52.68.143.225:9000/Sympathy/music/upload/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try{ String result = new String(bytes,"UTF-8");
                    Log.d("print", result);

                    Intent intent = new Intent(ModeActivity.this, VideoListDemoActivity.class);
                    intent.putExtra("url",result);
                    startActivity(intent);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                // handle failure response
            }
        });
    }

}