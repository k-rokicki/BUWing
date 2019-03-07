package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class MyProfileFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile;
        title = "mój profil";
        super.onCreate(savedInstanceState);
    }

}