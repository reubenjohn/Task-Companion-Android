package com.reubenjohn.studytimer.timming.frametimer;

import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;

import com.reubenjohn.studytimer.timming.Timer;

public class FrameTimer implements Runnable {
	public static int defaultInterval = 20;
	private long frame = 0, nextFrame = 0;
	private int interval = defaultInterval;
	private boolean running = false;

	private Handler thisHandler;

	private Timer timer;
	private ArrayList<FrameTimerListener> listeners;
	private ArrayList<FrameIntervalListenerContainer> frameILC;

	public FrameTimer(Handler handler) {
		running = false;
		thisHandler = handler;
		timer = new Timer();
		listeners = new ArrayList<FrameTimerListener>();
		frameILC = new ArrayList<FrameIntervalListenerContainer>();
	}

	public void start() {
		Log.d("StudyTimer", "FrameTimer.start() called");
		running = true;
		thisHandler.removeCallbacks(this);
		thisHandler.postDelayed(this, 0);
		timer.start();
	}

	public void stop() {
		Log.d("StudyTimer", "FrameTimer.stop() called");
		running = false;
		thisHandler.removeCallbacks(this);
		timer.stop();
	}

	protected void startFrame() {
		nextFrame = (int) ((frame + 1) * interval);
		for (FrameTimerListener listener : listeners) {
			listener.onNewFrame();
		}
		for (FrameIntervalListenerContainer container : frameILC) {
			if (frame % container.interval == 0) {
				container.listener.OnFrameReached();
			}
		}
	}

	protected void endFrame() {
		frame++;
		for (FrameTimerListener listener : listeners) {
			listener.onEndFrame();
		}
	}

	public void reset() {
		stop();
		timer.reset();
		frame = 0;
		for (FrameTimerListener listener : listeners) {
			listener.onReset();
		}
	}

	public void hardReset() {
		reset();
		interval = defaultInterval;
	}

	public void resetDefaults() {
		defaultInterval = 20;
	}

	public void resetALL() {
		hardReset();
		resetDefaults();
	}

	public void createSmartThreadSleep() {
		try {
			Thread.sleep(getRemainingTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void smartDelayStartNextFrame() {
		if (thisHandler != null)
			thisHandler.postDelayed(this, getRemainingTime());
	}

	public int addFrameIntervalListenerContainer(
			FrameIntervalListenerContainer frameIntervalListenerContainer) {
		frameILC.add(frameIntervalListenerContainer);
		return frameILC.indexOf(frameIntervalListenerContainer);
	}

	public int addFrameReachListener(
			FrameIntervalListener frameIntervalListener, int interval) {
		FrameIntervalListenerContainer container=new FrameIntervalListenerContainer(frameIntervalListener,
				interval);
		frameILC.add(container);
		return frameILC.indexOf(container);
	}

	public int addFrameTimerListener(FrameTimerListener listener) {
		listeners.add(listener);
		return listeners.indexOf(listener);
	}

	public void removeFrameIntervalListenerContainer(Object object){
		frameILC.remove(object);
	}
	
	public void removeFrameReachListener(Object object){
		frameILC.remove(object);
	}
	
	public void removeFrameTimerListener(Object object){
		listeners.remove(object);
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setFrequency(float frequency) {
		setInterval((int) (1000 / frequency));
	}

	public long getFrame() {
		return frame;
	}

	public int getInterval() {
		return interval;
	}

	public int getCurrentFrameElapse() {
		return (int) timer.getElapse();
	}

	public int getRemainingTime() {
		long elapse = timer.getElapse();
		return (int) ((nextFrame >= elapse) ? (nextFrame - elapse) : 0);
	}

	public String getFormattedTime() {
		return timer.getFormattedTime();
	}

	public String getFormattedTime(String format) {
		return timer.getFormattedTime(format);
	}

	@Override
	public void run() {
		if (running) {
			startFrame();
			endFrame();
			smartDelayStartNextFrame();
		}
	}

}
