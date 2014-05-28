package com.reubenjohn.studytimer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Home extends ActionBarActivity implements OnClickListener, OnCheckedChangeListener {

	private class Preferences {

		public static final boolean AUTO_HIDE = false;

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
	ToggleButton toggle;
	Button lap;
	View controlsView, contentView;
	StudyTimer T;
	Handler tHandler = new Handler();
	Boolean isLargeLayoutBoolean = false, resetRequested;

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

	private static class keys{
		static final int resetSession=10;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);

		bridgeXML();
		setListeners();
		initializeFeilds();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.d("StudyTimer", "Home created");
		if (Preferences.AUTO_HIDE)
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

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean requestReset = prefs.getBoolean("key_pref_data_reset_stats",
				false);
		Log.d("StudyTimer", "resumed Home: requestReset=" + requestReset);
		T.onResume(requestReset);
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
		toggle = (ToggleButton) findViewById(R.id.b_toggle);
		lap = (Button) findViewById(R.id.b_lap);
	}

	protected void setListeners() {
		toggle.setOnCheckedChangeListener(this);
		lap.setOnClickListener(this);
		findViewById(R.id.b_toggle).setOnTouchListener(mDelayHideTouchListener);
		contentView.setOnClickListener(this);
	}

	protected void initializeFeilds() {
		setupSystemUIHider();

		T = new StudyTimer(tHandler, getSupportFragmentManager());
		T.setStatusLogging(true);
		isLargeLayoutBoolean = getResources().getBoolean(R.bool.large_layout);
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

					@SuppressWarnings("unused")
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
		case R.id.b_lap:
			T.lap();
			break;
		case R.id.fullscreen_content:
			mSystemUiHider.show();
			break;

		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean newState) {
		if(newState==true)
			T.start();
		else
			T.stop();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case R.id.mi_preferences:
			i = new Intent("com.reubenjohn.studytimer.PREFERENCES");
			startActivityForResult(i, 0);
			break;
		case R.id.mi_new_session:
			showSessionDialog(isLargeLayoutBoolean);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == keys.resetSession) {
			if (resultCode == RESULT_OK) {
				resetRequested = data.getBooleanExtra("KEY_RESET_REQUESTED",
						false);
				Log.d("StudyTimer", "result=" + resetRequested);
				if(resetRequested){
					toggle.setChecked(false);
					T.reset();	
				}
			}
		}
	}

	protected void showSessionDialog(boolean windowed) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
		final TimePickerDialog timePicker = new TimePickerDialog(Home.this,
				new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int minute,
							int second) {
						long target = Time.getTimeInMilliseconds(0, 0, minute,
								second, 0);
						Log.d("StudyTimer", "Target time set: " + target);
						T.setTargetTime(target);
					}
				}, 1, 0, true);
		builder.setTitle(R.string.title_session_setup)
				.setItems(R.array.session_setup_elements,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								switch (which) {
								case 0:
									Log.d("StudyTimer",
											"Set target time dialog");
									timePicker.show();
									break;
								case 1:

									break;
								case 2:
									break;
								}
							}
						}).show();

	}

}
