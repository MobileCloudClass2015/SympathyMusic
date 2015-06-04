package kookmin.cs.sympathymusiz;

/**
 * Created by shinjiung on 15. 5. 7..
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shinjiung on 15. 5. 5..
 */
public class DbOpenHelper extends SQLiteOpenHelper {
    String sql;

    public DbOpenHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        sql = "CREATE TABLE test ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT);";
        db.execSQL(sql);


    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //버전이 업그레이드 됐을 경우 작업할 내용을 작성합니다.
    }

}