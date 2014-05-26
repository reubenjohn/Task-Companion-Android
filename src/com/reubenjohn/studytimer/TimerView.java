package com.reubenjohn.studytimer;

import android.os.Bundle;
import android.widget.TextView;

import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

public class TimerView implements FrameTimerListener {

	public Timer timer;
	private TextView text = null;

	public void commonConstruction() {
		timer = new Timer();
	}

	public TimerView() {
		commonConstruction();
	}

	public TimerView(TextView textView) {
		commonConstruction();
		setTextView(textView);
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public void reset(){
		timer.reset();
	}
	
	public void toggle() {
		timer.toggle();
	}

	public void updateText() {
		if (text != null) {
			text.setText(timer.getFormattedTime());
		}
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

	public void setTextView(TextView textView) {
		text = textView;
	}

	public void saveStateToBundle(Bundle outState) {
		timer.saveStateToBundle(outState);
	}

	@Override
	public String toString() {
		return text.getText().toString();
	}

	public void setFormat(String format) {
		assert timer != null;
		timer.setFormat(format);
	}

	public long getElapse() {
		return timer.getElapse();
	}

	public void setElapse(long elapse) {
		timer.setElapse(elapse);
	}

	public void setStartTime(long startTime) {
		timer.setStartTime(startTime);
	}

}
