package eu.leneurone.timelog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import eu.leneurone.timelog.exceptions.IncoherentMarkersException;
import eu.leneurone.timelog.fragments.DatePickerFragment;
import eu.leneurone.timelog.fragments.TimePickerFragment;
import eu.leneurone.timelog.math.TotalCalculator;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;
import eu.leneurone.timelog.services.StorageService;
import eu.leneurone.timelog.services.impl.StorageServiceImpl;
import eu.leneurone.timelog.utils.TimeUtils;
import eu.leneurone.timelog.validators.TimesValidator;

/**
 * Main screen of the app
 */
public class MainActivity extends AppCompatActivity implements DatePickerFragment.FragmentResultInterested, TimePickerFragment.FragmentResultInterested {

    /**
     * Stores the chosen date
     */
    private Calendar calendar;
    /**
     * stores the chosen times for the 4 markers
     */
    Map<Marker, Time> times = new HashMap<>(4);

    /**
     * storage service
     */
    StorageService service = new StorageServiceImpl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        calendar = Calendar.getInstance();

        // init the displayed date to the current day
        setTvDisplayDate();

        loadSavedData();

        configureButtons();
    }

    @Override
    public void onDateSet(Date date) {
        calendar.setTime(date);
        setTvDisplayDate();

        // mask the "Now" buttons if the selected date is not today
        setButtonsNowVisibility(calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        clearTimes();
        loadSavedData();
    }

    @Override
    public void onTimeSet(@NonNull Marker marker, @NonNull Time time) {
        times.put(marker, time);
        displayTime(marker, time);
        refreshTotal();
    }

    private void setTvDisplayDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        TextView tvDisplayDate = (TextView) findViewById(R.id.tvDate);
        tvDisplayDate.setText(format.format(calendar.getTime()));
    }

    private void loadSavedData() {
        times = service.loadDayWorklog(calendar.getTime(), getApplicationContext());
        for (Map.Entry<Marker, Time> timeEntry : times.entrySet()) {
            displayTime(timeEntry.getKey(), timeEntry.getValue());
        }
        refreshTotal();
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

    private void refreshTotal() {
        // compute and display total work time
        TextView tvTotal = (TextView) findViewById(R.id.tvTotal);
        try {
            tvTotal.setText(TimeUtils.formatTime(TotalCalculator.calculateTotalTime(calendar, times)));
            // clear previous validation errors
            clearValidationTv();
        } catch (IncoherentMarkersException e) {
            handleIncoherentMarkersException(e);
        }
    }

    private void displayTime(Marker marker, Time time) {
        String formattedTime = TimeUtils.formatTime(time);
        switch (marker) {
            case MORNING:
                TextView tvMorningTime = (TextView) findViewById(R.id.morningTime);
                tvMorningTime.setText(formattedTime);
                break;
            case LUNCH_START:
                TextView tvLunchStart = (TextView) findViewById(R.id.lunchStartTime);
                tvLunchStart.setText(formattedTime);
                break;
            case LUNCH_END:
                TextView tvLunchEnd = (TextView) findViewById(R.id.lunchEndTime);
                tvLunchEnd.setText(formattedTime);
                break;
            case EVENING:
                TextView tvEvening = (TextView) findViewById(R.id.eveningTime);
                tvEvening.setText(formattedTime);
                break;
            default:
                break;
        }
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
                try {
                    TimesValidator.validateMarkersCoherency(times);
                    // clear previous validation error (if any)
                    clearValidationTv();
                    // store the data
                    service.storeDayWorklog(calendar.getTime(), times, getApplicationContext());
                    // TODO display a small and temporary OK icon
                } catch (IncoherentMarkersException e) {
                    handleIncoherentMarkersException(e);
                }
            }
        });
    }

    private void clearValidationTv() {
        TextView tvValidation = (TextView) findViewById(R.id.validation);
        tvValidation.setText("");
        for(Marker marker : Marker.values()) {
            highlightTime(marker, getResources().getColor(android.R.color.primary_text_light));
        }
        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setEnabled(true);
    }

    private void handleIncoherentMarkersException(IncoherentMarkersException e) {
        TextView tvValidation = (TextView) findViewById(R.id.validation);
        tvValidation.setText(R.string.error_incoherent_markers);
        highlightTime(e.getEarliest(), getResources().getColor(R.color.colorAccent));
        highlightTime(e.getLatest(), getResources().getColor(R.color.colorAccent));

        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setEnabled(false);
    }

    private void highlightTime(@NonNull Marker marker, int color) {
        switch (marker) {
            case MORNING:
                TextView tvMorningTime = (TextView) findViewById(R.id.morningTime);
                tvMorningTime.setTextColor(color);
                break;
            case LUNCH_START:
                TextView tvLunchStart = (TextView) findViewById(R.id.lunchStartTime);
                tvLunchStart.setTextColor(color);
                break;
            case LUNCH_END:
                TextView tvLunchEnd = (TextView) findViewById(R.id.lunchEndTime);
                tvLunchEnd.setTextColor(color);
                break;
            case EVENING:
                TextView tvEvening = (TextView) findViewById(R.id.eveningTime);
                tvEvening.setTextColor(color);
                break;
            default:
                break;
        }
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

        // we're displaying data for today
        btnMorningNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                onTimeSet(marker, new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            }
        });
    }

    private void setButtonsNowVisibility(boolean areVisible) {
        int visibility = areVisible ? View.VISIBLE : View.INVISIBLE;
        findViewById(R.id.btnMorningTimeNow).setVisibility(visibility);
        findViewById(R.id.btnLunchStartTimeNow).setVisibility(visibility);
        findViewById(R.id.btnLunchEndNow).setVisibility(visibility);
        findViewById(R.id.btnEveningTimeNow).setVisibility(visibility);
    }

}
