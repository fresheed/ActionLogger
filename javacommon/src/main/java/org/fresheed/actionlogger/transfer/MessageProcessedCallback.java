package org.fresheed.actionlogger.transfer;

/**
 * Created by fresheed on 25.04.17.
 */
public interface MessageProcessedCallback {
    void inform(String info);
    void failure(String info);
}
