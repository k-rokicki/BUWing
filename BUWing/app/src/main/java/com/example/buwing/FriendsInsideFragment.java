package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class FriendsInsideFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_friends_inside;
        title = "znajomi w BUW";
        super.onCreate(savedInstanceState);
    }

}