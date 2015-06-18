package kookmin.cs.sympathymusiz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by seojunkyo on 2015. 6. 5..
 */
public class ModeActivity extends Activity {

    ListView list;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mode);

        ImageButton upload = (ImageButton)findViewById(R.id.upload);
        ImageButton recomd = (ImageButton)findViewById(R.id.recomd);
        ImageButton account = (ImageButton)findViewById(R.id.account);
        ImageButton menu = (ImageButton)findViewById(R.id.menu);


        upload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload, 1);


                /*
                Intent intent = new Intent(getBaseContext(), FileSelectionActivity.class);
                startActivityForResult(intent, 0);
                */
            }
        });
        recomd.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ModeActivity.this, VideoListDemoActivity.class);
                startActivity(intent);
            }
        });
        account.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1){

            if(resultCode == RESULT_OK){
                /*
                Uri uri=getIntent().getData();
                String fileName = null;
                Context context=getApplicationContext();
                String scheme = uri.getScheme();
                if (scheme.equals("file")) {
                    fileName = uri.getLastPathSegment();
                }
                else if (scheme.equals("content")) {
                    String[] proj = { MediaStore.Audio.Artists.ARTIST };
                    Uri contentUri = null;
                    Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
                    if (cursor != null && cursor.getCount() != 0) {
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST);
                        cursor.moveToFirst();
                        fileName = cursor.getString(columnIndex);
                        Log.d("test", uri.toString());
                    }
                }
                */
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                if (cursor.moveToFirst()) {
                    uri = Uri.parse(cursor.getString(0));
                }

                File myFile = new File(uri.getPath());
                myFile.getAbsolutePath();


                //the selected audio.
                //Uri uri = data.getData();
                Log.d("test", myFile.toString());
                uploadFile(uri.getPath().toString());


                //Intent intent = new Intent(ModeActivity.this, VideoListDemoActivity.class);
                //startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadFile(String filepath){

        /*
        byte[] myByteArray = blah;
        RequestParams params = new RequestParams();
        params.put("soundtrack", new ByteArrayInputStream(myByteArray), "she-wolf.mp3");

        */
        File myFile = new File(filepath);


        RequestParams params = new RequestParams();
        try {
            params.put("file", myFile);

            params.put("user_id","1234@naver.com");
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





        /*
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String pathToOurFile = filepath;
        String urlServer = "http://52.68.143.225:9000/Sympathy/music/upload/";
        String lineEnd = "\r\n";
        byte[] buffer;
        String twoHyphens = "--";
        String boundary =  "*****";

        int bytesRead, bytesAvailable, bufferSize;
        int maxBufferSize = 1*1024*1024;

        try
        {
            Log.d("test", "file upload"+filepath);
            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
            Log.d("test","loop1");
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            Log.d("test", connection.toString());
            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            Log.d("test", "loop3");
            // Set HTTP method to POST.
            connection.setRequestMethod("POST");
            Log.d("test", "loop4");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            connection.setRequestProperty("uploaded_file", pathToOurFile);
            Log.d("test", "loop5");
            try {
                outputStream = new DataOutputStream(connection.getOutputStream());
            }catch (Exception e){
                Log.d("test", e.getMessage());
            }
            Log.d("test","loop6");
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            Log.d("test", "loop7");
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + pathToOurFile + "\"" + lineEnd);
            Log.d("test", "loop8");
            outputStream.writeBytes(lineEnd);
            Log.d("test", "loop6");
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            Log.d("test","loop7");
            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            Log.d("test","loop8");
            while (bytesRead > 0)
            {
                Log.d("test","loop");
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            Log.d("test", serverResponseCode + "     " + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception ex)
        {
            //Exception handling
        }*/

    }

}