package com.reubenjohn.studytimer;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.reubenjohn.studytimer.timming.frametimer.FrameIntervalListener;
import com.reubenjohn.studytimer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Home extends FragmentActivity implements OnClickListener {

	private class Preferences {

		public static final boolean AUTO_HIDE = true;

		/**
		 * If {@link #Preferences.AUTO_HIDE} is set, the number of milliseconds to wait
		 * after user interaction before hiding the system UI.
		 */
		public static final int AUTO_HIDE_DELAY_MILLIS = 3000;

		/**
		 * The flags to pass to {@link SystemUiHider#getInstance}.
		 */
		public static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	}

	private SystemUiHider mSystemUiHider;
	Button toggle, lap;
	View controlsView, contentView;
	StudyTimer T;
	Handler tHandler = new Handler();

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (Preferences.AUTO_HIDE) {
				delayedHide(Preferences.AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);

		bridgeXML();
		setListeners();
		initializeFeilds();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.d("StudyTimer", "Home created");
		delayedHide(Preferences.AUTO_HIDE_DELAY_MILLIS);
	}
	

	@Override
	protected void onStop() {
		super.onStop();
		T.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		T.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		T.onPause();
	}

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

	protected void bridgeXML() {
		controlsView = findViewById(R.id.fullscreen_content_controls);
		contentView = findViewById(R.id.fullscreen_content);
		toggle = (Button) findViewById(R.id.b_toggle);
		lap = (Button) findViewById(R.id.b_lap);
	}

	protected void setListeners() {
		toggle.setOnClickListener(this);
		lap.setOnClickListener(this);
		findViewById(R.id.b_toggle).setOnTouchListener(mDelayHideTouchListener);
		contentView.setOnClickListener(this);
	}

	protected void initializeFeilds() {
		setupSystemUIHider();
		
		T = new StudyTimer(tHandler,getSupportFragmentManager());
		T.framer.setInterval(100);
		T.framer.addFrameReachListener(new FrameIntervalListener() {
			@Override
			public void OnFrameReached() {
				T.logStatus();
			}
		}, 100);
		/*
		 * LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
		 * LinearLayout.LayoutParams.WRAP_CONTENT,
		 * LinearLayout.LayoutParams.WRAP_CONTENT,1.f);
		 * params.gravity=Gravity.CENTER; params.weight=1.f;
		 * 
		 * elapse = new TimerView(Home.this,T.framer); elapse.setText("elapse");
		 * elapse.setLayoutParams(params); elapse.setTextSize(50);
		 * elapse.setTextColor(Color.GREEN);
		 * T.framer.setFrameTimerListener(elapse);
		 * 
		 * LinearLayout fullscreenContent = (LinearLayout)
		 * findViewById(R.id.fullscreen_content);
		 * fullscreenContent.addView(elapse);
		 */
	}

	protected void setupSystemUIHider() {

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				Preferences.HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							if (mControlsHeight == 0) {
								mControlsHeight = controlsView.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							controlsView
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							controlsView.setVisibility(visible ? View.VISIBLE
									: View.GONE);
						}

						if (visible && Preferences.AUTO_HIDE) {
							delayedHide(Preferences.AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_toggle:
			T.toggle();
			break;
		case R.id.b_lap:
			T.lap();
			break;
		case R.id.fullscreen_content:
			mSystemUiHider.show();
			break;

		}
	}
}
