package com.aspirephile.taskcompanion.session.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.taskcompanion.R;
import com.aspirephile.taskcompanion.StudyTimer;
import com.aspirephile.shared.timming.Time;

public class SessionSetupLapDuration extends Fragment {
    Logger l = new Logger(SessionSetupLapDuration.class);
    NullPointerAsserter asserter = new NullPointerAsserter(
            SessionSetupFragment.class.getName());

    TimePicker lapDuration;
    long duration;

    public SessionSetupLapDuration() {
        l.d("Constructing " + SessionSetupLapDuration.class.toString());
        duration = StudyTimer.defaults.lapDuration;
        if (asserter.assertPointer(duration))
            l.d("Construction successful");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l.d("Creating " + SessionSetupLapDuration.class.toString());
        if (container == null)
            return null;

        l.d("Inflating view");
        View v = inflater.inflate(R.layout.fragment_lap_duration, container,
                false);

        bridgeXML(v);
        initializeFields();

        return v;
    }

    private void bridgeXML(View v) {
        l.d("Bridging XML");
        lapDuration = (TimePicker) v.findViewById(R.id.tp_lap_duration);

        if (asserter.assertPointer(lapDuration))
            l.d("Bridging sucessful");
    }

    public long getLapDuration() {
        if (asserter.assertPointer(lapDuration))
            return Time.getTimeInMilliseconds(0, 0,
                    lapDuration.getCurrentHour(),
                    lapDuration.getCurrentMinute(), 0);
        else
            return StudyTimer.defaults.lapDuration;
    }

    public void setLapDuration(long lapDuration) {
        l.d("Setting lap duration to " + lapDuration);
        duration = lapDuration;
        updateLapDuration();
    }

    private void initializeFields() {
        l.d("Initializing feilds");
        lapDuration.setIs24HourView(true);
        updateLapDuration();
    }

    private void updateLapDuration() {
        l.d("Updating lap duration to " + duration);
        if (asserter.assertPointer(this.lapDuration)) {
            this.lapDuration.setCurrentHour((int) Time.getMinutes(duration));
            this.lapDuration.setCurrentMinute((int) Time.getSeconds(duration));
            l.d("Update successful");
        }

    }

}
