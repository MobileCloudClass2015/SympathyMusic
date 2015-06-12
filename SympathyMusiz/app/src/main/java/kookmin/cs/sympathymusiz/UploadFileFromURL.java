package kookmin.cs.sympathymusiz;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UploadFileFromURL extends AsyncTask<String, Integer, String>
		implements ProgressUpdateCallback {
	UploadFileCallback callback;
	// constant value
	private static final String ACTIVITY_TAG = "UPLOAD";

	// JSON element ids from repsonse of php script:
	private static final String JSON_TAG_SUCCESS = "success";
	private static final String JSON_TAG_MESSAGE = "message";

	//
	private JSONParser jsonParser;

	public UploadFileFromURL(UploadFileCallback callback) {
		this.callback = callback;

		jsonParser = new JSONParser(UploadFileFromURL.this);
	}

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		callback.onUploadFilePreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// TODO Auto-generated method stub
		callback.onUploadFileProgressUpdate(values[0]);
	}

	@Override
	protected String doInBackground(String... args) {
		// TODO Auto-generated method stub
		int success;
		String filePath = args[1];
		String fileName = args[2];

        String[] arr = fileName.split("/");
        Log.d(ACTIVITY_TAG, arr[4]);
		File sourceFile = new File(filePath);

		if (!sourceFile.isFile()) {
			String strMessage = "Source File not exist :" + filePath + fileName;
			Log.e("uploadFile", strMessage);
			return strMessage;
		}

		JSONObject json = jsonParser.makeHttpRequest(args[0], sourceFile,
				arr[4]);
		if (json == null) {
			return "No data or connection failure";
		}
		try {
			Log.d(ACTIVITY_TAG, "Upload attempt : " + json.toString());
			success = json.getInt(JSON_TAG_SUCCESS);
			if (success == 1) {
				Log.d(ACTIVITY_TAG, "Upload Successful : " + json.toString());

				return json.getString(JSON_TAG_MESSAGE);
			} else {
				Log.d(ACTIVITY_TAG,
						"Upload Failure : " + json.getString(JSON_TAG_MESSAGE));
				return json.getString(JSON_TAG_MESSAGE);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "Source File exist :" + filePath + fileName;
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		callback.doUploadFilePostExecute(result);
	}

	/**
	 * Callback of ProgressUpdate, set progress update status from
	 * ProgressOutHttpEntity
	 * **/
	@Override
	public void setProgressUpdateStatus(int value) {
		// TODO Auto-generated method stub
		publishProgress(value);
	}
}
