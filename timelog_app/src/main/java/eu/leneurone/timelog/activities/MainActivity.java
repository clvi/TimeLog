package eu.leneurone.timelog.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        refreshDisplay();
    }

    @Override
    public void onTimeSet(@NonNull Marker marker, @NonNull Time time) {
        times.put(marker, time);
        displayTime(marker, time);
        refreshTotal();
    }

    private void refreshDisplay() {
        setTvDisplayDate();

        // mask the "Now" buttons if the selected date is not today
        setButtonsVisibility(calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)
                && calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR));

        clearTimes();
        loadSavedData();
    }

    private void setTvDisplayDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        ((TextView) findViewById(R.id.tvDate)).setText(format.format(calendar.getTime()));
    }

    private void loadSavedData() {
        times = service.loadDayWorklog(calendar.getTime(), getApplicationContext());
        for (Map.Entry<Marker, Time> timeEntry : times.entrySet()) {
            displayTime(timeEntry.getKey(), timeEntry.getValue());
        }
        refreshTotal();
    }

    private void clearTimes() {
        ((TextView) findViewById(R.id.morningTime)).setText("");
        ((TextView) findViewById(R.id.lunchStartTime)).setText("");
        ((TextView) findViewById(R.id.lunchEndTime)).setText("");
        ((TextView) findViewById(R.id.eveningTime)).setText("");
        times.clear();
    }

    private void refreshTotal() {
        // compute and display total work time
        try {
            ((TextView) findViewById(R.id.tvTotal)).setText(TimeUtils.formatTime(TotalCalculator.calculateTotalTime(calendar, times)));
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
                ((TextView) findViewById(R.id.morningTime)).setText(formattedTime);
                break;
            case LUNCH_START:
                ((TextView) findViewById(R.id.lunchStartTime)).setText(formattedTime);
                break;
            case LUNCH_END:
                ((TextView) findViewById(R.id.lunchEndTime)).setText(formattedTime);
                break;
            case EVENING:
                ((TextView) findViewById(R.id.eveningTime)).setText(formattedTime);
                break;
            default:
                break;
        }
    }

    private void configureButtons() {
        findViewById(R.id.btnChangeDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO display a confirmation popup if times have been set and not saved,
                // TODO as the time will be cleared when a new date will be chosen
                DatePickerFragment picker = new DatePickerFragment();
                picker.setInitialDate(calendar.getTime());
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        findViewById(R.id.previousDayLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO display a confirmation popup if times have been set and not saved,
                // TODO as the time will be cleared when a new date will be chosen
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                refreshDisplay();
            }
        });

        findViewById(R.id.nextDayLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO display a confirmation popup if times have been set and not saved,
                // TODO as the time will be cleared when a new date will be chosen
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                refreshDisplay();
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

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TimesValidator.validateMarkersCoherency(times);
                    // clear previous validation error (if any)
                    clearValidationTv();
                    // store the data
                    service.storeDayWorklog(calendar.getTime(), times, getApplicationContext());
                    // show feedback toast
                    Toast.makeText(getApplicationContext(), R.string.saveOK, Toast.LENGTH_SHORT).show();
                } catch (IncoherentMarkersException e) {
                    handleIncoherentMarkersException(e);
                }
            }
        });

        findViewById(R.id.btnRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTotal();
            }
        });
    }

    private void clearValidationTv() {
        ((TextView) findViewById(R.id.validation)).setText("");
        for (Marker marker : Marker.values()) {
            highlightTime(marker, getResources().getColor(android.R.color.primary_text_light));
        }
        findViewById(R.id.btnSave).setEnabled(true);
    }

    private void handleIncoherentMarkersException(IncoherentMarkersException e) {
        ((TextView) findViewById(R.id.validation)).setText(R.string.error_incoherent_markers);
        highlightTime(e.getEarliest(), getResources().getColor(R.color.colorAccent));
        highlightTime(e.getLatest(), getResources().getColor(R.color.colorAccent));

        findViewById(R.id.btnSave).setEnabled(false);
    }

    private void highlightTime(@NonNull Marker marker, int color) {
        switch (marker) {
            case MORNING:
                ((TextView) findViewById(R.id.morningTime)).setTextColor(color);
                break;
            case LUNCH_START:
                ((TextView) findViewById(R.id.lunchStartTime)).setTextColor(color);
                break;
            case LUNCH_END:
                ((TextView) findViewById(R.id.lunchEndTime)).setTextColor(color);
                break;
            case EVENING:
                ((TextView) findViewById(R.id.eveningTime)).setTextColor(color);
                break;
            default:
                break;
        }
    }

    private void configureTimePickerButton(int buttonId, final Marker marker) {
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
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
        // we're displaying data for today
        findViewById(buttonId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                onTimeSet(marker, new Time(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            }
        });
    }

    private void setButtonsVisibility(boolean areVisible) {
        int visibility = areVisible ? View.VISIBLE : View.INVISIBLE;
        findViewById(R.id.btnMorningTimeNow).setVisibility(visibility);
        findViewById(R.id.btnLunchStartTimeNow).setVisibility(visibility);
        findViewById(R.id.btnLunchEndNow).setVisibility(visibility);
        findViewById(R.id.btnEveningTimeNow).setVisibility(visibility);
        findViewById(R.id.btnRefresh).setVisibility(visibility);
    }

}
