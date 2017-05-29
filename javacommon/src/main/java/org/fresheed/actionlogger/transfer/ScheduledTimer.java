package org.fresheed.actionlogger.transfer;

/**
 * Created by fresheed on 29.05.17.
 */

public interface ScheduledTimer {
    void start();
    void stop();
    void addTask(Runnable task);
}
