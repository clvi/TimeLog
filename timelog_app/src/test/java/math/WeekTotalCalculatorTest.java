package math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import eu.leneurone.timelog.exceptions.IncoherentMarkersException;
import eu.leneurone.timelog.math.TotalCalculator;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

import static junit.framework.Assert.assertEquals;

/**
 * Unit tests for totalCalculator week total time function
 * PowerMockRunner and PrepareForTest are necessary to mock static method Calendar.getInstance
 * to simulate hours of the current day
 * In this test, we assume the {@link TotalCalculatorTest} tests are all OK, since the result of
 * the total week time is almost only an addition of the total times of the days of the week.
 * We also assume the {@link eu.leneurone.timelog.validators.TimesValidatorTest} tests are all OK.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TotalCalculator.class})
public class WeekTotalCalculatorTest {

    @Test
    public void testSum() {
        assertEquals(new Time(0, 0), TotalCalculator.sum(new Time(0, 0), new Time(0, 0)));
        assertEquals(new Time(28, 3), TotalCalculator.sum(new Time(15, 28), new Time(12, 35)));
        assertEquals(new Time(29, 35), TotalCalculator.sum(new Time(28, 0), new Time(1, 35)));
    }

    @Test
    public void testNoData() throws IncoherentMarkersException {
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(new HashMap<Calendar, Map<Marker, Time>>()));
    }

    @Test
    public void testOneDay() throws IncoherentMarkersException {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 0));
        times.put(Marker.LUNCH_START, new Time(12, 0));
        times.put(Marker.LUNCH_END, new Time(13, 0));
        times.put(Marker.EVENING, new Time(17, 0));

        Map<Calendar, Map<Marker, Time>> data = new HashMap<>();
        data.put(Calendar.getInstance(), times);
        assertEquals(new Time(8, 0), TotalCalculator.calculateTotalTime(data));
    }

    @Test
    public void testSeveralDays() throws IncoherentMarkersException {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 0));
        times.put(Marker.LUNCH_START, new Time(12, 0));
        times.put(Marker.LUNCH_END, new Time(13, 0));
        times.put(Marker.EVENING, new Time(17, 45));

        Map<Calendar, Map<Marker, Time>> data = new HashMap<>();
        data.put(Calendar.getInstance(), times);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 5, 24);
        Map<Marker, Time> times2 = new HashMap<>();
        times2.put(Marker.MORNING, new Time(8, 0));
        times2.put(Marker.LUNCH_START, new Time(12, 0));
        times2.put(Marker.LUNCH_END, new Time(13, 0));
        times2.put(Marker.EVENING, new Time(17, 30));

        data.put(calendar, times2);

        assertEquals(new Time(17, 15), TotalCalculator.calculateTotalTime(data));
    }
}
