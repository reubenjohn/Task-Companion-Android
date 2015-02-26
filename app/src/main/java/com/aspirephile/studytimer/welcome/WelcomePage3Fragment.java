package com.aspirephile.studytimer.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspirephile.studytimer.R;

public class WelcomePage3Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null)
            return null;
        View v = inflater.inflate(R.layout.fragment_welcome_page_3, container,
                false);
        initializeFeilds();
        return v;
    }

    private void initializeFeilds() {
    }

}