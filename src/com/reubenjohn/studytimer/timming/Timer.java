package com.reubenjohn.studytimer.timming;

import android.os.Bundle;


public class Timer {
	static String defaultFormat="%HH:%MM:%SS.$sss";
	private String format=defaultFormat;
	private long initial = 0, cummulative = 0;
	boolean running = false;

	public void reset() {
		initial = cummulative = 0;
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
			cummulative += (System.currentTimeMillis() - initial);
		running = false;
	}

	public void start() {
		if (!running)
			initial = System.currentTimeMillis();
		running = true;
	}

	public void toggle() {
		if (running)
			stop();
		else
			start();
	}

	// TODO create method to manually set timer to a certain value (setElapse)
	// **remember to set initial to currentTime...**
	
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
			return cummulative + (System.currentTimeMillis() - initial);
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
		outState.putLong("Timer.initial", initial);
		outState.putLong("Timer.cummulative", cummulative);
		outState.putBoolean("Timer.running", running);
	}

	public void setElapse(long elapse) {
		cummulative=elapse;
		initial=System.currentTimeMillis();
	}

	public void setStartTime(long startTime) {
		initial=startTime;		
	}

}
