package com.reubenjohn.studytimer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LapsFragment extends Fragment{

	boolean empty=true;
	
	ArrayAdapter<String> durationAdapter;
	ArrayAdapter<Integer> lapNumberAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.laps_fragment, container, false);
		durationAdapter=new ArrayAdapter<String>(getActivity(),
				R.layout.laps_list_item,R.id.tv_lap_duration);
		lapNumberAdapter=new ArrayAdapter<Integer>(getActivity(),
				R.layout.laps_list_item,R.id.tv_lap_number);
		ListView listView = (ListView) v.findViewById(R.id.lv_laps);
		reset();
		listView.setAdapter(durationAdapter);
		return v;
	}
	
	public void reset(){
			durationAdapter.clear();
			durationAdapter.add(getResources().getString(R.string.empty_lap_adapter));
			empty=true;
	}
	public void addlap(String newLap) {
		if(empty){
			durationAdapter.clear();
			empty=false;
		}
		lapNumberAdapter.add(lapNumberAdapter.getCount()+1);
		durationAdapter.add(newLap);
	}


}
