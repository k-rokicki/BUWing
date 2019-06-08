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
import android.view.MotionEvent;
import android.os.SystemClock;
import android.os.Handler;
import android.view.View.OnTouchListener;
import android.net.Uri;
import android.webkit.JavascriptInterface;



import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;

public class MapFragment extends BaseFragment {
    View v;
    WebView webview;
    Button refresh;

    TextView popupZajmij;
    Button popupButtonZajmij;
    Button popupCloseZajmij;
    FrameLayout ramkaZajmij;

    FrameLayout ramkaZwolnij;
    Button popupButtonZwolnij;
    Button popupCloseZwolnij;

    FrameLayout ramkaZwolnijPrev;
    TextView popupZwolnijPrev;
    Button popupButtonZwolnijPrev;
    Button popupCloseZwolnijPrev;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

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
        public void hide() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZajmij.setVisibility(View.VISIBLE);
                }
            });
        }

        @JavascriptInterface
        public void showPopupZajmij() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZajmij.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "popupZajmij", Toast.LENGTH_SHORT).show();

                }
            });

        }

        @JavascriptInterface
        public void showPopupZwolnij() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZwolnij.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "popupZwolnij", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void showPopupZwolnijPrev() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZwolnijPrev.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "popupZwolnijPrev", Toast.LENGTH_SHORT).show();
                }
            });

        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //WebView.setDataDirectorySuffix("dir_name_no_separator");
        v = inflater.inflate(R.layout.fragment_map, container, false);
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

        refresh = (Button) v.findViewById(R.id.refresh_button);
        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.reload();
            }
        });

        // Popup zajmujacy
        popupButtonZajmij = (Button) v.findViewById(R.id.popupButtonZajmij);
        popupButtonZajmij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();

                webview.loadUrl("javascript:takeTable()");
                ramkaZajmij.setVisibility(View.GONE);
            }
        });
        popupCloseZajmij = (Button) v.findViewById(R.id.popupCloseZajmij);
        popupZajmij = (TextView) v.findViewById(R.id.popupZajmij);
        ramkaZajmij = (FrameLayout) v.findViewById(R.id.ramkaZajmij);
        popupCloseZajmij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();

                ramkaZajmij.setVisibility(View.GONE);
            }
        });

        // Popup zwalniajacy okupowany
        popupButtonZwolnij = (Button) v.findViewById(R.id.popupButtonZwolnij);
        popupButtonZwolnij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();

                webview.loadUrl("javascript:freeTable()");
                ramkaZwolnij.setVisibility(View.GONE);
            }
        });
        ramkaZwolnij = (FrameLayout) v.findViewById(R.id.ramkaZwolnij);
        popupCloseZwolnij = (Button) v.findViewById(R.id.popupCloseZwolnij);
        popupCloseZwolnij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();

                ramkaZwolnij.setVisibility(View.GONE);
            }
        });

        // Popup zwalniajacy poprzednio okupowany
        popupZwolnijPrev = (TextView) v.findViewById(R.id.popupZwolnijPrev);
        popupButtonZwolnijPrev = (Button) v.findViewById(R.id.popupButtonZwolnijPrev);
        popupButtonZwolnijPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();

                webview.loadUrl("javascript:freeTable()");
                ramkaZwolnijPrev.setVisibility(View.GONE);
            }
        });
        ramkaZwolnijPrev = (FrameLayout) v.findViewById(R.id.ramkaZwolnijPrev);
        popupCloseZwolnijPrev = (Button) v.findViewById(R.id.popupCloseZwolnijPrev);
        popupCloseZwolnijPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getActivity(), "View.VISIBLE", Toast.LENGTH_SHORT).show();

                ramkaZwolnijPrev.setVisibility(View.GONE);
            }
        });

        return v;
    }

}