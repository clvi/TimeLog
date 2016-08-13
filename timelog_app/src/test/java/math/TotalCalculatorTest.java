package math;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
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
 * Unit tests for totalCalculator
 * PowerMockrunner and PrepareFortest are necessary to mock static method Calendar.getInstance
 * to simulate hours of the current day
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TotalCalculator.class})
public class TotalCalculatorTest {

    @Test
    public void testFullNormalDataToday() throws IncoherentMarkersException {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 0));
        times.put(Marker.LUNCH_START, new Time(12, 0));
        times.put(Marker.LUNCH_END, new Time(13, 0));
        times.put(Marker.EVENING, new Time(17, 0));
        assertEquals(new Time(8, 0), TotalCalculator.calculateTotalTime(Calendar.getInstance(), times));

        times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 30));
        times.put(Marker.LUNCH_END, new Time(13, 45));
        times.put(Marker.EVENING, new Time(17, 10));
        assertEquals(new Time(7, 40), TotalCalculator.calculateTotalTime(Calendar.getInstance(), times));
    }

    @Test
    public void testFullNormalDataSomeDay() throws IncoherentMarkersException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 5, 24);

        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 0));
        times.put(Marker.LUNCH_START, new Time(12, 0));
        times.put(Marker.LUNCH_END, new Time(13, 0));
        times.put(Marker.EVENING, new Time(17, 0));
        assertEquals(new Time(8, 0), TotalCalculator.calculateTotalTime(calendar, times));

        times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 30));
        times.put(Marker.LUNCH_END, new Time(13, 45));
        times.put(Marker.EVENING, new Time(17, 10));
        assertEquals(new Time(7, 40), TotalCalculator.calculateTotalTime(calendar, times));
    }

    @Test
    public void testNoLunchBreak() throws IncoherentMarkersException {
        Calendar calendar = Calendar.getInstance();

        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 0));
        times.put(Marker.EVENING, new Time(17, 0));
        assertEquals(new Time(8, 0), TotalCalculator.calculateTotalTime(calendar, times));

        calendar.set(2015, 5, 24);
        assertEquals(new Time(8, 0), TotalCalculator.calculateTotalTime(calendar, times));
    }

    @Test
    public void testIncompleteDataToday() throws IncoherentMarkersException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 30);

        PowerMockito.mockStatic(Calendar.class);
        Mockito.when(Calendar.getInstance()).thenReturn(calendar);

        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 0));
        assertEquals(new Time(3, 30), TotalCalculator.calculateTotalTime(calendar, times));

        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);
        Mockito.when(Calendar.getInstance()).thenReturn(calendar);
        assertEquals(new Time(6, 30), TotalCalculator.calculateTotalTime(calendar, times));

        times.put(Marker.LUNCH_START, new Time(12, 30));
        assertEquals(new Time(4, 30), TotalCalculator.calculateTotalTime(calendar, times));

        times.put(Marker.LUNCH_END, new Time(13, 45));
        assertEquals(new Time(6, 15), TotalCalculator.calculateTotalTime(calendar, times));

        // special test case : when current time = lunch_end, total = lunch_start - morning
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 45);
        Mockito.when(Calendar.getInstance()).thenReturn(calendar);
        assertEquals(new Time(4, 30), TotalCalculator.calculateTotalTime(calendar, times));
    }

    @Test
    public void testIncompleteDataSomeDay() throws IncoherentMarkersException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 5, 24);

        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(calendar, times));

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 30));
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(calendar, times));

        times.clear();
        times.put(Marker.LUNCH_END, new Time(13, 45));
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(calendar, times));

        times.clear();
        times.put(Marker.EVENING, new Time(17, 10));
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(calendar, times));

        times.clear();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 30));
        assertEquals(new Time(4, 15), TotalCalculator.calculateTotalTime(calendar, times));

        times.put(Marker.LUNCH_END, new Time(13, 45));
        assertEquals(new Time(4, 15), TotalCalculator.calculateTotalTime(calendar, times));

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 30));
        times.put(Marker.LUNCH_END, new Time(13, 45));
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(calendar, times));

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 30));
        times.put(Marker.LUNCH_END, new Time(13, 45));
        times.put(Marker.EVENING, new Time(17, 10));
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(calendar, times));

    }

    @Test
    public void testNoData() throws IncoherentMarkersException {
        assertEquals(new Time(0, 0), TotalCalculator.calculateTotalTime(Calendar.getInstance(), new HashMap<Marker, Time>()));
    }
}
