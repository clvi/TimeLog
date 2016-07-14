package eu.leneurone.timelog.utils;

import android.support.annotation.NonNull;

import java.util.Locale;

import eu.leneurone.timelog.model.Time;

/**
 * Utils for manipulating Time objects
 */
public class TimeUtils {

    /**
     * Format a time object for display
     * @param time the time
     * @return the string, using format hh:mm
     */
    public static String formatTime(@NonNull Time time) {
        return String.format(Locale.FRANCE, "%02d:%02d", time.getHour(), time.getMinute());
    }
}
