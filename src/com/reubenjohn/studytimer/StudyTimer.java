package com.reubenjohn.studytimer;

import android.content.Context;

public class StudyTimer {

	public Timer elapse;

	StudyTimer(Context context) {
		elapse = new Timer();

	}

	protected void reset() {
		elapse.reset();
	}

}
