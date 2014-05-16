package com.reubenjohn.studytimer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

import com.reubenjohn.studytimer.timming.FrameTimer;
import com.reubenjohn.studytimer.timming.FrameTimerListener;
import com.reubenjohn.studytimer.timming.Timer;

public class TimerView extends TextView implements FrameTimerListener {

	public Timer timer;
	private FrameTimer framer;

	public void commonConstruction(){
		timer=new Timer();
	}
	public TimerView(Context context) {
		super(context);
		commonConstruction();
	}

	public TimerView(Context context, FrameTimer frameTimer) {
		super(context);
		framer = frameTimer;
		commonConstruction();
	}

	public void updateText() {
		setText(timer.getFormattedTime());	
	}

	
public void reset() {
		timer.reset();
	}

	@Override
protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	if(framer==null)
		setText(timer.getFormattedTime());	
}

	@Override
	public void onNewFrame() {
		updateText();
	}

	@Override
	public void onEndFrame() {

	}

	@Override
	public void onReset() {

	}
}
