package de.stonecs.android.lockcontrol.util;

/**
 * Created by Daniel on 23.09.13.
 */
public class TimeunitConversionUtil {

    private static final int HOURS_TO_SECONDS = 3600;
    private static final int MINUTES_TO_SECONDS = 60;

    public static String getStringRepresentationFor(int duration) {
        int h = duration / HOURS_TO_SECONDS;
        int m = (duration - (h * HOURS_TO_SECONDS)) / MINUTES_TO_SECONDS;
        int s = (duration - (h * HOURS_TO_SECONDS)) % MINUTES_TO_SECONDS;
        return String.format("%02d:%02d:%02d hh:mm:ss", h, m, s);
    }

    public static int getSecondsFor(int hours, int minutes, int seconds) {
        return (hours * HOURS_TO_SECONDS) + (minutes * MINUTES_TO_SECONDS) + seconds;
    }
}
