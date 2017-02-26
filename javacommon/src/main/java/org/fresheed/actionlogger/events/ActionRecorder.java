package org.fresheed.actionlogger.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fresheed on 30.01.17.
 */

public abstract class ActionRecorder {
    private enum LoggerState {
        WAITING, RUNNING
    }
    private LoggerState current_state=LoggerState.WAITING;

    private List<ActionEvent> events=new ArrayList<>();

    public void startLogging() throws LoggerConfigException{
        if (current_state==LoggerState.RUNNING) {
            throw new LoggerConfigException("Logging already started");
        }
        events.clear();
        current_state=LoggerState.RUNNING;
        setupSpecific();
    }

    /*
    Subclass should call this method when it have fetched new event
     */
    protected void addEvent(ActionEvent event){
        if (current_state!=LoggerState.RUNNING){
            return; // not throwing because it is hard to use it in subclass
        }
        events.add(event);
    }

    public void stopLogging(){
        current_state=LoggerState.WAITING;
        stopSpecific();
    }

    public List<ActionEvent> getLoggedEvents(){
        return events;
    }

    protected abstract void setupSpecific();
    protected abstract void stopSpecific();
}


