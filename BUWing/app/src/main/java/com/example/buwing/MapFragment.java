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
import android.widget.RemoteViews;

import android.webkit.JavascriptInterface;

import android.content.Context;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;

public class MapFragment extends BaseFragment {
    WebView webview;

    TextView popupZajmij;
    Button popupButtonZajmij;
    Button popupCloseZajmij;
    FrameLayout ramkaZajmij;

    FrameLayout ramkaZwolnij;
    TextView popupZwolnij;
    Button popupButtonZwolnij;
    Button popupCloseZwolnij;

    FrameLayout ramkaZwolnijPrev;
    TextView popupZwolnijPrev;
    Button popupButtonZwolnijPrev;
    Button popupCloseZwolnijPrev;


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
        public boolean showPopupZajmij() {
            Toast.makeText(getActivity(), "Przesun mape, popupZajmij", Toast.LENGTH_SHORT).show();
            ramkaZajmij.setVisibility(View.VISIBLE);
            if (ramkaZajmij.getVisibility() == View.VISIBLE) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "ccc", Toast.LENGTH_SHORT).show();
            }

            return false;
        }

        @JavascriptInterface
        public boolean showPopupZwolnij() {
            Toast.makeText(getActivity(), "Przesun mape, popupZwolnij", Toast.LENGTH_SHORT).show();
            ramkaZwolnij.setVisibility(View.VISIBLE);
            popupZwolnij.setText("bbb");

            if (ramkaZwolnij.getVisibility() == View.VISIBLE) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "ccc", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @JavascriptInterface
        public boolean showPopupZwolnijPrev() {
            Toast.makeText(getActivity(), "Przesun mape, popupZwolnijPrev", Toast.LENGTH_SHORT).show();
            ramkaZwolnijPrev.setVisibility(View.VISIBLE);
            if (ramkaZwolnijPrev.getVisibility() == View.VISIBLE) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getActivity(), "ccc", Toast.LENGTH_SHORT).show();
            }
            return false;
        }


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
        webview.setInitialScale(70);
        webview.setWebViewClient(new NewWebViewClient());

        webview.loadUrl("file:///android_asset/first.html?login=" + login
                + "&password=" + password + "&floor=1");

        Spinner spin = (Spinner) v.findViewById(R.id.level_spinner);
        String[] levels = {"Poziom 1", "Poziom 2", "Poziom 3",};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, levels);
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

        // Popup zajmujacy
        popupButtonZajmij = (Button) v.findViewById(R.id.popupButtonZajmij);
        popupButtonZajmij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("javascript:takeTable()");
                ramkaZajmij.setVisibility(View.INVISIBLE);
            }
        });
        popupCloseZajmij = (Button) v.findViewById(R.id.popupCloseZajmij);
        popupZajmij = (TextView) v.findViewById(R.id.popupZajmij);
        ramkaZajmij = (FrameLayout) v.findViewById(R.id.ramkaZajmij);
        popupCloseZajmij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ramkaZajmij.setVisibility(View.INVISIBLE);
            }
        });

        // Popup zwalniajacy okupowany
        popupButtonZwolnij = (Button) v.findViewById(R.id.popupButtonZwolnij);
        popupButtonZwolnij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("javascript:freeTable()");
                ramkaZwolnij.setVisibility(View.INVISIBLE);
            }
        });
        ramkaZwolnij = (FrameLayout) v.findViewById(R.id.ramkaZwolnij);
        popupCloseZwolnij = (Button) v.findViewById(R.id.popupCloseZwolnij);
        popupCloseZwolnij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ramkaZwolnij.setVisibility(View.INVISIBLE);
            }
        });

        // Popup zwalniajacy poprzednio okupowany
        popupZwolnijPrev = (TextView) v.findViewById(R.id.popupZwolnijPrev);
        popupButtonZwolnijPrev = (Button) v.findViewById(R.id.popupButtonZwolnijPrev);
        popupButtonZwolnijPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("javascript:freeTable()");
                ramkaZwolnijPrev.setVisibility(View.INVISIBLE);
            }
        });
        ramkaZwolnijPrev = (FrameLayout) v.findViewById(R.id.ramkaZwolnijPrev);
        popupCloseZwolnijPrev = (Button) v.findViewById(R.id.popupCloseZwolnijPrev);
        popupCloseZwolnijPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ramkaZwolnijPrev.setVisibility(View.INVISIBLE);
            }
        });


        return v;
    }
}