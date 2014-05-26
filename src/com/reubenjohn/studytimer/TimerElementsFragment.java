package com.reubenjohn.studytimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

public class TimerElementsFragment extends Fragment implements
		FrameTimerListener {

	private TextView tv_elapse, tv_total_elapse, tv_average;
	private TimerView elapse, totalElapse;
	int lapCount;
	boolean realTimeAverageEnabled = true, running;
	int average;
	private long targetTime;

	protected static class keys {
		public static final String elapse = "ELAPSE";
		public static String totalElapse = "TOTAL_ELAPSE";
		public static String running = "RUNNING";
		public static String stopTime="STOP_TIME_TIME";
		public static String targetTime="TARGET_TIME";
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(keys.running, running);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.timer_elements_fragment, container,
				false);
		bridgeXML(v);
		initializeFeilds();
		if (savedInstanceState != null) {

			if (savedInstanceState.getBoolean(keys.running, false)) {
				start();
			}
		}
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		Log.d("StudyTimer", "Timer Elements resume state:"+running);
		elapse.setElapse(prefs.getLong(keys.elapse, 0));
		totalElapse.setElapse(prefs.getLong(keys.totalElapse, 0));		
		targetTime=prefs.getLong(keys.targetTime, Time.getTimeInMilliseconds(0, 0, 1, 0, 0));
		if (running) {
			elapse.setStartTime(prefs.getLong(keys.stopTime, 0));
			totalElapse.setStartTime(prefs.getLong(keys.stopTime, 0));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences prefs = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(keys.elapse, elapse.getElapse());
		editor.putLong(keys.totalElapse, totalElapse.getElapse());
		editor.putLong(keys.stopTime, System.currentTimeMillis());
		editor.commit();
	}

	public void start() {
		elapse.start();
		totalElapse.start();
		running = true;
	}

	public void stop() {
		elapse.stop();
		totalElapse.stop();
		running = false;
	}

	public void toggle() {
		if (running)
			running = false;
		else
			running = true;
		elapse.toggle();
		totalElapse.toggle();
	}

	public void lap(int lapCount) {
		elapse.reset();
		if (running)
			elapse.start();
		this.lapCount = lapCount;
	}

	public void reset() {
		elapse.reset();
		totalElapse.reset();
	}

	public void initializeLapCount(int lapCount) {
		this.lapCount = lapCount;
	}

	public String getFormatedElapse() {
		return elapse.timer.getFormattedTime();
	}

	public void setAverage(int average) {
		this.average = average;
		if (!realTimeAverageEnabled)
			tv_average.setText(Time.getFormattedTime("%MM:%SS.%sss", average));
	}

	public void setTargetTIme(long targetTime) {
		this.targetTime=targetTime;
		SharedPreferences.Editor editor=getActivity().getPreferences(Context.MODE_PRIVATE).edit();
		editor.putLong(keys.targetTime, targetTime);
		editor.commit();
	}
	
	public void addFrameTimerListenersTo(FrameTimer framer) {
		framer.addFrameTimerListener(elapse);
		framer.addFrameTimerListener(totalElapse);
		framer.addFrameTimerListener(this);
	}

	public void removeFrameTimerListenersFrom(FrameTimer framer) {
		framer.removeFrameTimerListener(elapse);
		framer.removeFrameTimerListener(totalElapse);
	}

	protected void bridgeXML(View v) {
		tv_elapse = (TextView) v.findViewById(R.id.tv_elapse);
		tv_total_elapse = (TextView) v.findViewById(R.id.tv_total_elapse);
		tv_average = (TextView) v.findViewById(R.id.tv_average);
	}

	protected void initializeFeilds() {
		TimerViewFactory factory = new TimerViewFactory();
		factory.setDefaultFormat("%MM:%SS.%s");

		elapse = factory.produceTimerView(tv_elapse);
		totalElapse = factory.produceTimerView(tv_total_elapse);
	}

	@Override
	public void onNewFrame() {
		if (realTimeAverageEnabled) {
			int realTimeAverage = (int) (average * lapCount + elapse
					.getElapse()) / (lapCount + 1);
			tv_average.setText(Time.getFormattedTime("%MM:%SS.%sss",
					realTimeAverage));
		}
	}

	@Override
	public void onEndFrame() {

	}

	@Override
	public void onReset() {
	}

	public long getElapse() {
		return elapse.getElapse();
	}

	public float getLapProgress() {
		if(targetTime==0){
			return -1;
		}
		else{
			return (100.f*elapse.getElapse())/targetTime;
		}
	}

}
