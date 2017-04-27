package org.fresheed.actionlogger.events;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Created by fresheed on 01.02.17.
 */

public class TestActionLogger{

    private static final long DEFAULT_TIMESTAMP=101010;
    public static final float[] DEFAULT_VALUES=new float[]{1.0f, 2.0f, 3.0f};
    private static final ActionEvent default_event=new ActionEvent(DEFAULT_TIMESTAMP, DEFAULT_VALUES);

//    @Test
//    public void testIfNoActionsLogged() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.startLogging();
//        logger.stopLogging();
//        assertEquals(0, logger.getLoggedEvents().size());
//    }
//
//    @Test
//    public void testSingleActionLogged() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.startLogging();
//        logger.addEvent(default_event);
//        logger.stopLogging();
//        List<ActionEvent> events=logger.getLoggedEvents();
//        Assert.assertEquals(1, events.size());
//        ActionEvent event=events.get(0);
//        assertEquals(DEFAULT_TIMESTAMP, event.getTimestamp());
//        assertArrayEquals(DEFAULT_VALUES, event.getValues(), 0);
//    }
//
//    @Test
//    public void testLoggedInCorrectOrder() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.startLogging();
//        logger.addEvent(default_event);
//        logger.addEvent(new ActionEvent(2, new float[]{3.0f, 4.0f, 5.0f}));
//        logger.stopLogging();
//        List<ActionEvent> events=logger.getLoggedEvents();
//        Assert.assertEquals(2, events.size());
//        assertEquals(DEFAULT_TIMESTAMP, events.get(0).getTimestamp());
//        assertArrayEquals(DEFAULT_VALUES, events.get(0).getValues(), 0);
//        assertEquals(2, events.get(1).getTimestamp());
//        assertArrayEquals(new float[]{3.0f, 4.0f, 5.0f}, events.get(1).getValues(), 0);
//    }
//
//    @Test
//    public void testEventsSkippedBeforeStart() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.addEvent(default_event);
//        logger.startLogging();
//        logger.stopLogging();
//        assertEquals(0, logger.getLoggedEvents().size());
//    }
//
//    @Test
//    public void testEventsSkippedAfterStop() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.startLogging();
//        logger.stopLogging();
//        logger.addEvent(default_event);
//        assertEquals(0, logger.getLoggedEvents().size());
//    }
//
//    @Test
//    public void testEventsErasedOnRestart() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.startLogging();
//        logger.addEvent(default_event);
//        logger.stopLogging();
//        logger.startLogging();
//        logger.addEvent(default_event);
//        logger.stopLogging();
//        assertEquals(1, logger.getLoggedEvents().size());
//    }
//
//    @Test
//    public void testMultipleStartsAreIncorrect() throws LoggingException {
//        ActionRecorder logger=new ActionRecorder();
//        logger.startLogging();
//        try {
//            logger.startLogging();
//            Assert.fail("LCE not raised");
//        } catch (LoggingException lce){}
//    }
//
////    @Test
////    public void testTooMuchActionsLogged() throws LoggingException {
////        ActionRecorder logger=new ActionRecorder();
////        logger.startLogging();
////        for (int i=0; i<Integer.MAX_VALUE; i++){
////            logger.addEvent(default_event);
////        }
////        List<ActionEvent> events=logger.getLoggedEvents();
////        assertEquals(Integer.MAX_VALUE, events.size());
////        logger.stopLogging();
////    }

}
