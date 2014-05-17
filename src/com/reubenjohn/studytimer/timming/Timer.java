package com.reubenjohn.studytimer.timming;

import android.os.Bundle;


public class Timer {
	static String defaultFormat="%HH:%MM:%SS.$sss";
	private String format=defaultFormat;
	private long Initial = 0, cummulative = 0;
	boolean running = false;

	public void reset() {
		Initial = cummulative = 0;
		running = false;
	}

	public Timer() {
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
	
	/*
	 * Change the format of the Formatted String Eg:"%HH:%MM:%SS.$sss" 
	 */
	public void setFormat(String format){
		this.format=format;
	}

	public static void setDefulatFormat(String format){
		defaultFormat=format;
	}
	
	public long getElapse() {
		if (running)
			return cummulative + (System.currentTimeMillis() - Initial);
		else
			return cummulative;
	}

	public String getFormattedTime() {
		return Time.getFormattedTime(format, getElapse());
	}
	
	public String getFormattedTime(String format) {
		return Time.getFormattedTime(format, getElapse());
	}

	public boolean isRunning() {
		return running;
	}

	public void saveStateToBundle(Bundle outState){
		outState.putLong("Timer.Initial", Initial);
		outState.putLong("Timer.cummulative", cummulative);
		outState.putBoolean("Timer.running", running);
	}
}
