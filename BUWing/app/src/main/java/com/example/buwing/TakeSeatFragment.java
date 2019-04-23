package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

//import static com.example.buwing.MainActivity.seatTaken;

public class TakeSeatFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /*
        MainScreenFragment.CheckSeatTakenTask checkSeatTakenTask = new MainScreenFragment.CheckSeatTakenTask();
        checkSeatTakenTask.execute();
        if (!seatTaken) {
            _layout = R.layout.fragment_take_seat;
        } else {
            _layout = R.layout.fragment_seat_taken;
        }
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState); */

        super.onCreate(savedInstanceState);
        _layout = R.layout.fragment_take_seat;
    }

    public class NewWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_take_seat, container, false);
        WebView webview = (WebView) v.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new NewWebViewClient());
        webview.loadUrl("file:///android_asset/main.html");
        return v;
    }
}