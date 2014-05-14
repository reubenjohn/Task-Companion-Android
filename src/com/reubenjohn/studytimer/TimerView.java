package com.reubenjohn.studytimer;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

public class TimerView extends TextView {

	public Timer timer;

	public TimerView(Context context) {
		super(context);
		timer = new Timer();
	}

	public TimerView(Context context, Timer customTimer) {
		super(context);
		if (customTimer == null)
			timer = new Timer();
		else
			timer = customTimer;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		setText(timer.getFormattedTime());
		super.onDraw(canvas);
	}

	public void reset() {
		timer.reset();
	}
}
