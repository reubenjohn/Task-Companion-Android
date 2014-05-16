package com.reubenjohn.studytimer;

import android.content.Context;
import android.os.Handler;

import com.reubenjohn.studytimer.timming.FrameTimer;
import com.reubenjohn.studytimer.timming.Timer;

public class StudyTimer implements Runnable{

	public FrameTimer framer;
	Timer runtime;
	Handler thisHandler;
	
	boolean running=false;

	StudyTimer(Context context,Handler thisHandler) {
		framer = new FrameTimer();
		runtime=new Timer();
		this.thisHandler=thisHandler;
	}

	public void startTimer() {
		running=true;
		runtime.start();
	}

	protected void reset() {
		framer.reset();
		runtime.start();
	}

	@Override
	public void run() {
			framer.startFrame();
			framer.endFrame();
			thisHandler.postDelayed(this, framer.getRemainingTime());
	}

}
