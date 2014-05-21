package com.reubenjohn.studytimer;	

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
	private TimerView elapse, total_elapse;
	int lapCount = 0;
	boolean realTimeAverageEnabled = true,running=false;
	int average = 0;

	protected static class keys{
		public static final String elapse="ELAPSE";
		public static String total_elapse="TOTAL_ELAPSE";
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.timer_elements_fragment, container,
				false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs=getActivity().getPreferences(Context.MODE_PRIVATE);
		if(!running){
			elapse.setElapse(prefs.getLong(keys.elapse, 0));
			total_elapse.setElapse(prefs.getLong(keys.total_elapse, 0));
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences prefs=getActivity().getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=prefs.edit();
		editor.putLong(keys.elapse, elapse.getElapse());
		editor.putLong(keys.total_elapse, total_elapse.getElapse());
		editor.commit();
	}

	public void start() {
		elapse.start();
		total_elapse.start();
		running=true;
	}

	public void stop() {
		elapse.stop();
		total_elapse.stop();
		running=false;
	}

	public void toggle() {
		if(running)
			running=false;
		else
			running=true;
		elapse.toggle();
		total_elapse.toggle();
	}

	public void lap(int lapCount) {
		elapse.reset();
		elapse.start();
		this.lapCount = lapCount;
	}

	public void reset() {
		elapse.reset();
		total_elapse.reset();
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
			tv_average.setText(Time.getFormattedTime("%MM:%SS.%sss",
					average));
	}

	public void addFrameTimerListenersTo(FrameTimer framer) {
		framer.addFrameTimerListener(elapse);
		framer.addFrameTimerListener(total_elapse);
		framer.addFrameTimerListener(this);
	}

	public void removeFrameTimerListenersFrom(FrameTimer framer) {
		framer.removeFrameTimerListener(elapse);
		framer.removeFrameTimerListener(total_elapse);
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
		total_elapse = factory.produceTimerView(tv_total_elapse);
	}

	@Override
	public void onNewFrame() {
		if (realTimeAverageEnabled) {
			int realTimeAverage = (int) (average * lapCount + elapse
					.getElapse()) / (lapCount + 1);
			tv_average.setText(Time.getFormattedTime("%MM:%SS.%sss",realTimeAverage));
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

}
