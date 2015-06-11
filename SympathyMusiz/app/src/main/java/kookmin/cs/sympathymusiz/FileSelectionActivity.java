package kookmin.cs.sympathymusiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.merge.MergeAdapter;

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
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FileSelectionActivity extends Activity implements UploadFileCallback {

    private static final String TAG = "FileSelection";
    private static final String FILES_TO_UPLOAD = "upload";
    File mainPath = new File(Environment.getExternalStorageDirectory()+"/Music/");
    private ArrayList<File> resultFileList;

    private ListView directoryView;
    private ArrayList<File> directoryList = new ArrayList<File>();
    private ArrayList<String> directoryNames = new ArrayList<String>();
    //private ListView fileView;
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> fileNames = new ArrayList<String>();
    Button ok, all, cancel, storage , New;
    TextView path;
    Boolean Switch = false;


    Boolean switcher = false;
    String primary_sd;
    String secondary_sd;

    int index = 0;
    int top = 0;



    //서버에 올리기 위한 부분

    private ProgressDialog pDialog = null;

    ProgressDialog dialog = null;
    TextView messageText;
    String upLoadServerUri = null;
    int serverResponseCode = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_selection);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        directoryView = (ListView)findViewById(R.id.directorySelectionList);
        ok = (Button)findViewById(R.id.ok);
        all = (Button)findViewById(R.id.all);
        cancel = (Button)findViewById(R.id.cancel);
        storage = (Button)findViewById(R.id.storage);
        New = (Button)findViewById(R.id.New);
        path = (TextView)findViewById(R.id.folderpath);

        loadLists();
        New.setEnabled(false);

        ExtStorageSearch();
        if(secondary_sd==null){
            storage.setEnabled(false);
        }

        directoryView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                index = directoryView.getFirstVisiblePosition();
                View v = directoryView.getChildAt(0);
                top = (v == null) ? 0 : v.getTop();

                File lastPath = mainPath;
                try {
                    if (position < directoryList.size()) {
                        mainPath = directoryList.get(position);
                        loadLists();
                    }
                }catch (Throwable e){
                    mainPath = lastPath;
                    loadLists();
                }

            }
        });

        ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    ok();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });

        storage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    if (!switcher) {
                        mainPath = new File(secondary_sd);
                        loadLists();
                        switcher = true;
                        storage.setText(getString(R.string.Int));
                    } else {
                        mainPath = new File(primary_sd);
                        loadLists();
                        switcher = false;
                        storage.setText(getString(R.string.ext));
                    }
                }catch (Throwable e){

                }
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!Switch){
                    for (int i = directoryList.size(); i < directoryView.getCount(); i++){
                        directoryView.setItemChecked(i, true);
                    }
                    all.setText(getString(R.string.none));
                    Switch = true;
                }else if(Switch){
                    for (int i = directoryList.size(); i < directoryView.getCount(); i++) {
                        directoryView.setItemChecked(i, false);
                    }
                    all.setText(getString(R.string.all));
                    Switch = false;
                }
            }

        });
    }

    public void onBackPressed() {
        try {
            if(mainPath.equals(Environment.getExternalStorageDirectory().getParentFile().getParentFile())){
                finish();
            }else{
                File parent = mainPath.getParentFile();
                mainPath = parent;
                loadLists();
                directoryView.setSelectionFromTop(index, top);
            }

        }catch (Throwable e){

        }
    }

    public void ok() throws IOException, JSONException {
        Log.d(TAG, "Upload clicked, finishing activity");


        resultFileList = new ArrayList<File>();

        for(int i = 0 ; i < directoryView.getCount(); i++){
            if(directoryView.isItemChecked(i)){
                resultFileList.add(fileList.get(i-directoryList.size()));
                Log.d(TAG, "되는것인가?");
            }
            //Log.d(TAG, "되는것인가?");
        }
        if(resultFileList.isEmpty()){
            Log.d(TAG, "Nada seleccionado");
            finish();
        }
        final String tempurl = mainPath.toString()+resultFileList.toString();
        Toast.makeText(getApplicationContext(), fileList.toString(), Toast.LENGTH_LONG).show();
        Log.d(TAG, fileList.toString());






/*
        messageText.setText("Uploading file path :- "+tempurl+"'");

        upLoadServerUri = "http://www.androidexample.com/media/UploadToServer.php";


        dialog = ProgressDialog.show(FileSelectionActivity.this, "", "Uploading file...", true);

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("uploading started.....");
                    }
                });

                uploadFile(tempurl);

            }
        }).start();
*/
        new MyDownloadTask().execute(fileList.get(0).toString());

        Log.d(TAG, "안나오면 죽ㅇ버려");
//        new UploadFileFromURL(FileSelectionActivity.this).execute("http://52.68.183.62:9000/Sympathy/music/upload/", fileList.get(1).toString(),fileList.get(1).toString());


        Log.d(TAG, "Files: " + resultFileList.toString());
        Intent result = this.getIntent();
        result.putExtra(FILES_TO_UPLOAD, resultFileList);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    public void onUploadFilePreExecute() {
        // TODO Auto-generated method stub
        /*
        pDialog = new ProgressDialog(FileSelectionActivity.this);
        pDialog.setMessage(getResources().getString(
                R.string.main_activity_uploading_progress_dialog_context));
        pDialog.setIndeterminate(false);// ¨ú®ø¶i«×±ø
        pDialog.setCancelable(true);// ¶}±Ò¨ú®ø
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.show();
        */
    }

    @Override
    public void onUploadFileProgressUpdate(int value) {
        // TODO Auto-generated method stub
//        pDialog.setProgress(value);

    }

    @Override
    public void doUploadFilePostExecute(String result) {
        // TODO Auto-generated method stub
  //      pDialog.dismiss();
        if (result != null) {
            Toast.makeText(FileSelectionActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
/*

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"
                            +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name="uploaded_file";filename=""
                                + fileName + """ + lineEnd);

                        dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +uploadFileName;

                            messageText.setText(msg);
                            Toast.makeText(UploadToServer.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(UploadToServer.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(UploadToServer.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }

*/

    class MyDownloadTask extends AsyncTask<String, String, Void> {

        protected void onPreExecute() {
            //display progress dialog.
        }

        protected Void doInBackground(String... param) {
            InputStream inputStream;
            String result = "";
            try {
                String[] splitdata = param[0].split("/");
                Log.d(TAG,splitdata[5]);

                String[] upload1 = splitdata[5].split("-");
                Log.d(TAG,upload1[0]);
                Log.d(TAG,upload1[1]);



                upload1[1] = upload1[1].replace(".mp3","");
                Log.d(TAG,"씨발");



                HttpClient httpclient = new DefaultHttpClient();

                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("artist", upload1[0]);
                jsonObject.accumulate("title", upload1[1]);
                jsonObject.accumulate("start", 0);
                jsonObject.accumulate("count", 1);

                String json = jsonObject.toString();
                Log.d(TAG,json);

                HttpPost httpPost = new HttpPost("http://52.68.183.62:9000/Sympathy/music/search/");

                ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

                HttpParams params = httpclient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);

                post.add(new BasicNameValuePair("data", json));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                httpPost.setEntity(entity);

                HttpResponse httpResponse = httpclient.execute(httpPost);

                Log.d(TAG,httpResponse.toString());

                if (httpResponse != null) {
                    inputStream = httpResponse.getEntity().getContent();

                    if (inputStream != null) {

                        result = convertInputStreamToString(inputStream);
                        Log.d(TAG, result);


                        JSONObject object = new JSONObject(result);
                        JSONArray Array = new JSONArray(object.getString("tracks"));
                        ArrayList<ArrayList<String>> mGroupList = new ArrayList<ArrayList<String>>();
                        ArrayList<String> passdata = null;
                        for (int i=0; i< Array.length(); i++)
                        {
                            JSONObject insideObject = Array.getJSONObject(i);


                            ArrayList<String> mChildList = new ArrayList<String>();

                            mChildList.add(insideObject.getString("url"));
                            mChildList.add(insideObject.getString("title"));
                            mGroupList.add(mChildList);
                            VideoListDemoActivity.VideoListFragment test = null;
                            test.addlist(insideObject.getString("title"),insideObject.getString("url"));
                            passdata.add(insideObject.getString("title"));
                            passdata.add(insideObject.getString("url"));
                        }
                        Intent exIntent = new Intent(FileSelectionActivity.this,VideoListDemoActivity.class);
                        exIntent.putStringArrayListExtra("test", passdata);
                        startActivity(exIntent);




                    } else
                        result = "Didn't work!";
                }
            } catch (Exception e) {
//                Log.d("InputStream", e.getLocalizedMessage());
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

    private void loadLists() {
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        FileFilter directoryFilter = new FileFilter() {
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            };

        //if(mainPath.exists() && mainPath.length()>0){
        //Lista de directorios
        File[] tempDirectoryList = mainPath.listFiles(directoryFilter);

        if (tempDirectoryList != null && tempDirectoryList.length > 1) {
            Arrays.sort(tempDirectoryList, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
                });
        }

        directoryList = new ArrayList<File>();
        directoryNames = new ArrayList<String>();
        for (File file : tempDirectoryList) {
            directoryList.add(file);
            directoryNames.add(file.getName());
        }
        ArrayAdapter<String> directoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, directoryNames);

        //Lista de ficheros
        File[] tempFileList = mainPath.listFiles(fileFilter);

        if (tempFileList != null && tempFileList.length > 1) {
            Arrays.sort(tempFileList, new Comparator<File>() {
                    @Override
                    public int compare(File object1, File object2) {
                    return object1.getName().compareTo(object2.getName());
                }
                });
        }

        fileList = new ArrayList<File>();
        fileNames = new ArrayList<String>();
        for (File file : tempFileList) {
            fileList.add(file);
            fileNames.add(file.getName());
        }


        path.setText(mainPath.toString());
        iconload();
        setTitle(mainPath.getName());
        //}
    }

        /**
         * @Override public boolean onCreateOptionsMenu(Menu menu) {
         * getMenuInflater().inflate(R.menu.activity_file_selection, menu);
         * return true;
         * }
         * @Override public boolean onOptionsItemSelected(MenuItem item) {
         * switch (item.getItemId()) {
         * case android.R.id.home:
         * NavUtils.navigateUpFromSameTask(this);
         * return true;
         * }
         * return super.onOptionsItemSelected(item);
         * }*
         */

    public void iconload() {
        String[] foldernames = new String[directoryNames.size()];
        foldernames = directoryNames.toArray(foldernames);

        String[] filenames = new String[fileNames.size()];
        filenames = fileNames.toArray(filenames);

        CustomListSingleOnly adapter1 = new CustomListSingleOnly(FileSelectionActivity.this, directoryNames.toArray(foldernames), mainPath.getPath());
        CustomList adapter2 = new CustomList(FileSelectionActivity.this, fileNames.toArray(filenames), mainPath.getPath());


        MergeAdapter adap = new MergeAdapter();

        adap.addAdapter(adapter1);
        adap.addAdapter(adapter2);


        directoryView.setAdapter(adap);
    }

    public void ExtStorageSearch() {
        String[] extStorlocs = {"/storage/sdcard1", "/storage/extsdcard", "/storage/sdcard0/external_sdcard", "/mnt/extsdcard",
                "/mnt/sdcard/external_sd", "/mnt/external_sd", "/mnt/media_rw/sdcard1", "/removable/microsd", "/mnt/emmc",
                "/storage/external_SD", "/storage/ext_sd", "/storage/removable/sdcard1", "/data/sdext", "/data/sdext2",
                "/data/sdext3", "/data/sdext4", "/storage/sdcard0"};

        //First Attempt
        primary_sd = System.getenv("EXTERNAL_STORAGE");
        secondary_sd = System.getenv("SECONDARY_STORAGE");


        if (primary_sd == null) {
            primary_sd = Environment.getExternalStorageDirectory() + "";
        }
        if (secondary_sd == null) {//if fail, search among known list of extStorage Locations
            for (String string : extStorlocs) {
                if ((new File(string)).exists() && (new File(string)).isDirectory()) {
                    secondary_sd = string;
                    break;
                }
            }
        }
    }
}

