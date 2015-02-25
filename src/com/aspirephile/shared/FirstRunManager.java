package com.aspirephile.shared;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.aspirephile.shared.debug.Logger;

public class FirstRunManager {
	Logger l = new Logger(FirstRunManager.class);

	public static class defaults {
		public static final boolean isfirstRun = true;
	}

	private final Runnable launchNextActivity = new Runnable() {

		@Override
		public void run() {
			launchNextActivity();
		}
	};

	private Activity activity;

	private String booleanKey;
	private String sharedPrefsName;

	public class RunConfig {
		public Class<?> launchActivity;
		public int requestCode;
		public int enterAnim;
		public int exitAnim;

		public RunConfig() {
			launchActivity = null;
			requestCode = 0;
			enterAnim = 0;
			exitAnim = 0;
		}

		public boolean isLaunchConfigurationValid() {
			return ((launchActivity != null) && (requestCode != 0));
		}

		public boolean isAnimationConfigurationValid() {
			return (enterAnim > 0) && (exitAnim > 0);
		}

		public RunConfig setLaunchActivity(Class<?> cls) {
			launchActivity = cls;
			return RunConfig.this;
		}

		public RunConfig setRequestCode(int requestCode) {
			this.requestCode = requestCode;
			return RunConfig.this;
		}
		public RunConfig setTransitions(int enterAnim, int exitAnim) {
			this.enterAnim = enterAnim;
			this.exitAnim = exitAnim;
			return RunConfig.this;
		}

		@Override
		public String toString() {
			return "RunConfig("
					+ (isLaunchConfigurationValid() == true ? "Valid"
							: "Invalid")
					+ "){ "
					+ "Launch activity: "
					+ launchActivity.getName()
					+ ", "
					+ "request code: "
					+ requestCode
					+ ", "
					+ "Animations("
					+ (isAnimationConfigurationValid() == true ? "Valid"
							: "Invalid") + "){ " + "Enter: " + enterAnim + ", "
					+ "Exit: " + exitAnim + " } }";
		}


	}

	public RunConfig firstRunConfig, subsequentRunConfig;

	SharedPreferences prefs;

	public FirstRunManager(FragmentActivity activity) {
		this.activity = activity;
		prefs = this.activity.getSharedPreferences(sharedPrefsName,
				Context.MODE_PRIVATE);
		firstRunConfig = new RunConfig();
		subsequentRunConfig = new RunConfig();
	}

	public boolean isFirstRun() {
		return prefs.getBoolean(booleanKey, defaults.isfirstRun);
	}

	public FirstRunManager setSharedPrefsName(String sharedPrefsName) {
		this.sharedPrefsName = sharedPrefsName;
		return this;
	}

	public FirstRunManager setBooleanKey(String booleanKey) {
		this.booleanKey = booleanKey;
		return this;
	}

	public FirstRunManager launchNextActivity() {
		RunConfig runConfig;
		boolean isFirstRun = isFirstRun();
		// Setup appropriate launch configurations
		if (isFirstRun) {
			l.i("Initiating first run protocol");
			runConfig = firstRunConfig;
		} else {

			l.i("Previous run detected. Skipping first run protocol");
			runConfig = subsequentRunConfig;
		}
		// Validate and start activity
		if (runConfig.isLaunchConfigurationValid()) {
			Intent i = new Intent(activity, runConfig.launchActivity);
			l.d(activity + " launching next activity: "
					+ runConfig.launchActivity + "(" + i + ")");
			activity.startActivityForResult(i, runConfig.requestCode);
		} else {
			l.e("Invalid run configurations: " + runConfig);
			// Validate and override pending transition
			if (runConfig.isAnimationConfigurationValid()) {
				activity.overridePendingTransition(runConfig.enterAnim,
						runConfig.exitAnim);
			} else
				l.w("Invalid animation configurations: " + runConfig);
			// TODO Throw appropriate exceptions describing error (maybe with
			// launch
			// configurations)
		}
		// TODO Throw appropriate exceptions describing error (maybe with launch
		// configurations)

		// Lastly finish root activity to prevent return to it on back button
		// press from the subsequent activity
		if (!isFirstRun) {
			activity.finish();
		}
		return this;
	}

	public FirstRunManager scheduleNextActivity(int splashscreenduration) {
		l.d("Scheduling next activity launch for " + splashscreenduration
				+ "ms delay");
		new Handler().postDelayed(launchNextActivity, splashscreenduration);
		return this;
	}

	public void sendFirstRunCompletionResult(boolean isFirstRunSuccessful) {
		l.d("Received message: First run successful: " + isFirstRunSuccessful);
		if (isFirstRunSuccessful) {
			l.d("Marking indicator of successful first run");
			prefs.edit().putBoolean(booleanKey, !isFirstRunSuccessful).commit();
		}
		launchNextActivity();
	}

}
