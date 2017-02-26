package org.fresheed.actionlogger.events;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by fresheed on 01.02.17.
 */

public class TestActionLogger extends TestCase {

    private static class InternalActionLogger extends ActionRecorder {
        @Override
        protected void setupSpecific() {}
        @Override
        protected void stopSpecific() {}
    }

    @Test
    public void testIfNoActionsLogged() throws LoggerConfigException {
        InternalActionLogger logger=new InternalActionLogger();
        logger.startLogging();
        logger.stopLogging();
        assertEquals(0, logger.getLoggedEvents().size());
    }

    @Test
    public void testSomeActionsLogged() throws LoggerConfigException  {
        InternalActionLogger logger=new InternalActionLogger();
        logger.startLogging();
        logger.addEvent(new ActionEvent(0, new float[]{1.0f, 2.0f, 3.0f}));
        logger.addEvent(new ActionEvent(1, new float[]{3.0f, 4.0f, 5.0f}));
        logger.stopLogging();
        List<ActionEvent> events=logger.getLoggedEvents();
        Assert.assertEquals(2, events.size());
        assertEquals(0, events.get(0).getTimestamp());
        assertArrayEquals(new float[]{1.0f, 2.0f, 3.0f}, events.get(0).getValues(), 0);
        assertEquals(1, events.get(1).getTimestamp());
        assertArrayEquals(new float[]{3.0f, 4.0f, 5.0f}, events.get(1).getValues(), 0);
    }

    @Test
    public void testEventsSkippedBeforeStart() throws LoggerConfigException  {
        InternalActionLogger logger=new InternalActionLogger();
        logger.addEvent(new ActionEvent(0, new float[]{1.0f, 2.0f, 3.0f}));
        logger.startLogging();
        logger.stopLogging();
        assertEquals(0, logger.getLoggedEvents().size());
    }

    @Test
    public void testEventsSkippedAfterStop() throws LoggerConfigException  {
        InternalActionLogger logger=new InternalActionLogger();
        logger.startLogging();
        logger.stopLogging();
        logger.addEvent(new ActionEvent(0, new float[]{1.0f, 2.0f, 3.0f}));
        assertEquals(0, logger.getLoggedEvents().size());
    }

    @Test
    public void testEventsErasedOnRestart() throws LoggerConfigException {
        InternalActionLogger logger=new InternalActionLogger();
        logger.startLogging();
        logger.addEvent(new ActionEvent(0, new float[]{1.0f, 2.0f, 3.0f}));
        logger.stopLogging();
        logger.startLogging();
        logger.addEvent(new ActionEvent(0, new float[]{1.0f, 2.0f, 3.0f}));
        logger.stopLogging();
        assertEquals(1, logger.getLoggedEvents().size());
    }

    @Test
    public void testMultipleStartsAreIncorrect() throws LoggerConfigException  {
        InternalActionLogger logger=new InternalActionLogger();
        logger.startLogging();
        try {
            logger.startLogging();
            Assert.fail("LCE not raised");
        } catch (LoggerConfigException lce){}
    }

}
