package eu.leneurone.timelog.validators;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

import eu.leneurone.timelog.exceptions.IncoherentMarkersException;
import eu.leneurone.timelog.model.Marker;
import eu.leneurone.timelog.model.Time;

/**
 * Unit test for TimesValidator
 */
public class TimesValidatorTest {

    private class IncoherentMarkersMatcher extends BaseMatcher<IncoherentMarkersException> {

        private Marker earliest;
        private Marker latest;

        IncoherentMarkersMatcher(Marker earliest, Marker latest) {
            this.earliest = earliest;
            this.latest = latest;
        }

        @Override
        public boolean matches(Object item) {
            return item instanceof IncoherentMarkersException
                    && ((IncoherentMarkersException) item).getEarliest() == this.earliest
                    && ((IncoherentMarkersException) item).getLatest() == this.latest;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText((new IncoherentMarkersException(earliest, latest)).toString());
        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test2MarkersAndKO_1() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.LUNCH_START));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(6, 3));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test2MarkersAndKO_2() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_END, new Time(6, 30));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test2MarkersAndKO_3() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.EVENING));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.EVENING, new Time(7, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test2MarkersAndKO_4() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_START, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(11, 30));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test2MarkersAndKO_5() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_START, Marker.EVENING));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.EVENING, new Time(11, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test2MarkersAndKO_6() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_END, Marker.EVENING));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.LUNCH_END, new Time(13, 50));
        times.put(Marker.EVENING, new Time(13, 30));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_1() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.LUNCH_START));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(6, 3));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_2() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_START, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(10, 30));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_3() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.LUNCH_START));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(6, 3));
        times.put(Marker.LUNCH_END, new Time(4, 30));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_4() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_END, new Time(6, 30));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_5() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_END, Marker.EVENING));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        times.put(Marker.EVENING, new Time(12, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_6() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.MORNING, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_END, new Time(6, 30));
        times.put(Marker.EVENING, new Time(4, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_7() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_START, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(11, 30));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_8() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_END, Marker.EVENING));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        times.put(Marker.EVENING, new Time(12, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndKO_9() throws Exception {
        thrown.expect(new IncoherentMarkersMatcher(Marker.LUNCH_START, Marker.LUNCH_END));
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(11, 30));
        times.put(Marker.EVENING, new Time(10, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void testCompleteAndOK() throws Exception {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void testEmptyAndOK() throws Exception {
        TimesValidator.validateMarkersCoherency(new HashMap<Marker, Time>());
    }

    @Test
    public void test1Marker() throws Exception {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.LUNCH_END, new Time(13, 30));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test2MarkersAndOK() throws Exception {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 3));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.LUNCH_END, new Time(13, 30));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);
    }

    @Test
    public void test3MarkersAndOK() throws Exception {
        Map<Marker, Time> times = new HashMap<>();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.MORNING, new Time(8, 15));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);

        times.clear();
        times.put(Marker.LUNCH_START, new Time(12, 3));
        times.put(Marker.LUNCH_END, new Time(13, 30));
        times.put(Marker.EVENING, new Time(17, 50));
        TimesValidator.validateMarkersCoherency(times);
    }
}