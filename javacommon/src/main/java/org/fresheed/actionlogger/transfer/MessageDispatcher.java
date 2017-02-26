package org.fresheed.actionlogger.transfer;

/**
 * Created by fresheed on 05.02.17.
 */

public interface MessageDispatcher {
    void sendAll(Message msg);
    void addReceiver(MessageReceiver receiver);
    void removeReceiver(MessageReceiver receiver);
}