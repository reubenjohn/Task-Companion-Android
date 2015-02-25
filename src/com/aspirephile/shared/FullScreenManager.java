package com.aspirephile.shared;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.aspirephile.shared.debug.Logger;

public class FullScreenManager {
	Logger l = new Logger(FullScreenManager.class);

	Activity activity;

	public FullScreenManager(Activity activity) {
		this.activity = activity;
	}

	public FullScreenManager activateFullScreen() {
		l.d("Activating fullscreen for activity " + activity);
		if (Build.VERSION.SDK_INT < 16) {
			activateFullScreen_16lower();
		} else {
			activateFullScreen_v16();
		}
		return this;
	}

	private FullScreenManager activateFullScreen_16lower() {
		l.d("Approach for API lower that 16 is being followed");
		activity.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return this;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private FullScreenManager activateFullScreen_v16() {
		l.d("Approach for API 16 and higher is being followed");
		View decorView = activity.getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		return this;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public FullScreenManager allowContentBehindStatusBar() {
		l.d("Allowing content behind status bar");
		if (Build.VERSION.SDK_INT >= 16) {
			View decorView = activity.getWindow().getDecorView();
			decorView
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
			decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		} else
			l.e("Unable to allow content behind status bar! (API 16+ required) Current API: "
					+ Build.VERSION.SDK_INT);
		return this;
	}
}
