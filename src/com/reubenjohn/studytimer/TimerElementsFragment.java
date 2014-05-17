package com.reubenjohn.studytimer;

import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class TimerElementsFragment extends Fragment implements
		FrameTimerListener {

	private TextView tv_elapse;
	TimerView elapse;
	Toast t_elapseStarted, t_elapseStopped;

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
		initializeToasts(v);
		return v;
	}

	protected void bridgeXML(View v) {
		tv_elapse = (TextView) v.findViewById(R.id.tv_elapse);
	}

	protected void initializeFeilds() {
		elapse = new TimerView();
		elapse.setTextView(tv_elapse);
		elapse.timer.setFormat("%MM:%SS.%s");
	}

	@SuppressLint("ShowToast")
	protected void initializeToasts(View v) {
		t_elapseStarted = Toast.makeText(v.getContext(), "Elapse started",
				Toast.LENGTH_SHORT);
		t_elapseStopped = Toast.makeText(v.getContext(), "Elapse stopped",
				Toast.LENGTH_SHORT);
	}

	public void toggle() {
		elapse.timer.toggle();
	}

	@Override
	public void onNewFrame() {
		elapse.updateText();
	}

	@Override
	public void onEndFrame() {

	}

	@Override
	public void onReset() {

	}
}
