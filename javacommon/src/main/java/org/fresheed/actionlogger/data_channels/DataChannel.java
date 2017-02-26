package org.fresheed.actionlogger.data_channels;

import java.io.InputStream;

/**
 * Created by fresheed on 30.01.17.
 */

public interface DataChannel {
    void send(String name, InputStream data);
}