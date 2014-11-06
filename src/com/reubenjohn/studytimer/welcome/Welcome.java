package com.reubenjohn.studytimer.welcome;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.view.ActionMode;

import com.reubenjohn.studytimer.R;

public class Welcome extends FragmentActivity {

	ActionMode sessionSetupActionMode;
	WelcomeFragment welcomeF;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		bridgeXML();
		initiateFeilds();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fl_welcome, welcomeF).commit();
		}
	}

	private void bridgeXML() {

	}

	private void initiateFeilds() {
		welcomeF = new WelcomeFragment();
	}

	@Override
	public void onBackPressed() {
	}

}
