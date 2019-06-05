package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.Nullable;

import static com.example.buwing.MainActivity.seatTaken;

public class TakeSeatFragment extends BaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MainScreenFragment.CheckSeatTakenTask checkSeatTakenTask = new MainScreenFragment.CheckSeatTakenTask();
        checkSeatTakenTask.execute();
        if (!seatTaken) {
            _layout = R.layout.fragment_take_seat;
        } else {
            _layout = R.layout.fragment_seat_taken;
        }
        title = "zajmij miejsce";
        super.onCreate(savedInstanceState);
    }

}
