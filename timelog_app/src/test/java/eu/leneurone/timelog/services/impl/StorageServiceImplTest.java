package eu.leneurone.timelog.services.impl;

import android.content.Context;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

/**
 * Unit tests for StorageServiceImpl complex methods
 */
public class StorageServiceImplTest {

    private StorageServiceImpl service;

    private Context appCtx;

    @Before
    public void prepare() {
        appCtx = Mockito.mock(Context.class);
        service = Mockito.mock(StorageServiceImpl.class);
        // disable the mock for the loadWeekWorklog method, since we want to test it
        Mockito.when(service.loadWeekWorklog(Mockito.any(Calendar.class), Mockito.any(Context.class))).thenCallRealMethod();
    }

    @Test
    public void testLoadWeek_Monday() {
        // the 31/10/2016 was a monday
        GregorianCalendar monday = new GregorianCalendar(2016, Calendar.OCTOBER, 31);

        // do
        Map<Calendar, Map<Marker, Time>> data = service.loadWeekWorklog(monday, appCtx);

        // validate
        Assert.assertNotNull(data);
        Assert.assertTrue(data.isEmpty());
        Mockito.verifyNoMoreInteractions(appCtx);
        Mockito.verify(service).loadWeekWorklog(Mockito.any(Calendar.class), Mockito.any(Context.class));
        Mockito.verifyNoMoreInteractions(service);
    }

    @Test
    public void testLoadWeek_Tuesday() {
        // the 1/11/2016 was a tuesday
        GregorianCalendar tuesday = new GregorianCalendar(2016, Calendar.NOVEMBER, 1);

        // prepare stub
        GregorianCalendar monday = new GregorianCalendar(2016, Calendar.OCTOBER, 31);
        Map<Marker, Time> times = getTimes();
        Mockito.when(service.loadDayWorklog(Mockito.any(Date.class), Mockito.any(Context.class)))
                .thenReturn(times);

        // do
        Map<Calendar, Map<Marker, Time>> data = service.loadWeekWorklog(tuesday, appCtx);

        // validate
        Assert.assertNotNull(data);
        Assert.assertTrue(data.size() == 1);
        Assert.assertTrue(data.containsKey(monday));
        Assert.assertTrue(data.containsValue(times));
    }

    @Test
    public void testLoadWeek_Wednesday() {
        // the 2/11/2016 was a wednesday
        GregorianCalendar wednesday = new GregorianCalendar(2016, Calendar.NOVEMBER, 2);

        // prepare stub
        GregorianCalendar monday = new GregorianCalendar(2016, Calendar.OCTOBER, 31);
        GregorianCalendar tuesday = new GregorianCalendar(2016, Calendar.NOVEMBER, 1);
        Map<Marker, Time> times = getTimes();
        Mockito.when(service.loadDayWorklog(Mockito.any(Date.class), Mockito.any(Context.class)))
                .thenReturn(times);

        // do
        Map<Calendar, Map<Marker, Time>> data = service.loadWeekWorklog(wednesday, appCtx);

        // validate
        Assert.assertNotNull(data);
        Assert.assertTrue(data.size() == 2);
        Assert.assertTrue(data.get(monday).equals(times));
        Assert.assertTrue(data.get(tuesday).equals(times));
    }

    @Test
    public void testLoadWeek_Sunday() {
        // the 6/11/2016 was a sunday
        GregorianCalendar sunday = new GregorianCalendar(2016, Calendar.NOVEMBER, 6);

        // prepare stub
        GregorianCalendar monday = new GregorianCalendar(2016, Calendar.OCTOBER, 31);
        GregorianCalendar tuesday = new GregorianCalendar(2016, Calendar.NOVEMBER, 1);
        GregorianCalendar wednesday = new GregorianCalendar(2016, Calendar.NOVEMBER, 2);
        GregorianCalendar thursday = new GregorianCalendar(2016, Calendar.NOVEMBER, 3);
        GregorianCalendar friday = new GregorianCalendar(2016, Calendar.NOVEMBER, 4);
        GregorianCalendar saturday = new GregorianCalendar(2016, Calendar.NOVEMBER, 5);
        Map<Marker, Time> times = getTimes();
        Map<Marker, Time> times2 = getTimes();
        // total time for times2 : 6:40
        times2.put(Marker.MORNING, new Time(9, 15));
        Mockito.when(service.loadDayWorklog(Mockito.eq(monday.getTime()), Mockito.any(Context.class))).thenReturn(times);
        Mockito.when(service.loadDayWorklog(Mockito.eq(tuesday.getTime()), Mockito.any(Context.class))).thenReturn(times2);
        Mockito.when(service.loadDayWorklog(Mockito.eq(wednesday.getTime()), Mockito.any(Context.class))).thenReturn(times);
        Mockito.when(service.loadDayWorklog(Mockito.eq(thursday.getTime()), Mockito.any(Context.class))).thenReturn(times2);
        Mockito.when(service.loadDayWorklog(Mockito.eq(friday.getTime()), Mockito.any(Context.class))).thenReturn(times);
        Mockito.when(service.loadDayWorklog(Mockito.eq(saturday.getTime()), Mockito.any(Context.class))).thenReturn(times2);

        // do
        Map<Calendar, Map<Marker, Time>> data = service.loadWeekWorklog(sunday, appCtx);

        // validate
        Assert.assertNotNull(data);
        Assert.assertTrue(data.size() == 6);
        Assert.assertTrue(data.get(monday).equals(times));
        Assert.assertTrue(data.get(tuesday).equals(times2));
        Assert.assertTrue(data.get(wednesday).equals(times));
        Assert.assertTrue(data.get(thursday).equals(times2));
        Assert.assertTrue(data.get(friday).equals(times));
        Assert.assertTrue(data.get(saturday).equals(times2));
    }

    private Map<Marker, Time> getTimes() {
        // total time : 7:40
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 30));
        times.put(Marker.LUNCH_END, new Time(13, 45));
        times.put(Marker.EVENING, new Time(17, 10));
        return times;
    }

}
