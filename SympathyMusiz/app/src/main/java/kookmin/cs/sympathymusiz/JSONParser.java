package kookmin.cs.sympathymusiz;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONParser {
	private static final String UPLOAD_DIRECTORY = "upload";
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	private long totalSize;
	// constructor
	ProgressUpdateCallback progressUpdate;

	public JSONParser(ProgressUpdateCallback progressUpdate) {
		this.progressUpdate = progressUpdate;
	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public JSONObject makeHttpRequest(String url, File file, String fileName) {

		// Making HTTP request
		try {
			// request method is POST
			DefaultHttpClient httpClient = new DefaultHttpClient();

			// http post method, set params by setEntity
			HttpPost httpPost = new HttpPost(url);

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addBinaryBody(UPLOAD_DIRECTORY, file,
					ContentType.DEFAULT_BINARY, fileName);
			HttpEntity entity = builder.build();
			totalSize = entity.getContentLength();
			ProgressOutHttpEntity progressEntity = new ProgressOutHttpEntity(
					entity, new ProgressListenerCallback() {
						@Override
						public void transferred(long transferedBytes) {
							progressUpdate
									.setProgressUpdateStatus((int) (100 * transferedBytes / totalSize));
						}
					});
			
			httpPost.setEntity(progressEntity);

			HttpResponse httpResponse = httpClient.execute(httpPost);

			// Http connect success.
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			} else {
				return null;
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
            Log.d("testklsdjflksjdfl", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}
