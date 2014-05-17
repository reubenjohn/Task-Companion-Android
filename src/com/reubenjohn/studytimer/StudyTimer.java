package com.reubenjohn.studytimer;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;

public class StudyTimer {

	public FrameTimer framer;
	Timer runtime;
	TimerElementsFragment timerElements;
	LapsFragment lapsF;

	boolean running = false;

	StudyTimer(Handler thisHandler, FragmentManager fragM) {
		Timer.setDefulatFormat("%MM:%SS.%sss");
		framer = new FrameTimer(thisHandler);
		runtime = new Timer();
		timerElements = (TimerElementsFragment) fragM.findFragmentById(R.id.home_timer_elements);
		lapsF=(LapsFragment) fragM.findFragmentById(R.id.home_laps);
		runtime.start();
		framer.addFrameTimerListener(timerElements.elapse);
	}

	public void toggle() {
		timerElements.toggle();
	}
	
	public void lap(){
		Log.d("StudyTimer", "attempting to lap");
		if(lapsF!=null){
			lapsF.addlap(timerElements.elapse.timer.getFormattedTime());
		}
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

	public void onStop(){
		runtime.stop();
	}

	public void logStatus() {
		Log.d("StudyTimer", getStatus());
	}

	public String getStatus() {
		return "Status: Runtime[" + runtime.getFormattedTime() + "] Frame["
				+ framer.getFrame() + "]";
	}

	protected void setListeners() {
		framer.addFrameTimerListener(timerElements.elapse);
	}

}
