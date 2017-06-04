package org.fresheed.actionlogger.classification;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.utils.LogPreprocessor;

import java.util.ArrayList;
import java.util.List;

import jsat.classifiers.DataPoint;
import jsat.classifiers.neuralnetwork.BackPropagationNet;
import jsat.linear.ConcatenatedVec;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

/**
 * Created by fresheed on 04.06.17.
 */

public class RawMLPClassifier implements LogClassifier {
    private final BackPropagationNet mlp;
    private final static int NUM_EVENTS=10, RESAMPING_PERIOD_MS=100;

    public RawMLPClassifier(BackPropagationNet trained_mlp){
        mlp=trained_mlp;
    }

    @Override
    public ActivityKind classify(ActionLog log) {
        DataPoint point=extractFeatures(log);
        int classified=mlp.classify(point).mostLikely();
        ActivityKind kind=ActivityKind.getKindById(classified);
        return kind;
        //return ActivityKind.PUSHUPS;
    }

    private DataPoint extractFeatures(ActionLog log) {
        //System.out.println("WARNING: ORIGINALLY JOIN WAS PERFORMED COLUMN-WISE; NOW IT IS DONE ROW-WISE");
        try {
            ActionLog even_sampled=new LogPreprocessor().resample(log, RESAMPING_PERIOD_MS);
            List<ActionEvent> used_events=even_sampled.getEvents().subList(0, NUM_EVENTS);
            List<Vec> events_values = new ArrayList<>();
            for (ActionEvent event : used_events) {
                events_values.add(new DenseVector(convertValuesToDoubles(event.getValues())));
            }
            ConcatenatedVec joined = new ConcatenatedVec(events_values);
            DataPoint features = new DataPoint(joined);
            return features;
        } catch (LogPreprocessor.ProcessingException e) {
            throw new RuntimeException("Error handling on preprocessing is not implemented");
        }
    }

    private double[] convertValuesToDoubles(float[] floats) {
        final double[] doubles = new double[floats.length];
        for (int i = 0; i < floats.length; i++) {
            doubles[i] = floats[i];
        }
        return doubles;
    }

}
