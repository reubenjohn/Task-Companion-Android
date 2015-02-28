package com.aspirephile.taskcompanion.sound;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.taskcompanion.TaskCompanion;

@SuppressWarnings("UnusedDeclaration")
public class SoundManager {
    private static Logger l = new Logger(SoundManager.class);

    static WakeLock wakeLock;
    TaskCompanion T;

    public static class beepManager {
        private static boolean enabled = true;
        private static Handler handler;
        private static LapProgressSoundPool player;
        private static float decay = 2;
        private static int cutOff = 100;

        public static boolean isBeepsEnabled() {
            return enabled;
        }

    }

    public void initialize(Context context, TaskCompanion T) {

        wakeLock = ((PowerManager) context
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "TaskCompanion running wakeLock");
        beepManager.handler = new Handler();
        beepManager.player = new LapProgressSoundPool(context, wakeLock);
        this.T = T;
    }

    public void loadSettingsFromBundle(Bundle settings) {

        boolean newBeepEnabledValue = settings.getBoolean(
                TaskCompanion.keys.settings.sounds.lap_progress,
                TaskCompanion.defaults.sounds.lapProgress);
        if (newBeepEnabledValue != beepManager.enabled) {
            beepManager.enabled = newBeepEnabledValue;
            l.d("Lap progress sound setting changed to "
                    + newBeepEnabledValue);

        }

        if (T.isRunning()) {
            if (newBeepEnabledValue)
                SoundManager.postAllLapProgressSounds(
                        T.timerElements.getElapse(),
                        T.timerElements.getTargetTime());
            else
                SoundManager.removeAllLapProgressSounds();
        }
    }

    private static void postAllBeeps(long elapse, long targetTime) {
        long beep;
        String delayList = Boolean
                .toString(targetTime
                        - (beep = getBeepTime(elapse, targetTime, 1)) >= beepManager.cutOff)
                + "("
                + getBeepNumberAfter(elapse, targetTime)
                + ")"
                + "&&"
                + Boolean.toString(beep >= elapse) + ": ";
        for (int i = getBeepNumberAfter(elapse, targetTime); targetTime
                - (beep = getBeepTime(elapse, targetTime, i)) >= beepManager.cutOff; i++) {
            if (beep >= elapse) {
                beepManager.handler.postDelayed(beepManager.player.beep, beep
                        - elapse);
                delayList += (beep - elapse) + ",";

            }
        }
        l.d("Beeps are scheduled is " + delayList);
    }

    private static void removeAllBeeps() {
        beepManager.handler.removeCallbacks(beepManager.player.beep);
    }

    private static void postCutOffSound(long elapse, long targetTime) {
        long delay;
        if ((delay = targetTime - elapse) >= 0)
            beepManager.handler.postDelayed(beepManager.player.cutOff, delay);
    }

    private static void removeCutOffSound() {
        beepManager.handler.removeCallbacks(beepManager.player.cutOff);
    }

    public static void removeAllLapProgressSounds() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
            l.d("WakeLock released");
        }
        removeAllBeeps();
        removeCutOffSound();
    }

    public static long getBeepTime(long elapse, long targetTime, int beepNumber) {
        if (beepNumber == Integer.MAX_VALUE)
            return targetTime;
        else
            return (long) (targetTime - targetTime
                    / (Math.pow(beepManager.decay, (float) beepNumber)));
    }

    public static long getBeepDelay(long elapse, long targetTime, int beepNumber) {
        return getBeepTime(elapse, targetTime, beepNumber) - elapse;
    }

    public static int getBeepNumberAfter(long ms, long targetTime) {
        if (ms != targetTime)
            return (int) (Math.log(targetTime / (targetTime - ms)) / Math
                    .log(beepManager.decay)) + 1;
        else
            return Integer.MAX_VALUE;
    }

    public static long getBeepTimeAfter(long elapse, long targetTime, long ms) {
        if (ms != targetTime)
            return getBeepTime(elapse, targetTime,
                    getBeepNumberAfter(ms, targetTime));
        else
            return Long.MAX_VALUE;
    }

    public static long getBeepDelayAfter(long elapse, long targetTime, long ms) {
        if (ms <= targetTime)
            return getBeepTime(elapse, targetTime, (int) (Math.log(targetTime
                    / (targetTime - ms)) / Math.log(2)) + 1)
                    - elapse;
        else
            return Long.MAX_VALUE;
    }

    public static void postAllLapProgressSounds(long elapse, long targetTime) {
        l.d("WakeLock acquired");
        wakeLock.acquire();
        postAllBeeps(elapse, targetTime);
        postCutOffSound(elapse, targetTime);
    }

    public void onResume() {

    }

}
