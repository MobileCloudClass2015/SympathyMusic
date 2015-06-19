package kookmin.cs.sympathymusiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    public JSONObject jsonObj;
    public String json;
    public String userid = null;
    public String username = null;

    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mcallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            jsonObj = object;
                            MyDownloadTask task = new MyDownloadTask();
                            task.execute();
                        }
                    });



            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();

            Intent intent = new Intent(getActivity(), ModeActivity.class);
            startActivity(intent);
        }

        @Override
        public void onCancel(){
            Log.v("LoginActivity", "cancel");
        }

        @Override
        public void onError(FacebookException e) {
            Log.v("LoginActivity", e.getCause().toString());
        }
    };

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mcallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    class MyDownloadTask extends AsyncTask<Void, Void, Void> {
        public static final String TAG = "YOUR-TAG-NAME";
        protected void onPreExecute() {
            //display progress dialog.
        }

        protected Void doInBackground(Void... param) {
            InputStream inputStream;
            String result = "";
            try {
                HttpClient httpclient = new DefaultHttpClient();

                String json = jsonObj.toString();

                Log.v("JSON", json);

                HttpPost httpPost = new HttpPost("http://52.68.143.225:9000/Sympathy/user/register/");

                ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

                HttpParams params = httpclient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);

                post.add(new BasicNameValuePair("data", json));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                httpPost.setEntity(entity);

                HttpResponse httpResponse = httpclient.execute(httpPost);

                //Log.d(TAG,httpResponse.toString());

                if (httpResponse != null) {
                    inputStream = httpResponse.getEntity().getContent();

                    if (inputStream != null) {
                        result = convertInputStreamToString(inputStream);
                        Log.d(TAG, result);
                    } else
                        result = "Didn't work!";
                }
            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
            return null;
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            inputStream.close();
            return result;
        }
    }
}
