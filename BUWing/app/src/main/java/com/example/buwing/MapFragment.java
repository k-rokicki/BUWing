package com.example.buwing;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
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
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;

import static com.example.buwing.MainActivity.login;
import static com.example.buwing.MainActivity.password;
import static com.example.buwing.MainActivity.seatTaken;
import static com.example.buwing.MainActivity.takenSeatFloor;
import static com.example.buwing.MainActivity.takenSeatId;
import static com.example.buwing.MainScreenFragment.takeSeatMenuItemString;
import static java.util.Objects.requireNonNull;

import static com.example.buwing.MainScreenFragment.takeSeatFreeMenuItemString;
import static com.example.buwing.MainScreenFragment.takeSeatTakenMenuItemString;

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

        _layout = R.layout.fragment_map;
        title = "mapa BUW";
        super.onCreate(savedInstanceState);
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
        public void releasedSuccess() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Pomyślnie zwolniono miejsce", Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void tryAgain() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Spróbuj ponownie", Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void takenSuccess() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Pomyślnie zajęto miejsce", Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void takenFail() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Miejsce już zajęte", Toast.LENGTH_LONG).show();
                }
            });
        }

        @JavascriptInterface
        public void showPopupZajmij() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZajmij.setVisibility(View.VISIBLE);
                    disableMap();
                }
            });

        }

        @JavascriptInterface
        public void showPopupZwolnij() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZwolnij.setVisibility(View.VISIBLE);
                    disableMap();
                }
            });
        }

        @JavascriptInterface
        public void showPopupZwolnijPrev() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ramkaZwolnijPrev.setVisibility(View.VISIBLE);
                    disableMap();
                }
            });

        }

        @JavascriptInterface
        public void changeSeatTaken(boolean seatT, String takenStId, String takenStFloor) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seatTaken = seatT;
                    takenSeatId = Integer.valueOf(takenStId).intValue();
                    takenSeatFloor = Integer.valueOf(takenStFloor).intValue();

                    if (seatTaken) {
                        takeSeatMenuItemString = takeSeatTakenMenuItemString;
                    }
                    else {
                        takeSeatMenuItemString = takeSeatFreeMenuItemString;
                    }
                    LoggedInActivity.updateTakeSeatMenuItem();
                }
            });

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() { closePopups(); }
                });
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
                webview.reload();
            }
        });

        // Popup zajmujacy
        popupButtonZajmij = (Button) v.findViewById(R.id.popupButtonZajmij);
        popupButtonZajmij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("javascript:takeTable()");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
            }
        });
        popupCloseZajmij = (Button) v.findViewById(R.id.popupCloseZajmij);
        popupZajmij = (TextView) v.findViewById(R.id.popupZajmij);
        ramkaZajmij = (FrameLayout) v.findViewById(R.id.ramkaZajmij);
        popupCloseZajmij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
            }
        });

        // Popup zwalniajacy okupowany
        popupButtonZwolnij = (Button) v.findViewById(R.id.popupButtonZwolnij);
        popupButtonZwolnij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("javascript:freeTable()");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
            }
        });
        ramkaZwolnij = (FrameLayout) v.findViewById(R.id.ramkaZwolnij);
        popupCloseZwolnij = (Button) v.findViewById(R.id.popupCloseZwolnij);
        popupCloseZwolnij.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
            }
        });

        // Popup zwalniajacy poprzednio okupowany
        popupZwolnijPrev = (TextView) v.findViewById(R.id.popupZwolnijPrev);
        popupButtonZwolnijPrev = (Button) v.findViewById(R.id.popupButtonZwolnijPrev);
        popupButtonZwolnijPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                webview.loadUrl("javascript:freeTable()");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
            }
        });
        ramkaZwolnijPrev = (FrameLayout) v.findViewById(R.id.ramkaZwolnijPrev);
        popupCloseZwolnijPrev = (Button) v.findViewById(R.id.popupCloseZwolnijPrev);
        popupCloseZwolnijPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ramkaZwolnijPrev.setVisibility(View.GONE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closePopups();
                    }
                });
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireNonNull(getActivity()).setTitle("mapa");
    }

    public void closePopups() {
        enableMap();
        ramkaZwolnijPrev.setVisibility(View.INVISIBLE);
        ramkaZwolnij.setVisibility(View.INVISIBLE);
        ramkaZajmij.setVisibility(View.INVISIBLE);
    }

    public void disableMap() {
        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    public void enableMap() {
        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }
}