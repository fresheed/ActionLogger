package org.fresheed.actionlogger.events;

import org.fresheed.actionlogger.utils.LogPreprocessor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
/**
 * Created by fresheed on 01.02.17.
 */

public class TestLogPreprocessor {
    private static final int NUM_AXES=3, RESAMPLING_PERIOD_MS=100, MS_PER_NS=1_000_000;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldRejectEmptyLog() throws LogPreprocessor.ProcessingException {
        LogPreprocessor preprocessor=new LogPreprocessor();
        ActionLog empty=new ActionLog(NUM_AXES);
        exception.expect(LogPreprocessor.ProcessingException.class);
        preprocessor.resample(empty, RESAMPLING_PERIOD_MS);
    }

    @Test
    public void shouldKeepSingleValue() throws LogPreprocessor.ProcessingException {
        LogPreprocessor preprocessor=new LogPreprocessor();
        ActionLog log=new ActionLog(NUM_AXES);
        ActionEvent test_event=new ActionEvent(1*MS_PER_NS, new float[]{1, 2, 3});
        try {
            log.addEvent(test_event);
        } catch (LoggingException ignored) {}
        ActionLog processed=preprocessor.resample(log, RESAMPLING_PERIOD_MS);
        assertThat(processed.getEvents().size(), is(1));
        assertEventsEqual(test_event, processed.getEvents().get(0));
    }

    @Test
    public void shouldKeepSeparatedValues() throws LogPreprocessor.ProcessingException {
        LogPreprocessor preprocessor=new LogPreprocessor();
        ActionLog log=new ActionLog(NUM_AXES);
        ActionEvent first=new ActionEvent(0*MS_PER_NS, new float[]{1, 2, 3});
        ActionEvent second=new ActionEvent(150*MS_PER_NS, new float[]{4, 5, 6});
        try {
            log.addEvent(first);
            log.addEvent(second);
        } catch (LoggingException ignored) {}
        ActionLog processed=preprocessor.resample(log, RESAMPLING_PERIOD_MS);
        assertThat(processed.getEvents().size(), is(2));
        assertEventsEqual(first, processed.getEvents().get(0));
        assertEventsEqual(new ActionEvent(100*MS_PER_NS, new float[]{4, 5, 6}), processed.getEvents().get(1));
    }

    @Test
    public void shouldAverageNeighborValues() throws LogPreprocessor.ProcessingException {
        LogPreprocessor preprocessor=new LogPreprocessor();
        ActionLog log=new ActionLog(NUM_AXES);
        ActionEvent first=new ActionEvent(0*MS_PER_NS, new float[]{1, 2, 3});
        ActionEvent second=new ActionEvent(50*MS_PER_NS, new float[]{4, 5, 6});
        try {
            log.addEvent(first);
            log.addEvent(second);
        } catch (LoggingException ignored) {}
        ActionLog processed=preprocessor.resample(log, RESAMPLING_PERIOD_MS);
        assertThat(processed.getEvents().size(), is(1));
        ActionEvent averaged=new ActionEvent(0*MS_PER_NS, new float[]{2.5f, 3.5f, 4.5f});
        assertEventsEqual(averaged, processed.getEvents().get(0));
    }

    @Test
    public void shouldAverageNeighborPairs() throws LogPreprocessor.ProcessingException {
        LogPreprocessor preprocessor=new LogPreprocessor();
        ActionLog log=new ActionLog(NUM_AXES);
        try {
            log.addEvent(new ActionEvent(0*MS_PER_NS, new float[]{1, 2, 3}));
            log.addEvent(new ActionEvent(50*MS_PER_NS, new float[]{4, 5, 6}));
            log.addEvent(new ActionEvent(100*MS_PER_NS, new float[]{7, 8, 9}));
            log.addEvent(new ActionEvent(150*MS_PER_NS, new float[]{10, 11, 12}));
        } catch (LoggingException ignored) {}
        ActionLog processed=preprocessor.resample(log, RESAMPLING_PERIOD_MS);
        assertThat(processed.getEvents().size(), is(2));
        ActionEvent averaged_1=new ActionEvent(0*MS_PER_NS, new float[]{2.5f, 3.5f, 4.5f});
        assertEventsEqual(averaged_1, processed.getEvents().get(0));
        ActionEvent averaged_2=new ActionEvent(100*MS_PER_NS, new float[]{8.5f, 9.5f, 10.5f});
        assertEventsEqual(averaged_2, processed.getEvents().get(1));
    }

    @Test
    public void shouldRejectMissingValues() throws LogPreprocessor.ProcessingException {
        // actually we can imply that missing values mean no changes
        // but for further processing it's simpler just to fail
        LogPreprocessor preprocessor=new LogPreprocessor();
        ActionLog log=new ActionLog(NUM_AXES);
        try {
            log.addEvent(new ActionEvent(0*MS_PER_NS, new float[]{1, 2, 3}));
            log.addEvent(new ActionEvent(200*MS_PER_NS, new float[]{4, 5, 6}));
        } catch (LoggingException ignored) {}
        exception.expect(LogPreprocessor.ProcessingException.class);
        preprocessor.resample(log, RESAMPLING_PERIOD_MS);
    }

    private void assertEventsEqual(ActionEvent expected, ActionEvent actual) {
        assertThat(actual.getTimestamp(), is(expected.getTimestamp()));
        assertThat(actual.getValues(), is(expected.getValues()));
    }

}
