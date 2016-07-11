package eu.leneurone.timelog.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import eu.leneurone.timelog.R;
import eu.leneurone.timelog.fragments.DatePickerFragment;
import eu.leneurone.timelog.fragments.TimePickerFragment;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;
import eu.leneurone.timelog.services.StorageService;
import eu.leneurone.timelog.services.impl.StorageServiceImpl;
import eu.leneurone.timelog.validators.TimesValidator;

/**
 * Main screen of the app
 */
public class MainActivity extends Activity implements DatePickerFragment.FragmentResultInterested, TimePickerFragment.FragmentResultInterested {

    /**
     * Stores the chosen date
     */
    private Calendar calendar;
    /**
     * Displays the current or chosen date
     */
    private TextView tvDisplayDate;
    /**
     * stores the chosen times for the 4 markers
     */
    Map<Marker, Time> times = new HashMap<>(4);

    /** storage service */
    StorageService service = new StorageServiceImpl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();

        // init the displayed date to the current day
        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
        setTvDisplayDate(calendar.getTime());

        loadSavedData();

        configureButtons();
    }

    @Override
    public void onDateSet(Date date) {
        calendar.setTime(date);
        setTvDisplayDate(date);
        clearTimes();
        loadSavedData();
    }

    private void loadSavedData() {
        times = service.loadDayWorklog(calendar.getTime(), getApplicationContext());
        for (Map.Entry<Marker, Time> timeEntry : times.entrySet()) {
            displayTime(timeEntry.getKey(), timeEntry.getValue());
        }
    }

    private void clearTimes() {
        TextView tvMorningTime = (TextView) findViewById(R.id.morningTime);
        tvMorningTime.setText("");
        TextView tvLunchStart = (TextView) findViewById(R.id.lunchStartTime);
        tvLunchStart.setText("");
        TextView tvLunchEnd = (TextView) findViewById(R.id.lunchEndTime);
        tvLunchEnd.setText("");
        TextView tvEvening = (TextView) findViewById(R.id.eveningTime);
        tvEvening.setText("");
        times.clear();
    }

    @Override
    public void onTimeSet(@NonNull Marker marker, @NonNull Time time) {
        times.put(marker, time);
        displayTime(marker, time);
    }

    private void displayTime(Marker marker, Time time) {
        switch (marker) {
            case MORNING:
                TextView tvMorningTime = (TextView) findViewById(R.id.morningTime);
                tvMorningTime.setText(String.format(Locale.FRANCE, "%02d:%02d", time.getHour(), time.getMinute()));
                break;
            case LUNCH_START:
                TextView tvLunchStart = (TextView) findViewById(R.id.lunchStartTime);
                tvLunchStart.setText(String.format(Locale.FRANCE, "%02d:%02d", time.getHour(), time.getMinute()));
                break;
            case LUNCH_END:
                TextView tvLunchEnd = (TextView) findViewById(R.id.lunchEndTime);
                tvLunchEnd.setText(String.format(Locale.FRANCE, "%02d:%02d", time.getHour(), time.getMinute()));
                break;
            case EVENING:
                TextView tvEvening = (TextView) findViewById(R.id.eveningTime);
                tvEvening.setText(String.format(Locale.FRANCE, "%02d:%02d", time.getHour(), time.getMinute()));
                break;
            default:
                break;
        }
    }

    private void setTvDisplayDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        tvDisplayDate.setText(format.format(date));
    }

    private void configureButtons() {
        Button btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO display a confirmation popup if times have been set and not saved,
                // TODO as the time will be cleared when a new date will be chosen
                DatePickerFragment picker = new DatePickerFragment();
                picker.setInitialDate(calendar.getTime());
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        configureTimePickerButton(R.id.btnChangeMorningTime, Marker.MORNING);
        configureTimePickerButton(R.id.btnChangeLunchStartTime, Marker.LUNCH_START);
        configureTimePickerButton(R.id.btnChangeLunchEndTime, Marker.LUNCH_END);
        configureTimePickerButton(R.id.btnChangeEveningTime, Marker.EVENING);

        configureButtonNow(R.id.btnMorningTimeNow, Marker.MORNING);
        configureButtonNow(R.id.btnLunchStartTimeNow, Marker.LUNCH_START);
        configureButtonNow(R.id.btnLunchEndNow, Marker.LUNCH_END);
        configureButtonNow(R.id.btnEveningTimeNow, Marker.EVENING);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvValidation = (TextView) findViewById(R.id.validation);
                if (TimesValidator.areMarkersCoherent(times)) {
                    // clear previous validation error (if any)
                    tvValidation.setText("");
                    // store the data
                    service.storeDayWorklog(calendar.getTime(), times, getApplicationContext());
                } else {
                    tvValidation.setText(R.string.error_incoherent_markers);
                }
            }
        });

    }

    private void configureTimePickerButton(int buttonId, final Marker marker) {
        Button btnChangeMorningTime = (Button) findViewById(buttonId);
        btnChangeMorningTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment picker = new TimePickerFragment();
                if (times.containsKey(marker)) {
                    picker.setTime(marker, times.get(marker));
                } else {
                    picker.setMarker(marker);
                }
                picker.show(getFragmentManager(), marker + "TimePicker");
            }
        });
    }

    private void configureButtonNow(int buttonId, final Marker marker) {
        Button btnMorningNow = (Button) findViewById(buttonId);
        btnMorningNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                onTimeSet(marker, new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            }
        });
    }
}
