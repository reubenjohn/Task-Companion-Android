package com.reubenjohn.studytimer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

public class FillerFragment extends Fragment {
	private ProgressBar progress;
	private ValueAnimator animation;
	int duration = 100;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.filler, container, false);
		bridgeXML(v);
		progress.setMax(1000);
		return v;
	}

	protected void bridgeXML(View v) {
		progress = (ProgressBar) v.findViewById(R.id.pb_filler);
	}

	@TargetApi(11)
	public void setUpdateInterval(int intervalInMilliSeconds) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			animation.setDuration(intervalInMilliSeconds);
			duration = intervalInMilliSeconds;
		}
	}

	@TargetApi(11)
	public void updateFillerProgress(float percentage) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ObjectAnimator animator = ObjectAnimator.ofInt(progress,
					"Progress", (int) (percentage * 10));
			animator.setDuration(duration);
			animator.start();
		} else {
			progress.setProgress((int) percentage * 10);
		}
	}
}