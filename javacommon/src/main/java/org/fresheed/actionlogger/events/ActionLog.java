package org.fresheed.actionlogger.events;

import java.util.ArrayList;
import java.util.List;

import sun.rmi.runtime.Log;

/**
 * Created by fresheed on 25.04.17.
 */

public class ActionLog {
    private static final int MAX_LOG_LENGTH=100000/(8+4*3); // max message size by log entry size
    private final int num_axes;
    private final List<ActionEvent> events=new ArrayList<>();

    public ActionLog(int num_axes){
        this.num_axes=num_axes;
    }

    public void addEvent(ActionEvent event) throws LoggingException{
        if (event.getValues().length!=num_axes){
            throw new LoggingException("Attempted to add event with wrong axes amount");
        }
        if (events.size()>MAX_LOG_LENGTH){
            throw new LoggingException("Too much events were logged already");
        }
        events.add(event);
    }

    public List<ActionEvent> getEvents(){
        return events;
    }

    public int getNumAxes(){
        return num_axes;
    }
}
