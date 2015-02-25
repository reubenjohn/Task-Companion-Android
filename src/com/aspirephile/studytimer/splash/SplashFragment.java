package com.aspirephile.studytimer.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspirephile.shared.FirstRunManager;
import com.aspirephile.studytimer.Home;
import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.StudyTimer;
import com.aspirephile.studytimer.welcome.Welcome;

public class SplashFragment extends Fragment {

	FirstRunManager firstRunManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_splash, container, false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	private void bridgeXML(View v) {
		// TODO Auto-generated method stub

	}

	private void initializeFeilds() {
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
