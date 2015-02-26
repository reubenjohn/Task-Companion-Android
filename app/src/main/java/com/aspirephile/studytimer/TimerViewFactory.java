package com.aspirephile.studytimer;

import android.widget.TextView;

import com.aspirephile.shared.ui.TimerView;

public class TimerViewFactory {
    private String defaultFormat = "%MM:%SS.%s";

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(String format) {
        if (format != null)
            defaultFormat = format;
    }

    public TimerView generateTimerView() {
        TimerView timerView = new TimerView();
        timerView.setFormat(defaultFormat);
        return timerView;
    }

    public TimerView produceTimerView(TextView textView) {
        TimerView timerView = new TimerView();
        timerView.setFormat(defaultFormat);
        timerView.setTextView(textView);
        return timerView;
    }

}
