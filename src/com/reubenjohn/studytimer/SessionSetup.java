package com.reubenjohn.studytimer;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.reubenjohn.studytimer.R.integer;
import com.reubenjohn.studytimer.StudyTimer.defaults;
import com.reubenjohn.studytimer.preferences.STSP;
import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.util.kankan.widget.OnWheelChangedListener;
import com.reubenjohn.studytimer.util.kankan.widget.WheelView;
import com.reubenjohn.studytimer.util.kankan.widget.adapters.NumericWheelAdapter;

public class SessionSetup extends ActionBarActivity implements
		OnCheckedChangeListener, OnWheelChangedListener, OnTimeChangedListener {

	CheckBox smartTargetTimeToggle;
	TimePicker targetPicker, totalTimePicker;
	TextView smartTargetTimeToggleDescription, targetTimeTitle,
			targetTimeDescription;
	WheelView wheelView_100, wheelView_10, wheelView_1;
	int digit1, digit10, digit100;

	Bundle sessionInfo;

	ActionMode sessionCreateActionMode;

	private ActionMode.Callback sessionCreateActionModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Intent result = new Intent();
			setResult(RESULT_CANCELED, result);
			finish();
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_create_session, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem menu) {
			Log.d("StudyTimer", "Action item clicked");
			Intent result = new Intent();
			long target = Time.getTimeInMilliseconds(0, 0,
					targetPicker.getCurrentHour(),
					targetPicker.getCurrentMinute(), 0);
			sessionInfo.putLong(STSP.keys.targetTime, target);
			sessionInfo.putInt(STSP.keys.totalLaps, getTotalLaps());
			Log.d("StudyTimerSessionSetup", "Bundled new sessionInfo->{"
					+ STSP.keys.targetTime + ":" + target + "," + "TotalLaps"
					+ ":" + getTotalLaps() + "}");
			result.putExtras(sessionInfo);
			setResult(RESULT_OK, result);
			finish();
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_setup);

		bridgeXML();
		initializeFeilds();

	}

	private void initializeDigitWheel(int id) {
		WheelView wheelView = (WheelView) findViewById(id);
		NumericWheelAdapter myCustomNumericWheelAdapter = new NumericWheelAdapter(
				this, 0, 9);
		myCustomNumericWheelAdapter.setTextSize((int) new TextView(
				getApplicationContext()).getTextSize());
		wheelView.setViewAdapter(myCustomNumericWheelAdapter);
		wheelView.setCurrentItem(0);
		wheelView.setVisibleItems(3);
		wheelView.measure(30, 50);
		wheelView.setCyclic(true);
		int backgroundColor = getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.colorBackground,
						android.R.attr.textColorPrimary, }).getColor(0,
				0xFF00FF);
		wheelView.setShadowColours(backgroundColor, backgroundColor,
				0x00FFFFFF & backgroundColor);
		wheelView.setInterpolator(new AnticipateOvershootInterpolator());
		wheelView.addChangingListener(SessionSetup.this);
	}

	private void bridgeXML() {
		smartTargetTimeToggle = (CheckBox) findViewById(R.id.cb_smart_target_time);
		targetPicker = (TimePicker) findViewById(R.id.tp_target_time);
		totalTimePicker = (TimePicker) findViewById(R.id.tp_total_time);
		targetTimeTitle = (TextView) findViewById(R.id.tv_session_create_target_time_title);
		targetTimeDescription = (TextView) findViewById(R.id.tv_session_create_target_time_description);
		smartTargetTimeToggleDescription = (TextView) findViewById(R.id.tv_smart_target_time_description);
		wheelView_1 = (WheelView) findViewById(R.id.wv_total_lap_picker_digit1);
		wheelView_10 = (WheelView) findViewById(R.id.wv_total_lap_picker_digit10);
		wheelView_100 = (WheelView) findViewById(R.id.wv_total_lap_picker_digit100);

	}

	private void initializeFeilds() {
		smartTargetTimeToggle.setOnCheckedChangeListener(this);
		SharedPreferences sessionPrefs = getSharedPreferences(
				STSP.fileNames.currentSession, Context.MODE_PRIVATE);
		long targerTime = sessionPrefs.getLong(STSP.keys.targetTime,
				StudyTimer.defaults.targetTime);
		int totalLaps = sessionPrefs.getInt(STSP.keys.totalLaps,
				defaults.totalLaps);
		Log.d("SessionSetup", "Retreived targetTime=" + targerTime);
		Log.d("SessionSetup", "Retreived totalLaps=" + totalLaps);

		Time currentTargetTime = new Time(targerTime);
		targetPicker.setCurrentHour((int) currentTargetTime.getMinutes());
		targetPicker.setCurrentMinute((int) currentTargetTime.getSeconds());

		setTotalSessionTimePicker(targerTime,
				sessionPrefs.getInt(STSP.keys.totalLaps, defaults.totalLaps));

		targetPicker.setIs24HourView(true);
		targetPicker.setOnTimeChangedListener(this);
		totalTimePicker.setIs24HourView(true);
		totalTimePicker.setOnTimeChangedListener(this);

		sessionInfo = new Bundle();

		if (sessionCreateActionMode == null) {
			sessionCreateActionMode = SessionSetup.this
					.startSupportActionMode(sessionCreateActionModeCallBack);
		}
		initializeDigitWheel(R.id.wv_total_lap_picker_digit1);
		initializeDigitWheel(R.id.wv_total_lap_picker_digit10);
		initializeDigitWheel(R.id.wv_total_lap_picker_digit100);

		setTotalLaps(totalLaps);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		boolean toBeEnabled = !isChecked;
		targetPicker.setEnabled(toBeEnabled);
		targetTimeTitle.setEnabled(toBeEnabled);
		targetTimeDescription.setEnabled(toBeEnabled);
		if (isChecked)
			smartTargetTimeToggleDescription
					.setText(R.string.session_create_smart_target_description_enabled);
		else
			smartTargetTimeToggleDescription
					.setText(R.string.session_create_smart_target_description_disabled);
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		switch (wheel.getId()) {
		case R.id.wv_total_lap_picker_digit1:
			if (oldValue == 9 && newValue == 0) {
				incrementTotalLapsBy10();
			} else if (oldValue == 0 && newValue == 9) {
				decrementTotalLapsBy10();
			}
			digit1 = newValue;
			break;
		case R.id.wv_total_lap_picker_digit10:
			if (oldValue == 9 && newValue == 0) {
				incrementTotalLapsBy100();
			} else if (oldValue == 0 && newValue == 9) {
				decrementTotalLapsBy100();
			}
			digit10 = newValue;
			break;
		case R.id.wv_total_lap_picker_digit100:
			digit100 = newValue;
			break;
		}
	}

	private void decrementTotalLapsBy10() {
		switch (wheelView_10.getCurrentItem()) {
		case 0:
			if (wheelView_100.getCurrentItem() != 0)
				wheelView_10.setCurrentItem(wheelView_10.getCurrentItem() - 1);
			break;
		case 1:
			if (wheelView_100.getCurrentItem() == 0)
				wheelView_10.setVisibility(View.GONE);
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			wheelView_10.setCurrentItem(wheelView_10.getCurrentItem() - 1);
			break;
		}
	}

	private void decrementTotalLapsBy100() {
		switch (wheelView_100.getCurrentItem()) {
		case 1:
			wheelView_100.setVisibility(View.GONE);
			wheelView_100.setCurrentItem(wheelView_100.getCurrentItem() - 1);
		case 0:
			break;
		default:
			wheelView_100.setCurrentItem(wheelView_100.getCurrentItem() - 1);
			break;
		}
	}

	private void incrementTotalLapsBy10() {
		if (wheelView_10.getCurrentItem() != 9) {
			wheelView_10.setVisibility(View.VISIBLE);
			wheelView_10.setCurrentItem(wheelView_10.getCurrentItem() + 1);
		} else if (wheelView_100.getCurrentItem() != 9) {
			wheelView_100.setVisibility(View.VISIBLE);
			wheelView_100.setCurrentItem(wheelView_100.getCurrentItem() + 1);
		}
	}

	private void incrementTotalLapsBy100() {
		if (wheelView_100.getCurrentItem() != 9) {
			wheelView_100.setVisibility(View.VISIBLE);
			wheelView_100.setCurrentItem(wheelView_100.getCurrentItem() + 1);
		}
	}

	private int getTotalLaps() {
		return digit100 * 100 + digit10 * 10 + digit1;
	}

	private void setTotalLaps(int totalLaps) {
		if (totalLaps >= 10)
			wheelView_10.setVisibility(View.VISIBLE);
		else
			wheelView_10.setVisibility(View.GONE);
		if (totalLaps >= 100)
			wheelView_100.setVisibility(View.VISIBLE);
		else
			wheelView_100.setVisibility(View.GONE);
		
		wheelView_1.setCurrentItem(totalLaps % 10);
		totalLaps /= 10;
		wheelView_10.setCurrentItem(totalLaps % 10);
		totalLaps /= 10;
		wheelView_100.setCurrentItem(totalLaps % 10);
		totalLaps /= 10;
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		switch (view.getId()) {
		case R.id.tp_target_time:
			if (hourOfDay == 0 && minute == 0) {
				view.setCurrentMinute(1);
				// TODO flash the Target time text in red and then fade it away
				targetTimeTitle.setTextColor(Color.RED);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						targetTimeTitle.setTextColor(Color.BLACK);

					}
				}, 1000);
			} else {
				setTotalSessionTimePicker(
						Time.getTimeInMilliseconds(0, 0, hourOfDay, minute, 0),
						getTotalLaps());
			}
			break;
		case R.id.tp_total_time:
			setTargetTimePicker(Time.getTimeInMilliseconds(0, hourOfDay,
					minute, 0, 0));
			break;
		}
	}

	protected void setTargetTimePicker(long totalSessionTimeInMilliSeconds) {
		Time targetTime = new Time(totalSessionTimeInMilliSeconds
				/ getTotalLaps());
		targetPicker.setCurrentHour((int) targetTime.getMinutes());
		targetPicker.setCurrentMinute((int) targetTime.getSeconds());
	}

	protected void setTotalSessionTimePicker(long targetTime, int totalLaps) {
		long totalTime = targetTime * totalLaps;
		Time totalSessionTime = new Time(totalTime);
		totalTimePicker.setCurrentHour((int) totalSessionTime.getHours());
		totalTimePicker.setCurrentMinute((int) totalSessionTime.getMinutes());
	}
}