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

public class FriendsRecommendList extends ListActivity {

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
            Log.d("name",mes);
            // Call CopyReadAssets function

            new AlertDialog.Builder(FriendsRecommendList.this)
                    .setTitle("친구추천")
                    .setMessage("비슷한 음악적 취향을 가진 친구를 추천해줍니다.")
                    .setPositiveButton("플레이리스트", new DialogInterface.OnClickListener() {
                        //move to notification setting page
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(FriendsRecommendList.this, PlayList.class);
                            intent.putExtra("playlist", mes);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("친구신청", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            new MyDownloadTask1().execute();
                            dialog.dismiss();

                        }
                    })
                    .show();


        }
    };

    class MyDownloadTask1 extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            //display progress dialog.
        }

        protected String doInBackground(Void... param) {


            try {
                Log.d("test", "1");
                HttpClient httpclient = new DefaultHttpClient();
                Log.d("test", "2");
                Profile profile = Profile.getCurrentProfile();
                Log.d("test", "3");
                JSONObject jsonObject = new JSONObject();
                Log.d("test", "4");
                try {
                    Log.d("test", "5");
                    jsonObject.accumulate("user_id", profile.getName());
                    Log.d("test", "6");
                    jsonObject.accumulate("mate_id", mes);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("test", "7");
                String json = jsonObject.toString();

                Log.d("test", "8");
                HttpPost httpPost = new HttpPost("http://52.68.143.225:9000/Sympathy/mate/materequest/");
                Log.d("test", "9");
                ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();
                Log.d("test", "10");
                HttpParams params = httpclient.getParams();
                Log.d("test", "11");
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                Log.d("test", "12");
                HttpConnectionParams.setSoTimeout(params, 5000);
                Log.d("test", "13");

                post.add(new BasicNameValuePair("data", json));
                Log.d("test", "14");
                UrlEncodedFormEntity entity = null;
                try {
                    Log.d("test", "15");
                    entity = new UrlEncodedFormEntity(post, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d("test", "16");
                httpPost.setEntity(entity);
                Log.d("test", "17");

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
                Log.d("InputStream", e.getLocalizedMessage());
                e.printStackTrace();
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


                HttpPost httpPost = new HttpPost("http://52.68.143.225:9000/Sympathy/mate/recommend/");

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
