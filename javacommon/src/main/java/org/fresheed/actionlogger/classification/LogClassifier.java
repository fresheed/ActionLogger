package org.fresheed.actionlogger.classification;

import org.fresheed.actionlogger.events.ActionLog;

/**
 * Created by fresheed on 03.06.17.
 */

public interface LogClassifier {
    ActivityKind classify(ActionLog log);
}
