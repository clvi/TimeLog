package eu.leneurone.timelog.model;

import java.io.Serializable;

/**
 * represents the 4 interesting times for work time tracking : morning, lunch start, lunch end
 * and day end
 */
public enum Marker implements Serializable {
    /**
     * arrival time in the morning
     */
    MORNING(9, 0),
    /**
     * lunch time start
     */
    LUNCH_START(12, 0),
    /**
     * lunch time end
     */
    LUNCH_END(13, 0),
    /**
     * leaving to home
     */
    EVENING(18, 0);

    private int hour;
    private int minute;

    Marker(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}