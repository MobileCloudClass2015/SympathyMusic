package kookmin.cs.sympathymusiz;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * Created by seojunkyo on 2015. 6. 5..
 */
public class ModeActivity extends Activity {

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

        ImageButton upload = (ImageButton)findViewById(R.id.upload);
        ImageButton recomd = (ImageButton)findViewById(R.id.recomd);
        ImageButton fb_friend = (ImageButton)findViewById(R.id.list_friend);

        upload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ModeActivity.this, FileSelectionActivity.class);
                startActivity(intent);
            }
        });
        recomd.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ModeActivity.this, ListActivity.class);
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