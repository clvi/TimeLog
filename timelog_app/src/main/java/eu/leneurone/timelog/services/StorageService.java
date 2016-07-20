package eu.leneurone.timelog.services;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Date;
import java.util.Map;

import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

/**
 * Storage service, handling marker and time data
 */
public interface StorageService {
    /**
     * Stores the worklog for a day
     * @param day the day (not null)
     * @param times the worklog (not null)
     * @param context the application context (not null)
     */
    void storeDayWorklog(@NonNull Date day, @NonNull Map<Marker, Time> times, @NonNull Context context);

    /**
     * Read the stored worklog for a day
     * @param day the day (not null)
     * @param context the context (not null)
     * @return the read data, or an empty if no data exists for the given day, or if it couldn't have been read.
     */
    @NonNull
    Map<Marker, Time> loadDayWorklog(@NonNull Date day, @NonNull Context context);
}
