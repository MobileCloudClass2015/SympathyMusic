package kookmin.cs.sympathymusiz.Friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kookmin.cs.sympathymusiz.R;

/**
 * Created by seojunkyo on 2015. 3. 18..
 */
public class friendslistFragment extends Fragment {

    View view;
    WebView infoWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_list_fragment, container, false);

        infoWeb = (WebView) view.findViewById(R.id.fragment_frlist);
        infoWeb.getSettings().setJavaScriptEnabled(true);
        infoWeb.setWebViewClient(new webClient());
        infoWeb.loadUrl("http://m.naver.com");

        return view;
    }


    private class webClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
}