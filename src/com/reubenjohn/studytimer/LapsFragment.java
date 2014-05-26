package com.reubenjohn.studytimer;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.reubenjohn.studytimer.data.LapsCursorAdapter;
import com.reubenjohn.studytimer.data.StudyTimerDBManager;

public class LapsFragment extends Fragment {

	private LapsCursorAdapter adapter;
	private StudyTimerDBManager db;
	ListView listView;
	String formatedAverage = null;
	long average = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.laps_fragment, container, false);
		listView = (ListView) v.findViewById(R.id.lv_laps);
		db = new StudyTimerDBManager(getActivity().getApplicationContext());
		db.open();
		generateListView();
		return v;
	}

	protected void generateListView() {
		// TODO Use Cursor loaders to prevent possible janks
		// http://developer.android.com/guide/topics/ui/layout/listview.html#Loader

		Cursor cursor = db.fetchAllLaps();
		/*
		 * adapter = new SimpleCursorAdapter(getActivity(),
		 * R.layout.laps_list_item, cursor,
		 * StudyTimerDBManager.LapDBManager.columns,
		 * StudyTimerDBManager.LapDBManager.to);
		 */
		adapter = new LapsCursorAdapter(getActivity().getApplicationContext(),
				cursor, 0);
		listView.setAdapter(adapter);
	}

	public void reset() {
	}

	public void addlap(String newLap, int newElapsed) {
		db.addLap(newLap, newElapsed);
		generateListView();
		// average=getAverage();
	}

	public int getLapCount() {
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

}
