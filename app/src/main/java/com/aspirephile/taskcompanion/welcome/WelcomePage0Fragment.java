package com.aspirephile.taskcompanion.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aspirephile.taskcompanion.R;

public class WelcomePage0Fragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		return (LinearLayout) inflater.inflate(
				R.layout.fragment_welcome_page_0, container, false);
	}
}
