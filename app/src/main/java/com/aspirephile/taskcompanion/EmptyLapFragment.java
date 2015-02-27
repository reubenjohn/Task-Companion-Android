package com.aspirephile.taskcompanion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EmptyLapFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.empty_lap_fragment, container, false);
		bridgeXML(v);
		initializeFields();
		generateListView();
		return v;
	}

	public void generateListView() {
		
	}

	public void initializeFields() {
		
	}

	protected void bridgeXML(View v) {
		
	}
}
