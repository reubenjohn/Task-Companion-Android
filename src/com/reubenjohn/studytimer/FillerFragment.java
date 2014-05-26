package com.reubenjohn.studytimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class FillerFragment extends Fragment {
	View filler, empty;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.filler, container, false);
		bridgeXML(v);
		return v;
	}

	protected void bridgeXML(View v) {
		filler = v.findViewById(R.id.filler_background);
		empty = v.findViewById(R.id.empty_background);
	}

	public void updateFillerProgress(float percentage) {
		if(empty!=null){
			Log.d("StudyTimer","filler progress: "+percentage);
			empty.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, percentage));
		}
	}
}