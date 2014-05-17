package com.reubenjohn.studytimer;

import android.os.Handler;

import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;

public class StudyTimer {

	public FrameTimer framer;
	Timer runtime;
	
	boolean running=false;

	StudyTimer(Handler thisHandler) {
		framer = new FrameTimer(thisHandler);
		runtime=new Timer();
	}

	public void startTimer() {
		running=true;
		runtime.start();
	}

	protected void reset() {
		framer.reset();
		runtime.start();
	}

}
