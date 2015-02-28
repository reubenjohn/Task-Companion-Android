package com.aspirephile.taskcompanion;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.taskcompanion.TaskCompanion.defaults;
import com.aspirephile.taskcompanion.data.LapsCursorAdapter;
import com.aspirephile.taskcompanion.data.TaskCompanionDBManager;
import com.aspirephile.taskcompanion.preferences.STSP;

import java.util.Locale;

@SuppressWarnings("UnusedDeclaration")
public class LapsFragment extends Fragment implements
        TimerElementsFragment.TimerElementsListener {
    private Logger l = new Logger(LapsFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    private LapsCursorAdapter adapter;
    private TaskCompanionDBManager STDB;
    private TaskCompanionDBManager.LapDBManager lapsDB;
    ListView listView;
    TextView currentLap;
    ProgressBar lapProgress;
    String formatedAverage = null;

    long average = 0;
    TextToSpeech tts;
    String lapUtteranceId = "lapUtteranceId:";

    private class Cache {
        int totalLaps;
        int lapCount;
        int totalLapProgressPercentage;


        public void clear() {
            lapCount = 0;
        }

        public void resetSession() {
            lapCount = 0;
            totalLapProgressPercentage = 0;
        }
    }

    Cache cache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.laps_fragment, container, false);
        STDB = new TaskCompanionDBManager(getActivity().getApplicationContext());
        lapsDB = STDB.lapsDB.open();
        bridgeXML(v);
        initializeFields();
        generateListView();
        return v;
    }

    protected void bridgeXML(View v) {
        listView = (ListView) v.findViewById(R.id.lv_laps);
        currentLap = (TextView) v.findViewById(R.id.tv_current_lap);
        lapProgress = (ProgressBar) v.findViewById(R.id.pb_total_lap_progress);
    }

    protected void initializeFields() {
        Cache cache = new Cache();
        cache.lapCount = getLapCount();
        updateCurrentLap(cache.lapCount);
        lapProgress.getProgressDrawable().setColorFilter(Color.CYAN,
                Mode.SRC_IN);

        tts = new TextToSpeech(getActivity(),
                new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            tts.setLanguage(Locale.US);
                        }
                    }
                });

    }

    protected void generateListView() {
        // TODO Use Cursor loaders to prevent possible janks
        // http://developer.android.com/guide/topics/ui/layout/listview.html#Loader

        Cursor cursor = lapsDB.fetchAllLaps();
        /*
         * adapter = new SimpleCursorAdapter(getActivity(),
		 * R.layout.laps_list_item, cursor,
		 * TaskCompanionDBManager.LapDBProperties.columns,
		 * TaskCompanionManager.LapDBProperties.to);
		 */
        adapter = new LapsCursorAdapter(getActivity(), cursor, 0);
        listView.setAdapter(adapter);
    }

    public void resetSession() {
        l.d("LapsFragment resetSession");
        lapsDB.reset();
        cache.resetSession();
        average = 0;
        formatedAverage = getResources().getString(
                R.string.intitial_accurate_time);
        updateCurrentLap(0);
    }

    public void reset() {
        l.d("LapsFragment reset");
        lapsDB.reset();
        cache.clear();
        average = 0;
        formatedAverage = getResources().getString(
                R.string.intitial_accurate_time);
        updateCurrentLap(0);
    }

    public boolean hasNoLaps() {
        return cache.lapCount == 0;
    }

    public boolean addLap(long newElapsed) {
        lapsDB.addLap(newElapsed);
        generateListView();
        cache.lapCount++;
        updateCurrentLap(cache.lapCount);
        if (getLapCount() >= getTotalLapCount()) {
            l.e("Lap count greater than max!");
        } else {
            int lapUtteranceCount = getLapCount() + 1;
            CharSequence speech = Integer.toString(lapUtteranceCount);
            if (getAverage() > TaskCompanion.defaults.Speech.LapIncludedSpeech) {
                speech = "Lap " + speech;
            }
            l.d("About to speak: " + speech);
            if (Build.VERSION.SDK_INT >= 21) {
                tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, (lapUtteranceId + lapUtteranceCount));
                // TODO setOnUtteranceProgressListener(UtteranceProgressListener) (http://developer.android.com/reference/android/speech/tts/TextToSpeech.html#speak(java.lang.CharSequence, int, android.os.Bundle, java.lang.String)) to keep track and hence determine if subsequent laps have to be uttered
            } else
                //noinspection deprecation
                tts.speak(speech.toString(), TextToSpeech.QUEUE_FLUSH, null);
        }
        return cache.lapCount == 1;
    }

    protected void updateCurrentLap(int lapCount) {
        l.d("Updating currentLap to : " + (lapCount + 1));
        currentLap.setText(Integer.toString(lapCount + 1));
        cache.totalLapProgressPercentage = lapCount;
        if (cache.totalLaps != 0) {
            cache.totalLapProgressPercentage = (int) (lapCount * 100.f / cache.totalLaps);
            l.d("cache.totalLapProgressPercentage = (int) ("
                    + lapCount + " * 100.f / " + cache.totalLaps + ") = "
                    + cache.totalLapProgressPercentage);
        }

        updateLapProgressBar(cache.totalLapProgressPercentage);

    }

    protected void updateLapProgressBar(int totalLapProgressPercentage) {
        if (asserter.assertPointer(lapProgress)) {
            l.d("Updating lap progress bar to "
                    + totalLapProgressPercentage + "%");
            if (android.os.Build.VERSION.SDK_INT >= 11)
                updateLapProgressBarHoneyCombStyle(totalLapProgressPercentage);
            else
                lapProgress.setProgress(totalLapProgressPercentage); // update
            // GingerBread
            // style
        }

    }

    /*
     * will update the "progress" propriety of SeekBar until it reaches progress
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    protected void updateLapProgressBarHoneyCombStyle(int lapCount) {
        ObjectAnimator animation = ObjectAnimator.ofInt(lapProgress,
                "progress", lapCount);
        animation.setDuration(getAverage());
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

    }

    public int getLapCount() {
        return lapsDB.getLapCount();
    }

    public int getTotalLapCount() {
        return cache.totalLaps;
    }

    public void setTotalLapCount(int totalLaps) {
        l.d("Total lap count set to " + totalLaps);
        cache.totalLaps = totalLaps;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lapsDB.close();
    }

    public int getAverage() {
        return lapsDB.getAverage();
    }

    public String getFormattedAverage() {
        return lapsDB.getFormattedAverage();
    }

    @Override
    public void onTotalElapseSetManually(long elapse) {
        lapsDB.distributeToLaps(elapse);
    }

    public void createNewSession(Bundle sessionInfo) {
        if (asserter.assertPointer(sessionInfo)) {
            setTotalLapCount(sessionInfo.getInt(STSP.keys.totalLaps,
                    defaults.totalLaps));
        }
    }

    public void putSessionInfo(Bundle sessionInfo) {
        sessionInfo.putInt(STSP.keys.totalLaps, getTotalLapCount());
    }

    public void loadSessionFromBundle(Bundle sessionInfo) {
        cache.totalLaps = sessionInfo.getInt(STSP.keys.totalLaps, defaults.totalLaps);
    }

}
