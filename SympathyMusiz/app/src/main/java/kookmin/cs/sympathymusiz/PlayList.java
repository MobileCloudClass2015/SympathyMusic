package kookmin.cs.sympathymusiz;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class PlayList extends ListActivity {

	private EditText et;	// Variable for the search function
	int textlength = 0;		// Variable for the search function

	ArrayList<String> tempList = new ArrayList<String>();	//Temporary array for initializing the search function

	private TextView layoutTitle;
	private ListView list;
	private String[] files;

	final MyAdapter Adapter = new MyAdapter(this);	// Adapter for managing a list

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String receive  = intent.getExtras().getString("play");

		try {
			list = (ListView) findViewById(android.R.id.list);


			JSONObject object = null;

			object = new JSONObject(receive);
			JSONArray Array = null;
			try {
				Array = new JSONArray(object.getString("tracks"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			for (int j=0; j< Array.length(); j++)
			{
				Array.getString(j);
				JSONObject insideObject = null;


				try {
					insideObject = Array.getJSONObject(j);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				try {
					String title = insideObject.getString("user_id");

					Adapter.addItem(title);

					list.setAdapter(Adapter);


				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}


		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filelist);

		ImageButton homebtn;	//��ü ����.
        homebtn=(ImageButton)findViewById(R.id.home); //����

        homebtn.setOnClickListener(new ImageButton.OnClickListener() {// ��ü�� Ŭ�� �Ǹ�.
			public void onClick(View v) {    //onClick �Լ�.
				Intent intent = new Intent(PlayList.this, MainActivity.class);    //MainActivity ��(this) sub�� .
				intent.putExtra("splash", "splash");
				startActivity(intent);
			}
		});


					list.setAdapter(Adapter);

					list.setTextFilterEnabled(true);


			//Click event
			list.setOnItemClickListener(mItemClickListener);

	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			// �׸� �����̸� �޾ƿ���
			String mes;
			// Copy the selected string in a list
			mes = Adapter.getArr().get(position);
			// Call CopyReadAssets function

			new AlertDialog.Builder(PlayList.this)
					.setTitle("친구추천")
					.setMessage("비슷한 음악적 취향을 가진 친구를 추천해줍니다.")
					.setPositiveButton("PlayList", new DialogInterface.OnClickListener()
					{
						//move to notification setting page
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							RequestParams params = new RequestParams();
							params.put("user_id", "1234@naver.com");

							AsyncHttpClient client = new AsyncHttpClient();
							client.post("http://52.68.143.225:9000/Sympathy/music/upload/", params, new AsyncHttpResponseHandler() {
								@Override
								public void onSuccess(int i, Header[] headers, byte[] bytes) {
									try {
										list = (ListView) findViewById(android.R.id.list);

										String result = new String(bytes, "UTF-8");
										Log.d("print", result);



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
					})
					.setNegativeButton("친구신청", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{

							dialog.dismiss();
						}
					})
					.show();



		}
	};


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
