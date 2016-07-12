package eu.leneurone.timelog.validators;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.leneurone.timelog.exceptions.IncoherentMarkersException;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

/**
 * Validates the coherency of a map of marker - time pairs
 */
public final class TimesValidator {

    private TimesValidator() {
    }

    /**
     * check if in a given list of markers and time pairs, the values are time-coherent.
     *
     * @param times the marker - time pairs
     * @throws IncoherentMarkersException if after ordering the paris following the order of the Marker enum,
     *                                    at least one pair of markers is not coherent (a later marker have a earlier value than an earlier marker).
     */
    public static void validateMarkersCoherency(@NonNull Map<Marker, Time> times) throws IncoherentMarkersException {
        // order the non-empty data following chronologic order of the markers
        List<Time> orderedData = new ArrayList<>(4);
        // the ordered list of markers is used to build the exception in case of error
        List<Marker> orderedMarkers = new ArrayList<>(4);
        for (Marker marker : Marker.values()) {
            // the enum is read in the order of the enum values declaration, which is chronologic
            if (times.containsKey(marker)) {
                orderedData.add(times.get(marker));
                orderedMarkers.add(marker);
            }
        }
        // compare each item to the previous one and return false at the first error found
        for (int i = 1; i < orderedData.size(); i++) {
            // the index i is the later marker, index i-1 the earlier
            if (orderedData.get(i).compareTo(orderedData.get(i - 1)) < 0) {
                throw new IncoherentMarkersException(orderedMarkers.get(i - 1), orderedMarkers.get(i));
            }
        }
        // no error found : each marker value is greater or equal to the previous in the chronologic order
    }
}
