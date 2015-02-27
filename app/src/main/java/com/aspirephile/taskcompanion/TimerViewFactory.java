package com.aspirephile.taskcompanion;

import android.widget.TextView;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.shared.ui.TimerView;

@SuppressWarnings("UnusedDeclaration")
public class TimerViewFactory {
    private String defaultFormat = "%MM:%SS.%s";
    Logger l = new Logger(TimerViewFactory.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(String format) {
        if (asserter.assertPointer(defaultFormat))
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
