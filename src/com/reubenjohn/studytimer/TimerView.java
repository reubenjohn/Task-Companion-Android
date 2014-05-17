package com.reubenjohn.studytimer;

import android.os.Bundle;
import android.widget.TextView;

import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

public class TimerView implements FrameTimerListener {

	public Timer timer;
	private TextView text;

	public void commonConstruction() {
		timer = new Timer();
	}

	public TimerView() {
		commonConstruction();
	}

	public void updateText() {
		if (text != null) {
			text.setText(timer.getFormattedTime());
		}
	}

	public void reset() {
		timer.reset();
	}

	@Override
	public void onNewFrame() {
		updateText();
	}

	@Override
	public void onEndFrame() {

	}

	@Override
	public void onReset() {

	}

	public void setTextView(TextView textView){
		text=textView;
	}

	public void saveStateToBundle(Bundle outState){
		timer.saveStateToBundle(outState);
	}
}
