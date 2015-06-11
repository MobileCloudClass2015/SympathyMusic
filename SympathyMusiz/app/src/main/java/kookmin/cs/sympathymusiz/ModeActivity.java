package kookmin.cs.sympathymusiz;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by seojunkyo on 2015. 6. 5..
 */
public class ModeActivity extends ActionBarActivity {

    ListView list;
    DbOpenHelper dbHelper;
    SQLiteDatabase db;
    String sql;
    Cursor cursor;

    final static String dbName = "mode.db";
    final static int dbVersion = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode);

        Button upload = (Button)findViewById(R.id.upload);
        Button recomd = (Button)findViewById(R.id.recomd);
        Button video = (Button)findViewById(R.id.videobtn);

        upload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FileSelectionActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        recomd.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ModeActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        video.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ModeActivity.this, VideoListDemoActivity.class);
                startActivity(intent);
            }
        });


    }

    private void selectDB(){
        db = dbHelper.getWritableDatabase();
        sql = "SELECT * FROM test;";

        cursor = db.rawQuery(sql, null);
        if(cursor.getCount() > 0){
            startManagingCursor(cursor);
            DBAdapter dbAdapter = new DBAdapter(this, cursor);
            list.setAdapter(dbAdapter);
        }
    }
}