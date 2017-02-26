package org.fresheed.actionlogger.utils;

import org.fresheed.actionlogger.events.ActionEvent;

import java.util.List;

/**
 * Created by fresheed on 01.02.17.
 */

public class EventsWriter {
    public String writeEventsLog(List<ActionEvent> events){
        StringBuffer buffer=new StringBuffer();
        for (ActionEvent event: events){
            buffer.append(String.format("%d", event.getTimestamp()));
            for (float value: event.getValues()){
                buffer.append(String.format(" %f ", value));
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
