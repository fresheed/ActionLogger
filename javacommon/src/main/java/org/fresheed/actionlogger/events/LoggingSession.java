package org.fresheed.actionlogger.events;

import java.util.List;

import javax.swing.Action;

/**
 * Created by fresheed on 25.04.17.
 */

public interface LoggingSession {
    List<ActionEvent> stopAndRetrieve();
}
