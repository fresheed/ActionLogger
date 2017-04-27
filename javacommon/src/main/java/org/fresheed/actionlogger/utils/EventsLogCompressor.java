package org.fresheed.actionlogger.utils;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.LoggingException;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fresheed on 21.02.17.
 */

public class EventsLogCompressor {

    private static final int BYTES_FOR_SINGLE_VALUE=Float.SIZE/8;
    private static final int BYTES_FOR_TIMESTAMP=Long.SIZE/8;

    public byte[] compressEventsLog(ActionLog log){
        List<ActionEvent> events=log.getEvents();
        int values_amount=log.getNumAxes();
        int entry_size=getEntrySize(values_amount);
        int buffer_size=entry_size*events.size();
        byte[] container=new byte[buffer_size];
        ByteBuffer buffer=ByteBuffer.wrap(container);
        for (ActionEvent event: events) {
            buffer.putLong(event.getTimestamp());
            for (float value: event.getValues()) {
                buffer.putFloat(value);
            }

        }
        return container;
    }

    public ActionLog decompressEventsLog(byte[] compressed, int values_amount) throws LogEncodingException{
        if (values_amount<=0){
            throw new LogEncodingException("Values amount should be positive");
        }
        int entry_size=getEntrySize(values_amount);
        if (compressed.length % entry_size != 0){
            throw new LogEncodingException("Computed entry length does not match actual value");
        }
        int entries_amount=compressed.length/entry_size;
        ByteBuffer buffer=ByteBuffer.wrap(compressed);
        List<ActionEvent> events=new ArrayList<>();

        for (int num=0; num<entries_amount; num++) {
            long timestamp = buffer.getLong();
            float[] values=new float[values_amount];
            for (int i = 0; i < values_amount; i++) {
                values[i] = buffer.getFloat();
            }
            events.add(new ActionEvent(timestamp, values));
        }
        ActionLog log=new ActionLog(values_amount);
        try {
            for (ActionEvent event: events){
                log.addEvent(event);
            }
            return log;
        } catch (LoggingException e) {
            throw new RuntimeException("It should not happen");
        }
    }

    public static int getEntrySize(int values_amount){
        return BYTES_FOR_TIMESTAMP+BYTES_FOR_SINGLE_VALUE*values_amount;
    }

    public static class LogEncodingException extends Exception {
        public LogEncodingException(String message) {
            super(message);
        }

        public LogEncodingException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
