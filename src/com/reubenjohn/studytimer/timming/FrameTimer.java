package com.reubenjohn.studytimer.timming;


public class FrameTimer {
	public static int defaultInterval = 20;
	private long frame = 0;
	private int interval = defaultInterval;

	private Timer timer;
	private FrameTimerListener listener;

	public FrameTimer() {
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
	}

	public void startFrame() {
		timer.start();
		listener.onNewFrame();
	}

	public void endFrame() {
		timer.reset();
		timer.start();
		frame++;
		listener.onEndFrame();
	}

	public void reset() {
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
}
