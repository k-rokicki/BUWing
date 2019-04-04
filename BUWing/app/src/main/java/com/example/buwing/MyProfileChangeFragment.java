package com.example.buwing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import java.util.Objects;

public class MyProfileChangeFragment extends BaseFragment {

    Button changeInfoButton, changePasswordButton, deleteAccountButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        _layout = R.layout.fragment_my_profile_change;
        title = "mój profil - edytuj";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        changeInfoButton = Objects.requireNonNull(getActivity()).findViewById(R.id.changeInfoButton);
        changePasswordButton = Objects.requireNonNull(getActivity()).findViewById(R.id.changePasswordButton);
        deleteAccountButton = Objects.requireNonNull(getActivity()).findViewById(R.id.deleteAccountButton);

        changeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyProfileChangeInfoFragment();
                FragmentTransaction ft =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                                beginTransaction().
                                setCustomAnimations
                                        (R.anim.slide_in_right, R.anim.slide_out_left);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyProfileChangePasswordFragment();
                FragmentTransaction ft =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                                beginTransaction().
                                setCustomAnimations
                                        (R.anim.slide_in_right, R.anim.slide_out_left);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new MyProfileDeleteAccountFragment();
                FragmentTransaction ft =
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                                beginTransaction().
                                setCustomAnimations
                                        (R.anim.slide_in_right, R.anim.slide_out_left);
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new MyProfileFragment();
        FragmentTransaction ft =
                Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                        beginTransaction().
                        setCustomAnimations
                                (android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

}