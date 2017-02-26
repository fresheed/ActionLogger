package org.fresheed.actionlogger.events;

/**
 * Created by fresheed on 01.02.17.
 */

public class ActionEvent {
    private final long timestamp;
    private final float[] event_values;

    public ActionEvent(long timestamp, float[] values){
        this.timestamp=timestamp;
        this.event_values=values;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public float[] getValues(){
        return event_values;
    }

}
