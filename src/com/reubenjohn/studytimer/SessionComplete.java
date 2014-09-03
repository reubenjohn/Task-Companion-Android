package com.reubenjohn.studytimer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.reubenjohn.studytimer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SessionComplete extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.timer_elements_fragment);

		bridgeXML();
		initializeFeilds();

	}

	protected void bridgeXML() {
		
	}

	protected void initializeFeilds() {
		
	}

}
