package com.reubenjohn.studytimer.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.data.StudyTimerDBManager.LapDBManager;

public class LapsCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;
	TextView lapNumber, lapDuration;

	public LapsCursorAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		view.setBackgroundColor(context.getResources().getColor(R.color.purple2));
		/*
		 * if (cursor.getPosition() % 2 == 1) {
		 * view.setBackgroundColor(context.getResources().getColor(
		 * R.color.orange)); } else {
		 * view.setBackgroundColor(context.getResources().getColor(
		 * R.color.purple2)); }
		 */
		lapNumber = (TextView) view.findViewById(R.id.tv_lap_number);
		lapNumber.setText(cursor.getString(cursor
				.getColumnIndex(LapDBManager.KEY_ROWID)));

		lapDuration = (TextView) view.findViewById(R.id.tv_lap_duration);
		lapDuration.setText(cursor.getString(cursor
				.getColumnIndex(LapDBManager.KEY_DURATION)));

	}
	// TODO overide getView() to improve performance

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.laps_list_item, parent, false);
	}

}
