package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class FriendsListFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_friends_list;
        title = "lista znajomych";
        super.onCreate(savedInstanceState);
    }
}
