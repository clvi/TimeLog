package eu.leneurone.timelog.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TimePicker;

import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

/**
 * Fragment implementing a time picker
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private Time time;
    private Marker marker;
    private FragmentResultInterested listener;

    public void setTime(@NonNull Marker marker, @NonNull Time time) {
        this.marker = marker;
        this.time = time;
    }

    public void setMarker(@NonNull Marker marker) {
        this.marker = marker;
        this.time = new Time(marker.getHour(), marker.getMinute());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (FragmentResultInterested) getActivity();
        return new TimePickerDialog(getActivity(), this, time.getHour(), time.getMinute(), true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        listener.onTimeSet(this.marker, new Time(hourOfDay, minute));
    }

    public interface FragmentResultInterested {
        void onTimeSet(@NonNull Marker marker, @NonNull Time time);
    }

}
