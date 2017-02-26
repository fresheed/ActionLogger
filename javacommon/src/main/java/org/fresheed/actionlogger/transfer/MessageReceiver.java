package org.fresheed.actionlogger.transfer;

/**
 * Created by fresheed on 05.02.17.
 */

public interface MessageReceiver  {
    void receive(Message msg);
}
