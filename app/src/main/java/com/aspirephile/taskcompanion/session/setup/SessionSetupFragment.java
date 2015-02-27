package com.aspirephile.taskcompanion.session.setup;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.taskcompanion.R;
import com.aspirephile.taskcompanion.StudyTimer;
import com.aspirephile.taskcompanion.StudyTimer.defaults;
import com.aspirephile.taskcompanion.data.SessionParams;
import com.aspirephile.taskcompanion.preferences.STSP;

import java.util.List;
import java.util.Vector;

public class SessionSetupFragment extends Fragment implements OnClickListener,
        OnLongClickListener, OnTouchListener, OnPageChangeListener {
    Logger l = new Logger(SessionSetupFragment.class);
    NullPointerAsserter asserter = new NullPointerAsserter(
            SessionSetupFragment.class.getName());
    Button totalLapsIncrement, totalLapsDecrement;
    EditText totalLaps;
    SessionSetupLapDuration lapDuration;
    SessionSetupSessionDuration sessionDuration;
    private ViewPager durationPager;
    private DurationPageAdapter durationPageAdapter;
    private IterativeUpdater iterativeUpdater;
    private Handler iterativeUpdateHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l.d("Creating " + SessionSetupFragment.class.toString());
        l.d("Inflating layout");
        View v = inflater.inflate(R.layout.fragment_session_setup, container,
                false);
        bridgeXML(v);
        initializeFeilds();
        return v;
    }

    private void bridgeXML(View v) {
        l.d("Bridging XML");
        durationPager = (ViewPager) v
                .findViewById(R.id.vp_session_setup_duration);
        totalLapsIncrement = (Button) v
                .findViewById(R.id.b_total_laps_increment);
        totalLapsDecrement = (Button) v
                .findViewById(R.id.b_total_laps_decrement);
        totalLaps = (EditText) v.findViewById(R.id.et_total_laps);
        if (asserter.assertPointer(durationPager, totalLapsIncrement,
                totalLapsDecrement, totalLaps))
            l.d("Bridging successful");
    }

    private void initializeFeilds() {
        l.d("Initializing feilds");
        lapDuration = new SessionSetupLapDuration();
        sessionDuration = new SessionSetupSessionDuration();
        iterativeUpdater = new IterativeUpdater();
        iterativeUpdateHandler = new Handler();
        totalLapsIncrement.setOnClickListener(this);
        totalLapsIncrement.setOnLongClickListener(this);
        totalLapsIncrement.setOnTouchListener(this);
        totalLapsDecrement.setOnClickListener(this);
        totalLapsDecrement.setOnLongClickListener(this);
        totalLapsDecrement.setOnTouchListener(this);
        totalLaps.setText(String.valueOf(StudyTimer.defaults.totalLaps));
        initializePaging();
        applySessionParams(getSessionParamsFromPreferences());
    }

    private void applySessionParams(SessionParams params) {
        if (asserter.assertPointer(params)) {
            setTotalLaps(params.getTotalLaps());
            setLapDuration(params.getLapDuration());
        }
    }

    private void setLapDuration(long lapDuration) {
        this.lapDuration.setLapDuration(lapDuration);

    }

    private SessionParams getSessionParamsFromPreferences() {
        SharedPreferences sessionPrefs = getActivity().getSharedPreferences(
                STSP.fileNames.currentSession, Context.MODE_PRIVATE);
        long lapDuration = sessionPrefs.getLong(STSP.keys.targetTime,
                StudyTimer.defaults.lapDuration);
        int totalLaps = sessionPrefs.getInt(STSP.keys.totalLaps,
                defaults.totalLaps);
        Log.d("SessionSetupFragment", "Retreived lapDuration=" + lapDuration);
        Log.d("SessionSetupFragment", "Retreived totalLaps=" + totalLaps);
        SessionParams params = new SessionParams();
        params.setLapDuration(lapDuration);
        params.setTotalLaps(totalLaps);
        return params;

    }

    private void initializePaging() {
        List<Fragment> fragments = new Vector<>();
        // TODO remember to change the FragmentPosition constants if changing this
        fragments.add(lapDuration);
        fragments.add(sessionDuration);

        durationPageAdapter = new DurationPageAdapter(getActivity()
                .getSupportFragmentManager(), fragments);

        Resources res = getResources();
        durationPageAdapter.setPageTitles(
                res.getString(R.string.session_create_target_title),
                res.getString(R.string.session_create_total_time_title));
        durationPager.setOnPageChangeListener(this);
        durationPager.setAdapter(durationPageAdapter);
    }

    private int getTotalLaps() {
        String raw = totalLaps.getText().toString();
        int processed;
        try {
            processed = Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            processed = StudyTimer.defaults.totalLaps;
        } catch (Exception e) {
            e.printStackTrace();
            processed = StudyTimer.defaults.totalLaps;
        }
        if (processed < StudyTimer.prefs.minLaps)
            return StudyTimer.prefs.minLaps;
        if (processed > StudyTimer.prefs.maxLaps)
            return StudyTimer.prefs.maxLaps;
        return processed;

    }

    private void setTotalLaps(int totalLaps) {
        if (totalLaps <= StudyTimer.prefs.minLaps)
            totalLaps = StudyTimer.prefs.minLaps;
        else if (totalLaps >= StudyTimer.prefs.maxLaps)
            totalLaps = StudyTimer.prefs.maxLaps;
        this.totalLaps.setText(String.valueOf(totalLaps));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.b_total_laps_increment:
                totalLaps.setText(String.valueOf(incrementTotalLaps()));
                break;
            case R.id.b_total_laps_decrement:
                decrementTotalLaps();
                totalLaps.setText(String.valueOf(decrementTotalLaps()));
                break;
            default:
                Log.d(SessionSetupFragment.class.getName(), "Unknown item clicked");
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.b_total_laps_increment:
                l.d("Increment button long clicked");
                iterativeUpdater.mode = LapIteratorMode.INCREMENT;
                iterativeUpdateHandler.post(iterativeUpdater);
                break;
            case R.id.b_total_laps_decrement:
                l.d("Decrement button long clicked");
                iterativeUpdater.mode = LapIteratorMode.DECREMENT;
                iterativeUpdateHandler.post(iterativeUpdater);
                break;
            default:
                l.d("Unknown item clicked");
                break;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            switch (v.getId()) {
                case R.id.b_total_laps_increment:
                case R.id.b_total_laps_decrement:
                    l.d("Increment/Decrement button up");
                    iterativeUpdateHandler.removeCallbacks(iterativeUpdater);
                    totalLaps.setSelection(totalLaps.getText().length());
                    break;
                default:
                    l.d("Unexpected view has ACTION_UP");
                    break;
            }
        return false;
    }

    private int decrementTotalLaps() {
        int currentTotalLaps = getTotalLaps();
        if (currentTotalLaps <= StudyTimer.prefs.minLaps)
            return StudyTimer.prefs.minLaps;
        return --currentTotalLaps;

    }

    private int incrementTotalLaps() {

        int currentTotalLaps = getTotalLaps();
        if (currentTotalLaps >= StudyTimer.prefs.maxLaps)
            return StudyTimer.prefs.maxLaps;
        return ++currentTotalLaps;

    }

    public SessionParams getSessionParams() {
        SessionParams sessionParams = new SessionParams();
        sessionParams.setTotalLaps(getTotalLaps());
        sessionParams.setLapDuration(lapDuration.getLapDuration());
        return sessionParams;
    }

    @Override
    public void onPageSelected(int position) {
        l.d("Page " + position + " selected");
        DurationPagerProperties.position = position;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // l.d("Page " + position + " scrolled with position offset: "
        // + positionOffset);

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        l.d("Page state changed to " + state);
        if (state == 1) {
            long value;
            switch (DurationPagerProperties.position) {
                case DurationPagerProperties.FragmentPositions.lapDuration:
                    value = lapDuration.getLapDuration() * getTotalLaps();
                    l.d("Session duration set to: " + value);
                    sessionDuration.setSessionDuration(value);
                    break;
                case DurationPagerProperties.FragmentPositions.sessionDuration:
                    value = sessionDuration.getSessionDuration() / getTotalLaps();
                    l.d("Setting Lap duration to: " + value);
                    lapDuration.setLapDuration(value);
                    break;
                default:
                    l.d("Unknown case: " + DurationPagerProperties.position
                            + " selected");
                    break;
            }
        }
    }

    private static enum LapIteratorMode {
        INCREMENT, DECREMENT
    }

    private static class DurationPagerProperties {
        static int position;

        public static class FragmentPositions {

            protected static final int lapDuration = 0;
            protected static final int sessionDuration = 1;

        }
    }

    private class IterativeUpdater implements Runnable {
        public LapIteratorMode mode = LapIteratorMode.INCREMENT;

        @Override
        public void run() {
            if (mode == LapIteratorMode.INCREMENT) {
                totalLaps.setText(String.valueOf(incrementTotalLaps()));
                totalLaps.setSelection(totalLaps.getText().length());
                iterativeUpdateHandler.postDelayed(this,
                        StudyTimer.prefs.totalLapsLongPressIterationDelay);
            } else if (mode == LapIteratorMode.DECREMENT) {
                totalLaps.setText(String.valueOf(decrementTotalLaps()));
                iterativeUpdateHandler.postDelayed(this,
                        StudyTimer.prefs.totalLapsLongPressIterationDelay);
            }

        }
    }

}
