package eu.leneurone.timelog.math;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Map;

import eu.leneurone.timelog.exceptions.IncoherentMarkersException;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;
import eu.leneurone.timelog.validators.TimesValidator;

/**
 * compute the total work time using the given markers and times
 */
public class TotalCalculator {

    /**
     * default lunch duration in minutes
     */
    public static final int DEFAULT_LUNCH_DURATION_IN_MINUTES = 60;

    private TotalCalculator() {
    }

    public static Time calculateTotalTime(@NonNull Calendar theDay, @NonNull Map<Marker, Time> times) throws IncoherentMarkersException {
        if (times.isEmpty()) {
            // fail fast
            return new Time(0, 0);
        }

        TimesValidator.validateMarkersCoherency(times);

        // compute work time (in minutes)
        int total = 0;

        Calendar today = Calendar.getInstance();

        if (times.size() == 4) {
            // the 4 markers are set : easy
            // morning = lunch_start - morning
            // afternoon = evening - lunch_end
            // total = morning + afternoon
            total = diffInMinutes(times.get(Marker.LUNCH_START), times.get(Marker.MORNING))
                    + diffInMinutes(times.get(Marker.EVENING), times.get(Marker.LUNCH_END));
        } else if (times.size() == 2
                && times.containsKey(Marker.MORNING)
                && times.containsKey(Marker.EVENING)) {
            // just start and end : we remove 1 hour of standard lunch break
            // total = evening - morning - 1 hour
            total = diffInMinutes(times.get(Marker.EVENING), times.get(Marker.MORNING)) - DEFAULT_LUNCH_DURATION_IN_MINUTES;
        } else if (times.size() == 2
                && times.containsKey(Marker.MORNING)
                && times.containsKey(Marker.LUNCH_START)) {
            // the morning markers are set : total = lunch_start - morning
            total = diffInMinutes(times.get(Marker.LUNCH_START), times.get(Marker.MORNING));
        } else if (times.size() == 3
                && times.containsKey(Marker.MORNING)
                && times.containsKey(Marker.LUNCH_START)
                && !isToday(today, theDay)) {
            // the morning markers and one of the evening markers are set, and we're not today :
            // total = lunch_start - morning
            total = diffInMinutes(times.get(Marker.LUNCH_START), times.get(Marker.MORNING));
        } else if (isToday(today, theDay)) {
            // try some things only if the day is today
            if (times.size() == 1 && times.containsKey(Marker.MORNING)) {
                if (today.get(Calendar.HOUR_OF_DAY) < 13) {
                    // we are still in the morning : total = now - morning
                    total = diffInMinutes(calendarToTime(today), times.get(Marker.MORNING));
                } else {
                    // we are in the afternoon, so we remove 1 hour of standard lunch break
                    // total = now - morning - 1 hour
                    total = diffInMinutes(calendarToTime(today), times.get(Marker.MORNING)) - DEFAULT_LUNCH_DURATION_IN_MINUTES;
                }
            } else if (times.size() == 3
                    && times.containsKey(Marker.MORNING)
                    && times.containsKey(Marker.LUNCH_START)
                    && times.containsKey(Marker.LUNCH_END)) {
                // we are in the afternoon, and all the previous markers are set :
                // morning = lunch_start - morning
                // afternoon = now - lunch_end
                // total = morning + afternoon
                total = diffInMinutes(times.get(Marker.LUNCH_START), times.get(Marker.MORNING))
                        + diffInMinutes(calendarToTime(today), times.get(Marker.LUNCH_END));
            }
        }
        return minutesToTime(total);
    }

    private static boolean isToday(@NonNull Calendar today, @NonNull Calendar theDay) {
        return theDay.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && theDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    // builds a Time from the hours and minutes of the calendar
    private static Time calendarToTime(@NonNull Calendar calendar) {
        return new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    // calculates the difference in minutes between 2 times
    private static int diffInMinutes(@NonNull Time later, @NonNull Time earlier) {
        return timeToMinutesFromMidnight(later) - timeToMinutesFromMidnight(earlier);
    }

    // convert a Time into minutes from midnight
    private static int timeToMinutesFromMidnight(@NonNull Time time) {
        return time.getHour() * 60 + time.getMinute();
    }

    // convert a number of minutes to a time (with hour and minutes)
    @NonNull
    private static Time minutesToTime(int minutes) {
        return new Time(minutes / 60, minutes % 60);
    }
}
