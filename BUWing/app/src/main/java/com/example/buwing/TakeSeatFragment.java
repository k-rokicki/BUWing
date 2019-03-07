package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class TakeSeatFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_take_seat;
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState);
    }

}