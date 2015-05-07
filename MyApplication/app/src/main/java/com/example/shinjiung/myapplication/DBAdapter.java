package com.example.shinjiung.myapplication;

/**
 * Created by shinjiung on 15. 5. 7..
 */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.shinjiung.myapplication.R;

/**
 * Created by shinjiung on 15. 5. 7..
 */
public class DBAdapter extends CursorAdapter {
    public DBAdapter(Context context, Cursor c) {
        super(context, c);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final TextView name = (TextView)view.findViewById(R.id.mode_text);


        name.setText(cursor.getString(cursor.getColumnIndex("name")));

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.mode_item, parent, false);
        return v;
    }

}