package com.reubenjohn.studytimer;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameIntervalListener;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

public class StudyTimer implements FrameTimerListener {

	public FrameTimer framer;
	Timer runtime;
	TimerElementsFragment timerElements;
	LapsFragment lapsF;
	FillerFragment filler;
	private static class logging {
		static boolean status = false;
		static int loggingInterval = 300;
		static FrameIntervalListener listener=null;
	}

	public class SessionInfo{
		public long totalElapse;
		public long currentElapse;
		public int laps;
	}
	
	StudyTimer(Handler thisHandler, FragmentManager fragM) {
		Time.setDefaultFormat("%MM:%SS.%s");
		framer = new FrameTimer(thisHandler);
		framer.setInterval(100);
		runtime = new Timer();
		runtime.setFormat("%MM:%SS.%sss");
		timerElements = (TimerElementsFragment) fragM
				.findFragmentById(R.id.home_timer_elements);
		lapsF = (LapsFragment) fragM.findFragmentById(R.id.home_laps);
		filler = (FillerFragment) fragM
				.findFragmentById(R.id.filler_background);
		timerElements.initializeLapCount(lapsF.getLapCount());
		timerElements.setAverage(lapsF.getAverage());
		setListeners(framer);
		runtime.start();
	}

	public void startNewSession(SessionInfo sessionInfo) {
		if(sessionInfo==null){
			reset();
		}
	}
	
	public void toggle() {
		timerElements.toggle();
	}

	public void lap() {
		if (lapsF != null) {
			lapsF.addlap(timerElements.getFormatedElapse(),(int)timerElements.getElapse());
		}
		timerElements.setAverage(lapsF.getAverage());
		timerElements.lap(lapsF.getLapCount());
	}

	protected void reset() {
		framer.reset();
		runtime.start();
		timerElements.reset();
	}

	public void onPause() {
		framer.stop();
		runtime.stop();
		Log.d("StudyTimer", "onPause() called");
	}

	public void onResume() {
		Log.d("StudyTimer", "onResume() called");
		framer.start();
		runtime.start();
	}

	public void onStop() {
	}

	public void logStatus() {
		Log.i("StudyTimer", getStatus());
	}

	public String getStatus() {
		return "Status: Runtime[" + runtime.getFormattedTime() + "] Frame["
				+ framer.getFrame() + "] " + getTimerElementsStatus() + " "
				+ getLapsFStatus();
	}

	public String getTimerElementsStatus() {
		if (timerElements != null)
			return "Basic timer elements[OK]";
		else
			return "Basic timer elements[BAD]";
	}

	public String getDBFStatus() {
		if (lapsF != null)
			return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
		else
			return "Lap elements[BAD]";
	}

	public String getLapsFStatus() {
		if (lapsF != null)
			return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
		else
			return "Lap elements[BAD]";
	}
	
	protected void setListeners(FrameTimer framer) {
		timerElements.addFrameTimerListenersTo(framer);
		framer.addFrameTimerListener(this);
	}

	public void setStatusLogging(boolean status) {
		if (status) {
			if (!logging.status) {
				logging.status = true;
				logging.listener=new FrameIntervalListener() {
					@Override
					public void OnFrameReached() {
						logStatus();
					}
				};
				framer.addFrameReachListener(logging.listener, logging.loggingInterval);
			}
		}
		else{
			if(logging.status){
				if(logging.listener!=null)
					logging.status=false;
					framer.removeFrameIntervalListenerContainer(logging.listener);
			}
		}
	}
	
	public void setTargetTime(long timeInMilliseconds) {
		timerElements.setTargetTIme(timeInMilliseconds);
	}
	
	public float getLapProgress() {
		assert timerElements!=null;
		if(timerElements!=null)
			return timerElements.getLapProgress();
		else
			return -1;
	}


	@Override
	public void onNewFrame() {
		filler.updateFillerProgress(timerElements.getLapProgress());
	}

	@Override
	public void onEndFrame() {
		
	}

	@Override
	public void onReset() {
		
	}


}
