package com.reubenjohn.studytimer;

public class Timer {
	private long Initial = 0, cummulative = 0;
	boolean running = false;

	public void reset() {
		Initial = cummulative = 0;
		running = false;
	}

	Timer() {
		reset();
	}
	Timer(long startWith) {
		reset();
		cummulative=startWith;
	}

	public void stop() {
		if (running)
			cummulative += (System.currentTimeMillis() - Initial);
		running = false;
	}

	public void start() {
		if (!running)
			Initial = System.currentTimeMillis();
		running = true;
	}

	public void toggle() {
		if (running)
			stop();
		else
			start();
	}

	// TODO create method to manually set timer to a certain value (setElapse)
	// **remember to set Initial to currentTime...**

	public long getElapse() {
		if (running)
			return cummulative + (System.currentTimeMillis() - Initial);
		else
			return cummulative;
	}

	public String getFormattedTime() {
		return Time.getFormattedTime("%HH:%MM:%SS.$sss", getElapse());
	}
	
	public String getFormattedTime(String format) {
		return Time.getFormattedTime(format, getElapse());
	}

	public boolean isRunning() {
		return running;
	}

}
