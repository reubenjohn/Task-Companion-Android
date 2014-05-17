package com.reubenjohn.studytimer;

import android.os.Handler;
import android.util.Log;

import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;

public class StudyTimer {

	public FrameTimer framer;
	Timer runtime;

	boolean running = false;

	StudyTimer(Handler thisHandler) {
		Timer.setDefulatFormat("%MM:%SS.$sss");
		framer = new FrameTimer(thisHandler);
		runtime = new Timer();
		runtime.start();
	}

	public void startTimer() {
		running = true;
		framer.start();
	}

	protected void reset() {
		framer.reset();
		runtime.start();
	}

	public void onPause() {
		framer.stop();
		Log.d("StudyTimer", "onPause() called");
	}

	public void onResume() {
		Log.d("StudyTimer", "onResume() called");
		framer.start();
	}

	public String getStatus() {
		return "Status: Runtime[" + runtime.getFormattedTime() + "] Frame["
				+ framer.getFrame() + "]";
	}

	public void logStatus() {
		Log.d("StudyTimer", getStatus());
	}
}
