package eu.leneurone.timelog.exceptions;

import eu.leneurone.timelog.model.Marker;

/**
 * exception for uncoherent marker - time pairs
 */
public class IncoherentMarkersException extends Exception {

    private Marker earliest;
    private Marker latest;

    public IncoherentMarkersException(Marker earliest, Marker latest) {
        this.earliest = earliest;
        this.latest = latest;
    }

    public Marker getEarliest() {
        return earliest;
    }

    public Marker getLatest() {
        return latest;
    }

    @Override
    public String getMessage() {
        if (latest != null && earliest != null) {
            return "Value of marker " + latest + " is sooner than the value of marker " + earliest;
        } else {
            return "Values of markers are incoherent (a later marker has a sooner value than a sooner marker)";
        }
    }

    @Override
    public String toString() {
        return "IncoherentMarkersException{" +
                "earliest=" + earliest +
                ", latest=" + latest +
                '}';
    }
}