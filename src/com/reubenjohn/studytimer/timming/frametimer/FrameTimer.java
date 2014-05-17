package com.reubenjohn.studytimer.timming.frametimer;

import java.util.ArrayList;

import android.os.Handler;
import android.util.Log;

import com.reubenjohn.studytimer.timming.Timer;

public class FrameTimer implements Runnable {
	public static int defaultInterval = 20;
	private long frame = 0;
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
	}

	public void stop() {
		Log.d("StudyTimer", "FrameTimer.stop() called");
		running = false;
		thisHandler.removeCallbacks(this);
	}

	protected void startFrame() {
		timer.start();
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
		timer.reset();
		timer.start();
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

	public void createSmartSleep() {
		try {
			Thread.sleep(getRemainingTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addFrameIntervalListenerContainer(
			FrameIntervalListenerContainer frameIntervalListenerContainer) {
		frameILC.add(frameIntervalListenerContainer);
	}

	public void addFrameReachListener(FrameIntervalListener frameIntervalListener,
			int interval) {
		frameILC.add(new FrameIntervalListenerContainer(frameIntervalListener,
				interval));
	}

	public void addFrameTimerListener(FrameTimerListener listener) {
		listeners.add(listener);
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
		return (int) ((interval >= timer.getElapse()) ? (interval - timer
				.getElapse()) : 0);
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
			thisHandler.postDelayed(this, getRemainingTime());
		}
	}

}
