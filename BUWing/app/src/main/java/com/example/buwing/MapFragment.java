package com.example.buwing;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.webkit.JavascriptInterface;

import android.content.Context;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;

public class MapFragment extends BaseFragment {
    WebView webview;
    TextView popup;
    Button popupButton;
    FrameLayout ramka;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        /*
        MainScreenFragment.CheckSeatTakenTask checkSeatTakenTask = new MainScreenFragment.CheckSeatTakenTask();
        checkSeatTakenTask.execute();
        if (!seatTaken) {
            _layout = R.layout.fragment_map;
        } else {
            _layout = R.layout.fragment_seat_taken;
        }
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState); */

        super.onCreate(savedInstanceState);
        _layout = R.layout.fragment_map;
    }

    public class NewWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public class WebAppInterface {
        @JavascriptInterface

        public int getWinHeight() {
            return webview.getMeasuredWidthAndState();
        }

        @JavascriptInterface
        public int getWinScrollX() {
            return webview.getScrollX();
        }

        @JavascriptInterface
        public int getWinScrollY() {
            return webview.getScrollY();
        }

        @JavascriptInterface
        public boolean showPopup() {
            popups();
            return false;
        }


    }

    public void popups() {
      //  webview.setVisibility(View.INVISIBLE);
      //  webview.zoomOut();
      //  webview.setsc
      //  popup.setVisibility(View.VISIBLE);
      //  popup.setVisibility(View.VISIBLE);
       // popupButton.setVisibility(View.VISIBLE);
        ramka.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //WebView.setDataDirectorySuffix("dir_name_no_separator");
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        webview = (WebView) v.findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.addJavascriptInterface(new WebAppInterface(), "Android");
       // webview.getSettings().setUserAgentString("Chrome");
       // Toast.makeText(getActivity(), webview.getSettings().getUserAgentString(), Toast.LENGTH_SHORT).show();
        webview.setInitialScale(70);
        webview.setWebViewClient(new NewWebViewClient());
        webview.loadUrl("file:///android_asset/first.html?login=" + login
                + "&password=" + password + "&floor=1");
        Spinner spin = (Spinner) v.findViewById(R.id.level_spinner);
        String[] levels = {"Poziom 1", "Poziom 2", "Poziom 3",};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, levels);
        //adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (levels[position].equals("Poziom 1")) {
                    webview.loadUrl("file:///android_asset/first.html?login=" +
                            login + "&password=" + password + "&floor=1");
                }
                else if (levels[position].equals("Poziom 2")) {
                    webview.loadUrl("file:///android_asset/second.html?login=" +
                            login + "&password=" + password + "&floor=2");
                }
                else {
                    webview.loadUrl("file:///android_asset/third.html?login=" +
                            login + "&password=" + password + "&floor=3");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spin.setAdapter(adapter);

        Button refresh = (Button) v.findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.reload();
            }
        });

    //    popupButton = (Button) v.findViewById(R.id.popupButton);
      //  popupButton.setVisibility(View.INVISIBLE);
        popup = (TextView) v.findViewById(R.id.popup);
      //  popup.setVisibility(View.VISIGOBLE);
        ramka = (FrameLayout) v.findViewById(R.id.ramka);
        ramka.setVisibility(View.INVISIBLE);



        return v;
    }
}