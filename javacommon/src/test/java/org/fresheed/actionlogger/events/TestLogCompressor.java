package org.fresheed.actionlogger.events;

import junit.framework.TestCase;

import org.fresheed.actionlogger.utils.EventsLogCompressor;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fresheed on 21.02.17.
 */

public class TestLogCompressor{

    @Test
    public void shouldReturnEmptyListOnEmptyInput(){
        List<ActionEvent> empty_log=new ArrayList<>();
        byte[] compressed=callCompress(empty_log, 3);
        assertArrayEquals(new byte[0], compressed);
        assertEquals(new ArrayList<ActionEvent>(), callDecompress(compressed, 3));
    }

    @Test
    public void shouldSupportSingleAction(){
        List<ActionEvent> log=new ArrayList<>();
        log.add(new ActionEvent(1010, new float[]{1,2,3}));
        //assertArrayEquals(new byte[]{}, new EventsLogCompressor().compressEventsLog(empty_log));
        byte[] compressed=callCompress(log, 3);
        assertEquals(8+4*3, compressed.length);
        assertSameLogs(log, callDecompress(compressed, 3));
    }

    @Test
    public void shouldSupportMultipleActions(){
        List<ActionEvent> log=new ArrayList<>();
        ActionEvent event=new ActionEvent(1010, new float[]{1,2,3});
        log.add(event);
        log.add(event);
        log.add(event);
        byte[] compressed=callCompress(log, 3);
        assertEquals((8+4*3)*3, compressed.length);
        assertSameLogs(log, callDecompress(compressed, 3));
    }

    @Test
    public void shouldRejectEncodingZeroLengthActions(){
        List<ActionEvent> log=new ArrayList<>();
        try{
            new EventsLogCompressor().compressEventsLog(log, 0);
            fail("Compressor not failed on zero-length event's values");
        } catch (EventsLogCompressor.LogEncodingException e){}
    }

    @Test
    public void shouldRejectParsingLengthActions(){
        try{
            new EventsLogCompressor().decompressEventsLog(new byte[]{1,2,3}, 0);
            fail("Compressor not failed on zero-length event's values");
        } catch (EventsLogCompressor.LogEncodingException e){}
    }

    @Test
    public void shouldRejectEncodingDifferentLengthActions(){
        List<ActionEvent> log=new ArrayList<>();
        log.add(new ActionEvent(1010, new float[]{1,2,3}));
        log.add(new ActionEvent(2020, new float[]{1,2}));

        try{
            new EventsLogCompressor().compressEventsLog(log, 3);
            fail("Compressor not failed on different lengths of events' values");
        } catch (EventsLogCompressor.LogEncodingException e){}
    }

    @Test
    public void shouldRejectParseDifferentLengthActions(){
        List<ActionEvent> log_2=new ArrayList<>();
        log_2.add(new ActionEvent(1010, new float[]{1,2}));
        byte[] compressed_2=callCompress(log_2, 2);

        List<ActionEvent> log_3=new ArrayList<>();
        log_3.add(new ActionEvent(1010, new float[]{1,2,3}));
        byte[] compressed_3=callCompress(log_3, 3);

        byte[] combined=new byte[compressed_2.length+compressed_3.length];
        System.arraycopy(compressed_2, 0, combined, 0, compressed_2.length);
        System.arraycopy(compressed_3, 0, combined, compressed_2.length, compressed_3.length);

        try{
            new EventsLogCompressor().decompressEventsLog(combined, 2);
            fail("Compressor not failed on different lengths of events' values");
        } catch (EventsLogCompressor.LogEncodingException e){}
    }

    private void assertSameEvents(ActionEvent one, ActionEvent two){
        assertEquals(one.getTimestamp(), two.getTimestamp());
        assertArrayEquals(one.getValues(), two.getValues(), 0);
    }

    private void assertSameLogs(List<ActionEvent> one, List<ActionEvent> two){
        assertEquals(one.size(), two.size());
        for (int i=0; i<one.size(); i++){
            assertSameEvents(one.get(i), two.get(i));
        }
    }

    private byte[] callCompress(List<ActionEvent> events, int values_amount){
        try {
            return new EventsLogCompressor().compressEventsLog(events, values_amount);
        } catch (EventsLogCompressor.LogEncodingException e){
            fail("Exception was thrown on compressing: "+e);
            return null;
        }
    }

    private List<ActionEvent> callDecompress(byte[] compressed, int values_amount){
        try {
            return new EventsLogCompressor().decompressEventsLog(compressed, values_amount);
        } catch (EventsLogCompressor.LogEncodingException e){
            fail("Exception was thrown on decompressing: "+e);
            return null;
        }
    }

}
