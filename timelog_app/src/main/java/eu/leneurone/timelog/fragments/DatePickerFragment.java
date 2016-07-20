package eu.leneurone.timelog.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Fragment implementing a date picker
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private Calendar calendar = Calendar.getInstance();
    private FragmentResultInterested listener;

    public void setInitialDate(Date date) {
        if(date != null) {
            calendar.setTime(date);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);

        listener = (FragmentResultInterested) getActivity();

        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        listener.onDateSet(calendar.getTime());
    }

    public interface FragmentResultInterested {
        void onDateSet(Date date);
    }

}
