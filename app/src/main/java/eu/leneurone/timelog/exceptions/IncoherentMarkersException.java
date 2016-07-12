package eu.leneurone.timelog.exceptions;

import eu.leneurone.timelog.model.Marker;

/**
 * exception for uncoherent marker - time pairs
 */
public class IncoherentMarkersException extends Exception {

    private Marker soonerMarker;
    private Marker laterMarker;

    public IncoherentMarkersException(Marker soonerMarker, Marker laterMarker) {
        this.soonerMarker = soonerMarker;
        this.laterMarker = laterMarker;
    }

    public Marker getSoonerMarker() {
        return soonerMarker;
    }

    public Marker getLaterMarker() {
        return laterMarker;
    }

    @Override
    public String getMessage() {
        if (laterMarker != null && soonerMarker != null) {
            return "Value of marker " + laterMarker + " is sooner than the value of marker " + soonerMarker;
        } else {
            return "Values of markers are incoherent (a later marker has a sooner value than a sooner marker)";
        }
    }
}