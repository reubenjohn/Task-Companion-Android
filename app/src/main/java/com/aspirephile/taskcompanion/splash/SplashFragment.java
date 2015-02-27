package com.aspirephile.taskcompanion.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspirephile.shared.FirstRunManager;
import com.aspirephile.taskcompanion.Home;
import com.aspirephile.taskcompanion.R;
import com.aspirephile.taskcompanion.StudyTimer;
import com.aspirephile.taskcompanion.welcome.Welcome;

public class SplashFragment extends Fragment {

    FirstRunManager firstRunManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_splash, container, false);
        initializeFields();
        return v;
    }

    private void initializeFields() {
        firstRunManager = new FirstRunManager(getActivity());
        firstRunManager.firstRunConfig.setLaunchActivity(Welcome.class)
                .setRequestCode(StudyTimer.codes.request.welcome)
                .setTransitions(R.anim.slide_in, R.anim.slide_out);
        firstRunManager.subsequentRunConfig.setLaunchActivity(Home.class)
                .setRequestCode(StudyTimer.codes.request.home)
                .setTransitions(R.anim.slide_in, R.anim.slide_out);
        firstRunManager.setBooleanKey(StudyTimer.keys.prefs.firstRun)
                .setSharedPrefsName(StudyTimer.files.appPrefs);
    }

    @Override
    public void onResume() {
        firstRunManager
                .scheduleNextActivity(StudyTimer.props.splashScreenDuration);
        super.onResume();
    }

    public void sendFirstRunCompletionResult(boolean isFirstRunSuccessful) {
        firstRunManager.sendFirstRunCompletionResult(isFirstRunSuccessful);
    }
}
