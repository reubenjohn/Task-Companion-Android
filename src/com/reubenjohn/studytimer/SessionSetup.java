package com.reubenjohn.studytimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.TimePicker;

import com.reubenjohn.studytimer.preferences.STSP;
import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.util.kankan.widget.WheelView;
import com.reubenjohn.studytimer.util.kankan.widget.adapters.NumericWheelAdapter;

public class SessionSetup extends ActionBarActivity {

	TimePicker targetPicker;
	WheelView wheelView;

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
			Log.d("StudyTimerSessionSetup", "Bundled{" + Keys.target + ":"
					+ target + "}");
			sessionInfo.putLong(Keys.target, target);
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
		wheelView = (WheelView) findViewById(id);
		wheelView.setViewAdapter(new NumericWheelAdapter(this, 0, 9));
		wheelView.setCurrentItem((int) (Math.random() * 10));
		wheelView.setCyclic(true);
		wheelView.setInterpolator(new AnticipateOvershootInterpolator());
	}

	private void bridgeXML() {
		targetPicker = (TimePicker) findViewById(R.id.tp_target_time);

	}

	private void initializeFeilds() {
		long targerTime = getSharedPreferences(STSP.fileNames.currentSession,
				Context.MODE_PRIVATE).getLong(STSP.keys.targetTime,
				StudyTimer.defaults.targetTime);
		Time currentTargetTime = new Time(targerTime);
		Log.d("SessionSetup", "Retrerived targetTime=" + targerTime);
		targetPicker.setCurrentHour((int) currentTargetTime.getMinutes());
		targetPicker.setCurrentMinute((int) currentTargetTime.getSeconds());
		targetPicker.setIs24HourView(true);
		targetPicker
				.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

					@Override
					public void onTimeChanged(TimePicker view, int hourOfDay,
							int minute) {

					}
				});

		sessionInfo = new Bundle();

		if (sessionCreateActionMode == null) {
			sessionCreateActionMode = SessionSetup.this
					.startSupportActionMode(sessionCreateActionModeCallBack);
		}
		initializeDigitWheel(R.id.wv_total_lap_picker_digit1);
		initializeDigitWheel(R.id.wv_total_lap_picker_digit2);
		initializeDigitWheel(R.id.wv_total_lap_picker_digit3);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}