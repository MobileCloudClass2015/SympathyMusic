package kookmin.cs.sympathymusiz;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by seojunkyo on 2015. 6. 4..
 */
public class ListActivity extends FragmentActivity implements View.OnClickListener{
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.play_list);




    }

    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
