package com.aspirephile.studytimer.welcome;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aspirephile.studytimer.R;

public class WelcomePage2Fragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		return (LinearLayout) inflater.inflate(
				R.layout.fragment_welcome_page_2, container, false);
	}
}