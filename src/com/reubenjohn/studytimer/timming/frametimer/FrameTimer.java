package com.reubenjohn.studytimer.timming.frametimer;

import android.os.Handler;

import com.reubenjohn.studytimer.timming.Timer;


public class FrameTimer implements Runnable{
	public static int defaultInterval = 200;
	private long frame = 0;
	private int interval = defaultInterval;
	private boolean running=false;
	
	private Handler thisHandler;

	private Timer timer;
	private FrameTimerListener listener;
	private FrameIntervalListener frameIL;

	public FrameTimer(Handler handler) {
		running=false;
		thisHandler=handler;
		timer = new Timer();
		listener = new FrameTimerListener() {

			@Override
			public void onReset() {
			}

			@Override
			public void onNewFrame() {
			}

			@Override
			public void onEndFrame() {
			}
		};
		frameIL=new FrameIntervalListener() {
			@Override
			public void OnFrameReached() {
			}
		};
	}

	public void start(){
		running=true;
		thisHandler.removeCallbacks(this);
		thisHandler.postDelayed(this, 0);
	}
	public void stop(){
		running=false;
		thisHandler.removeCallbacks(this);
	}

	public void addFrameIntervalListener(FrameIntervalListener listener){
		frameIL=listener;
	}
	
	protected void startFrame() {
		timer.start();
		listener.onNewFrame();
		if(frame%frameIL.getInterval()==0){
			frameIL.OnFrameReached();
		}
	}

	protected void endFrame() {
		timer.reset();
		timer.start();
		frame++;
		listener.onEndFrame();
	}

	public void reset() {
		stop();
		timer.reset();
		frame = 0;
		listener.onReset();
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

	public void createSmartSleep(){
		try {
			Thread.sleep(getRemainingTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public void setFrameTimerListener(FrameTimerListener listener) {
		this.listener = listener;
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
		if(running){
			startFrame();
			endFrame();
			thisHandler.postDelayed(this, getRemainingTime());
		}
	}
}
