package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class LogoutFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_logout;
        title = "wyloguj";
        super.onCreate(savedInstanceState);
    }

}