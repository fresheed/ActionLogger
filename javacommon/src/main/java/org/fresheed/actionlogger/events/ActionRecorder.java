package org.fresheed.actionlogger.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fresheed on 30.01.17.
 */

public final class ActionRecorder {
    private enum LoggerState {
        WAITING, RUNNING
    }
    private LoggerState current_state=LoggerState.WAITING;

    private final List<ActionEvent> events=new ArrayList<>();

    public void startLogging() throws LoggerStateException {
        if (current_state==LoggerState.RUNNING) {
            throw new LoggerStateException("Logging already started");
        }
        events.clear();
        current_state=LoggerState.RUNNING;
    }

    public void addEvent(ActionEvent event){
        if (current_state!=LoggerState.RUNNING){
            return;
        }
        events.add(event);
    }

    public void stopLogging(){
        current_state=LoggerState.WAITING;
    }

    public List<ActionEvent> getLoggedEvents(){
        return events;
    }
}


