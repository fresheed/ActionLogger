package org.fresheed.actionlogger.classification;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionLog;

/**
 * Created by fresheed on 03.06.17.
 */

public interface LogClassifier {
    Activity classify(ActionLog log);
}
