package kookmin.cs.sympathymusiz.Friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import kookmin.cs.sympathymusiz.R;

/**
 * Created by se    ojunkyo on 2015. 6. 4..
 */
public class friendsActivity extends FragmentActivity implements View.OnClickListener{
    final String TAG = "friendsActivity";

    int mCurrentFragmentIndex;
    public final static int FRAGMENT_ONE = 0;
    public final static int FRAGMENT_TWO = 1;
    public final static int FRAGMENT_THREE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

        ImageButton bt_frrecomd = (ImageButton) findViewById(R.id.friends_recomd);
        bt_frrecomd.setOnClickListener(this);
        ImageButton bt_frlist = (ImageButton) findViewById(R.id.friends_list);
        bt_frlist.setOnClickListener(this);
        ImageButton bt_fr_facebook = (ImageButton) findViewById(R.id.friends_fb);
        bt_fr_facebook.setOnClickListener(this);

        mCurrentFragmentIndex = FRAGMENT_ONE;

        fragmentReplace(mCurrentFragmentIndex);
    }

    public void fragmentReplace(int reqNewFragmentIndex) {

        Fragment newFragment = null;
        Log.d(TAG, "fragmentReplace " + reqNewFragmentIndex);
        newFragment = getFragment(reqNewFragmentIndex);

        // replace fragment
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_main, newFragment);

        // Commit the transaction
        transaction.commit();

    }
    private Fragment getFragment(int idx) {
        Fragment newFragment = null;

        switch (idx) {
            case FRAGMENT_ONE:
                newFragment = new friendsrecomdFragment();
                break;
            case FRAGMENT_TWO:
                newFragment = new friendslistFragment();
                break;
            case FRAGMENT_THREE:
                newFragment = new FbInviteFragment();
                break;
            default:
                Log.d(TAG, "Unhandle case");
                break;
        }
        return newFragment;
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friends_recomd:
                mCurrentFragmentIndex = FRAGMENT_ONE;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.friends_list:
                mCurrentFragmentIndex = FRAGMENT_TWO;
                fragmentReplace(mCurrentFragmentIndex);
                break;
            case R.id.friends_fb:
                mCurrentFragmentIndex = FRAGMENT_THREE;
                fragmentReplace(mCurrentFragmentIndex);
                break;
        }
    }
}