package org.fresheed.actionlogger.events;

/**
 * Created by fresheed on 26.02.17.
 */

public interface ActionSource {
    void setRecorder(ActionRecorder recorder);
    void activate();
    void deactivate();
}
