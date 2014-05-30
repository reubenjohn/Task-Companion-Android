package com.reubenjohn.studytimer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.reubenjohn.studytimer.data.LapsCursorAdapter;
import com.reubenjohn.studytimer.data.StudyTimerDBManager;

public class LapsFragment extends Fragment implements
		TimerElementsFragment.TimerElementsListener {

	private LapsCursorAdapter adapter;
	private StudyTimerDBManager db;
	ListView listView;
	TextView currentLap;
	String formatedAverage = null;
	long average = 0;
	int cached_lapCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.laps_fragment, container, false);
		db = new StudyTimerDBManager(getActivity().getApplicationContext());
		db.open();
		bridgeXML(v);
		initalizeFeilds();
		generateListView();
		return v;
	}

	protected void bridgeXML(View v) {
		listView = (ListView) v.findViewById(R.id.lv_laps);
		currentLap = (TextView) v.findViewById(R.id.tv_current_lap);
	}

	protected void initalizeFeilds() {
		assert currentLap != null;
		cached_lapCount = getLapCount();
		updateCurentLap(cached_lapCount);
	}

	protected void generateListView() {
		// TODO Use Cursor loaders to prevent possible janks
		// http://developer.android.com/guide/topics/ui/layout/listview.html#Loader

		Cursor cursor = db.fetchAllLaps();
		/*
		 * adapter = new SimpleCursorAdapter(getActivity(),
		 * R.layout.laps_list_item, cursor,
		 * StudyTimerDBManager.LapDBProperties.columns,
		 * StudyTimerDBManager.LapDBProperties.to);
		 */
		adapter = new LapsCursorAdapter(getActivity(), cursor, 0);
		listView.setAdapter(adapter);
	}

	public void reset() {
		Log.d("StudyTimer", "LapsFragment reset");
		db.reset();
		cached_lapCount = 0;
		average = 0;
		formatedAverage = getResources().getString(
				R.string.intitial_accurate_time);
		updateCurentLap(0);
	}

	public boolean hasNoLaps() {
		return cached_lapCount == 0;
	}

	public boolean addLap(String newLap, int newElapsed) {
		db.addLap(newLap, newElapsed);
		generateListView();
		cached_lapCount++;
		updateCurentLap(cached_lapCount);
		// average=getAverage();
		return cached_lapCount == 1;
	}

	protected void updateCurentLap(int lapCount) {
		if (currentLap != null)
			currentLap.setText(Integer.toString(lapCount + 1));
	}

	public int getLapCount() {
		assert db != null;
		if (db == null) {
			return -1;
		} else
			return db.getLapCount();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		db.close();
	}

	public int getAverage() {
		return db.getAverage();
	}

	public String getFormattedAverage() {
		return db.getFormattedAverage();
	}

	@Override
	public void onTotalElapseSetManually(long elapse) {
		db.distributeToLaps(elapse);
	}

}
