package com.reubenjohn.studytimer;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.reubenjohn.studytimer.StudyTimer.defaults;
import com.reubenjohn.studytimer.data.LapsCursorAdapter;
import com.reubenjohn.studytimer.data.StudyTimerDBManager;
import com.reubenjohn.studytimer.preferences.STSP;

public class LapsFragment extends Fragment implements
		TimerElementsFragment.TimerElementsListener {

	private LapsCursorAdapter adapter;
	private StudyTimerDBManager db;
	ListView listView;
	TextView currentLap;
	ProgressBar lapProgress;
	String formatedAverage = null;
	long average = 0;
	int totalLaps;

	private static class cache {
		static final int DEFAULT_TOTAL_LAP_COUNT = 20;
		static int lapCount;
		static int totalLapProgressPercentage;

		public static void clear() {
			lapCount = 0;
		}

		public static void resetSession() {
			lapCount = 0;
			totalLapProgressPercentage = 0;
		}
	}

	// pb_total_lap_progress

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
		lapProgress = (ProgressBar) v.findViewById(R.id.pb_total_lap_progress);
	}

	protected void initalizeFeilds() {
		assert currentLap != null;
		cache.lapCount = getLapCount();
		updateCurentLap(cache.lapCount);
		lapProgress.getProgressDrawable()
				.setColorFilter(Color.RED, Mode.SRC_IN);
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

	public void resetSession() {
		Log.d("StudyTimer", "LapsFragment resetSession");
		db.reset();
		cache.resetSession();
		average = 0;
		formatedAverage = getResources().getString(
				R.string.intitial_accurate_time);
		updateCurentLap(0);
	}

	public void reset() {
		Log.d("StudyTimer", "LapsFragment reset");
		db.reset();
		cache.clear();
		average = 0;
		formatedAverage = getResources().getString(
				R.string.intitial_accurate_time);
		updateCurentLap(0);
	}

	public boolean hasNoLaps() {
		return cache.lapCount == 0;
	}

	public boolean addLap(String newLap, int newElapsed) {
		db.addLap(newLap, newElapsed);
		generateListView();
		cache.lapCount++;
		updateCurentLap(cache.lapCount);
		// average=getAverage();
		return cache.lapCount == 1;
	}

	protected void updateCurentLap(int lapCount) {
		assert currentLap != null;
		assert lapProgress != null;
		Log.d("LapsFragment", "Updating currentLap to : " + (lapCount + 1));
		currentLap.setText(Integer.toString(lapCount + 1));
		cache.totalLapProgressPercentage = lapCount;
		if (totalLaps != 0) {
			cache.totalLapProgressPercentage = (int) (lapCount * 100.f / totalLaps);
			Log.d("LapsFragment", "cache.totalLapProgressPercentage = (int) ("
					+ lapCount + " * 100.f / " + totalLaps + ") = "
					+ cache.totalLapProgressPercentage);
		}

		updateLapProgressBar(cache.totalLapProgressPercentage);

	}

	public void updateCurrentLapProgress(float lapProgress) {
		if (totalLaps != 0)
			updateCurrentLapProgress(cache.totalLapProgressPercentage);
	}

	protected void updateLapProgressBar(int totalLapProgressPercentage) {
		if (lapProgress != null) {
			Log.d("LapsFragment", "Updating lap progress bar to "
					+ totalLapProgressPercentage + "%");
			if (android.os.Build.VERSION.SDK_INT >= 11)
				updateLapProgressBarHoneyCombStyle(totalLapProgressPercentage);
			else
				lapProgress.setProgress(totalLapProgressPercentage); // update
																		// GingerBread
			// style
		}

	}

	/*
	 * will update the "progress" propriety of SeekBar until it reaches progress
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void updateLapProgressBarHoneyCombStyle(int lapCount) {
		ObjectAnimator animation = ObjectAnimator.ofInt(lapProgress,
				"progress", lapCount);
		animation.setDuration(getAverage());
		animation.setInterpolator(new DecelerateInterpolator());
		animation.start();

	}

	public int getLapCount() {
		assert db != null;
		if (db == null) {
			return -1;
		} else
			return db.getLapCount();
	}

	public int getTotalLapCount() {
		return totalLaps;
	}

	public void setTotalLapCount(int totalLaps) {
		Log.d("LapsFragment", "Total lap count set to " + totalLaps);
		this.totalLaps = totalLaps;
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

	public void createNewSession(Bundle sessionInfo) {
		Log.d("LapsFragment", "assert sessionInfo!=null -> "
				+ (sessionInfo != null));
		setTotalLapCount(sessionInfo.getInt(STSP.keys.totalLaps, defaults.totalLaps));
	}

	public void putSessionInfo(Bundle sessionInfo) {
		sessionInfo.putInt(STSP.keys.totalLaps, getTotalLapCount());
	}

	public void loadSessionFromBundle(Bundle sessionInfo) {
		totalLaps = sessionInfo.getInt(STSP.keys.totalLaps, defaults.totalLaps);
	}

}
