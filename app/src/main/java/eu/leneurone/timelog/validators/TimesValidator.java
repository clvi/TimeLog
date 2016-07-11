package eu.leneurone.timelog.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

/**
 * Validates the coherency of a map of marker - time pairs
 */
public final class TimesValidator {

    private TimesValidator() {}

    /**
     * check if in a given list of markers and time pairs, the values are time-coherent
     * @param times the marker - time pairs
     * @return false if the map is empty or if after ordering the paris following the order of the Marker enum,
     * at least one pair of markers is not coherent (a later marker have a earlier value than an earlier marker).
     * true if all the markers are coherent.
     */
    public static boolean areMarkersCoherent(Map<Marker, Time> times) {
        // we wont save empty data
        if (times.isEmpty()) {
            return false;
        }

        // order the non-empty data following chronologic order of the markers
        List<Time> orderedData = new ArrayList<>(4);
        for (Marker marker : Marker.values()) {
            // the enum is read in the order of the enum values declaration, which is chronologic
            if (times.containsKey(marker)) {
                orderedData.add(times.get(marker));
            }
        }
        // compare each item to the previous one and return false at the first error found
        for (int i = 1; i < orderedData.size(); i++) {
            if (orderedData.get(i).compareTo(orderedData.get(i - 1)) < 0) {
                return false;
            }
        }
        // no error found : each marker value is greater or equal to the previous in the chronologic order
        return true;
    }
}
