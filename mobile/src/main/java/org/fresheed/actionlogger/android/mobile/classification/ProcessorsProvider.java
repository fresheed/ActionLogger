package org.fresheed.actionlogger.android.mobile.classification;

import android.content.res.Resources;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.classification.ActivityKind;
import org.fresheed.actionlogger.classification.LogClassifier;
import org.fresheed.actionlogger.classification.RawMLPClassifier;
import org.fresheed.actionlogger.events.ActionLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jsat.classifiers.neuralnetwork.BackPropagationNet;

/**
 * Created by fresheed on 04.06.17.
 */

public class ProcessorsProvider {

    private static final Map<String, ClassifierLoader> classifiers_by_codes=new HashMap<String, ClassifierLoader>(){{
        put("RAW_MLP", new RawMLPLoader());
        put("RANDOM", new RandomClassifierLoader());
    }};

    private final Resources resources;

    public ProcessorsProvider(Resources resources){
        this.resources=resources;
    }

    public LogClassifier getSelectedClassifier(String code) throws ClassifierLoadException{
        if (!classifiers_by_codes.containsKey(code)){
            throw new ClassifierLoadException("No classifier matching "+code);
        }
        ClassifierLoader loader=classifiers_by_codes.get(code);
        LogClassifier classifier=loader.load(resources);
        return classifier;
    }

    private interface ClassifierLoader{
        LogClassifier load(Resources resources) throws ClassifierLoadException;
    }

    private static class RawMLPLoader implements ClassifierLoader {
        private static final String asset_code="raw_mlp_97";
        @Override
        public LogClassifier load(Resources resources) throws ClassifierLoadException {
            InputStream file_input=resources.openRawResource(R.raw.raw_mlp_97);
            try {
                ObjectInputStream serialization_input=new ObjectInputStream(file_input);
                BackPropagationNet mlp=(BackPropagationNet)serialization_input.readObject();
                serialization_input.close();
                RawMLPClassifier classifier=new RawMLPClassifier(mlp);
                return classifier;
            } catch (IOException | ClassNotFoundException e) {
                throw new ClassifierLoadException("Cannot load RawMLPClassifier", e);
            }
        }
    }

    private static class RandomClassifierLoader implements ClassifierLoader {

        @Override
        public LogClassifier load(Resources resources) throws ClassifierLoadException {
            LogClassifier randomizer=new LogClassifier() {
                @Override
                public ActivityKind classify(ActionLog log) {
                    int kind_id=new Random().nextInt(ActivityKind.values().length);
                    return ActivityKind.values()[kind_id];
                }
            };
            return randomizer;
        };
    }

    public static class ClassifierLoadException extends Exception {
        public ClassifierLoadException(String message) {
            super(message);
        }

        public ClassifierLoadException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
}
