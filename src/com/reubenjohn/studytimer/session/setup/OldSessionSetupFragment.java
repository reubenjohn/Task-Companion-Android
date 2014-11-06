package com.reubenjohn.studytimer.session.setup;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.R.array;
import com.reubenjohn.studytimer.R.id;
import com.reubenjohn.studytimer.R.layout;
import com.reubenjohn.studytimer.R.string;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class OldSessionSetupFragment extends DialogFragment {

	ListView list;
	Button positive, negative;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(R.string.title_session_setup);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.old_session_setup_fragment, container,
				false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	protected void bridgeXML(View v) {
		list = (ListView) v.findViewById(R.id.lv_session_setup);
		positive = (Button) v.findViewById(R.id.b_session_create);
		negative = (Button) v.findViewById(R.id.b_session_cancel);
	}

	private void initializeFeilds() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, getResources()
						.getStringArray(R.array.session_setup_elements));
		list.setAdapter(adapter);
	}

	public void setPositiveListener(OnClickListener listener) {
		assert listener != null;
		if (listener != null)
			positive.setOnClickListener(listener);
	}

	public void setNegativeListener(OnClickListener listener) {
		assert positive != null;
		if (listener != null)
			negative.setOnClickListener(listener);
	}

}