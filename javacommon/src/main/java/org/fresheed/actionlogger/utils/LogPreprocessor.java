package org.fresheed.actionlogger.utils;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.LoggingException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

/**
 * Created by fresheed on 03.06.17.
 */

public class LogPreprocessor {

    public ActionLog resample(ActionLog source, int sampling_period_ms) throws ProcessingException{
        if (source.getEvents().size()==0){
            throw new ProcessingException("Source log is empty");
        }
        List<ActionEvent> events=source.getEvents();
        long period_ns=msToNs(sampling_period_ms);
        List<List<ActionEvent>> regrouped_events=regroupEvents(events, period_ns);
        ActionLog resampled=new ActionLog(source.getNumAxes());
        List<ActionEvent> averaged_events=aggregate(regrouped_events, period_ns);
        for (ActionEvent event: averaged_events){
            try {
                resampled.addEvent(event);
            } catch (LoggingException e) {
                throw new RuntimeException("Should not happen");
            }
        }
        return resampled;
    }

    private long msToNs(long milliseconds){
        return milliseconds*1_000_000;
    }

    private List<List<ActionEvent>> regroupEvents(List<ActionEvent> events, long period) throws ProcessingException {
        long start_time=events.get(0).getTimestamp();
        List<List<ActionEvent>> grouped=new ArrayList<>();
        long break_at=start_time+period;
        List<ActionEvent> current_group=new ArrayList<>();
        for(ActionEvent current_event: events){
            long passed_after_break=current_event.getTimestamp()-break_at;
            if (passed_after_break>=0){
                if (passed_after_break>=period){
                    throw new ProcessingException("There are missing time periods");
                }
                break_at+=period;
                grouped.add(current_group);
                current_group=new ArrayList<>();
                current_group.add(current_event);
            } else {
                current_group.add(current_event);
            }
        }
        grouped.add(current_group);
        return grouped;
    }

    private List<ActionEvent> aggregate(List<List<ActionEvent>> grouped, long period){
        List<ActionEvent> aggregated=new ArrayList<>();
        int cur_index=0;
        long start_time=grouped.get(0).get(0).getTimestamp();
        for (List<ActionEvent> event_group: grouped){
            long timestamp=start_time+cur_index*period;
            float[] values=averageValues(event_group);
            aggregated.add(new ActionEvent(timestamp, values));
            cur_index++;
        }
        return aggregated;
    }

    private float[] averageValues(List<ActionEvent> events){
        int cardinality=events.get(0).getValues().length;
        float[] sums=new float[cardinality];
        float[] averages=new float[cardinality];
        for (ActionEvent event: events){
            float[] values=event.getValues();
            for (int i=0; i<averages.length; i++){
                sums[i]+=values[i];
            }
        }
        for (int i=0; i<averages.length; i++){
            averages[i]=sums[i]/events.size();
        }
        return averages;
    }

    public static class ProcessingException extends Exception {
        public ProcessingException(String message) {
            super(message);
        }

        public ProcessingException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
