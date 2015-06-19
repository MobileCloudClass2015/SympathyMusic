package kookmin.cs.sympathymusiz;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Profile;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class FriendList extends ListActivity {

    private EditText et;    // Variable for the search function
    int textlength = 0;        // Variable for the search function

    ArrayList<String> tempList = new ArrayList<String>();    //Temporary array for initializing the search function

    private TextView layoutTitle;
    private ListView list;
    private String[] files;
    InputStream inputStream = null;
    String result = "";
    String mes = "";

    final MyAdapter Adapter = new MyAdapter(this);    // Adapter for managing a list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.filelist);


        list = (ListView) findViewById(android.R.id.list);


        new MyDownloadTask().execute();

        list.setAdapter(Adapter);
        list.setTextFilterEnabled(true);
        list.setOnItemClickListener(mItemClickListener);


    }


    private OnItemClickListener mItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            // �׸� �����̸� �޾ƿ���

            // Copy the selected string in a list
            mes = Adapter.getArr().get(position);
            // Call CopyReadAssets function

            new AlertDialog.Builder(FriendList.this)
                    .setTitle("친구의 음악 리스트")
                    .setMessage("친구의 리스트를 보시려면 OK를 누르세요.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        //move to notification setting page
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(FriendList.this, PlayList.class);
                            intent.putExtra("playlist", mes);
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();


        }
    };

    class MyDownloadTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            //display progress dialog.
        }

        protected String doInBackground(Void... param) {


            try {
                HttpClient httpclient = new DefaultHttpClient();
                Profile profile = Profile.getCurrentProfile();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.accumulate("user_id", profile.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String json = jsonObject.toString();


                HttpPost httpPost = new HttpPost("http://52.68.143.225:9000/Sympathy/mate/mymatelist/");

                ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

                HttpParams params = httpclient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);

                post.add(new BasicNameValuePair("data", json));

                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(post, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                httpPost.setEntity(entity);

                HttpResponse httpResponse = null;
                try {
                    httpResponse = httpclient.execute(httpPost);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if (httpResponse != null) {
                    try {
                        inputStream = httpResponse.getEntity().getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (inputStream != null) {


                        try {
                            result = convertInputStreamToString(inputStream);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Log.d("test", result);
                        return result;


                    } else
                        result = null;
                }
            } catch (Exception e) {
//                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String rs) {
            super.onPostExecute(result);

            Log.d("test", "들어갔니?");
            JSONObject object = null;
            try {
                object = new JSONObject(rs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray Array = null;
            try {
                Array = new JSONArray(object.getString("mate_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < Array.length(); i++) {
                JSONObject insideObject = null;
                try {
                    insideObject = Array.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    Adapter.addItem(insideObject.getString("id"));
                    Log.d("test", insideObject.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


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



    class MyDownloadTask1 extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            //display progress dialog.
        }

        protected String doInBackground(Void... param) {


            try {
                HttpClient httpclient = new DefaultHttpClient();
                Profile profile = Profile.getCurrentProfile();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.accumulate("user_id", profile.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String json = jsonObject.toString();


                HttpPost httpPost = new HttpPost("http://52.68.143.225:9000/Sympathy/mate/mymatelist/");

                ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

                HttpParams params = httpclient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);

                post.add(new BasicNameValuePair("data", json));

                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(post, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                httpPost.setEntity(entity);


            } catch (Exception e) {
//                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String rs) {
            super.onPostExecute(result);

            Log.d("test", "들어갔니?");
            JSONObject object = null;
            try {
                object = new JSONObject(rs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray Array = null;
            try {
                Array = new JSONArray(object.getString("mate_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < Array.length(); i++) {
                JSONObject insideObject = null;
                try {
                    insideObject = Array.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                try {
                    Adapter.addItem(insideObject.getString("id"));
                    Log.d("test", insideObject.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }


    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        return false;
    }

    public void AppendList(ArrayList<String> str) {
        setListAdapter((ListAdapter) new bsAdapter(this));
    }

    public class bsAdapter extends BaseAdapter {
        Activity cntx;

        public bsAdapter(Activity context) {
            // TODO Auto-generated constructor stub
            this.cntx = context;

        }

        public int getCount() {
            // TODO Auto-generated method stub
            return Adapter.getArr().size();
        }

        public Object getItem(int position) {
            // TODOArray Auto-generated method stub
            return Adapter.getArr().get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return Adapter.getArr().size();
        }


        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View row = null;

            LayoutInflater inflater = cntx.getLayoutInflater();

            //if you want to change layout of list, you edit list_item.xml
            row = inflater.inflate(R.layout.list_item, null);

            TextView tv = (TextView) row.findViewById(R.id.lblListItem);

            tv.setText(Adapter.getArr().get(position));

            return row;
        }
    }

}
