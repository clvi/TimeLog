package eu.leneurone.timelog.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * encapsulates a marker, and the hour and minutes chosen by the user
 * WARNING the order of the item in the declaration is important and must be chronologic, as
 * this is used to validate the data coherency before save
 */
public class Time implements Comparable<Time>, Serializable {
    /** the hour */
    private Integer hour;

    /** the minutes */
    private Integer minute;

    public Time(Integer hour, Integer minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getMinute() {
        return minute;
    }

    @Override
    public String toString() {
        return hour + ":" + minute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Time time = (Time) o;
        return getHour().equals(time.getHour()) && getMinute().equals(time.getMinute());

    }

    @Override
    public int hashCode() {
        int result = getHour().hashCode();
        result = 31 * result + getMinute().hashCode();
        return result;
    }

    @Override
    public int compareTo(@NonNull Time another) {
        if(this.equals(another)) {
            return 0;
        }

        if(this.getHour() > another.getHour()) {
            return 1;
        } else if (this.getHour() < another.getHour()) {
            return -1;
        } else {
            // hours are equals
            if(this.getMinute() > another.getMinute()) {
                return 1;
            } else {
                // the minutes can't be equals, or the method equals called before would have return
                // true and this code wouldn't have been executed.
                return -1;
            }
        }
    }


}
