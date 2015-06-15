package kookmin.cs.sympathymusiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by seojunkyo on 2015. 6. 4..
 */
public class ListActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_list);


        ImageButton upload = (ImageButton)findViewById(R.id.upload);


        upload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FileSelectionActivity.class);
                startActivityForResult(intent, 0);
            }
        });

    }
}
