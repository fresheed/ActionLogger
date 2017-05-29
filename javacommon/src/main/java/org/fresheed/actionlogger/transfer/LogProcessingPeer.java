package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fresheed on 29.05.17.
 */

public class LogProcessingPeer implements MessageReceiver  {
    private final MessageDispatcher dispatcher;
    private final MessageProcessedCallback callback;

    private final int SUPPORTED_EVENT_CARDINALITY=3;

    public LogProcessingPeer(MessageDispatcher dispatcher, MessageProcessedCallback callback){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.callback=callback;
    }

    @Override
    public void receive(Message msg) {
        if ("ACTION_LOG".equals(msg.name)){
            byte[] buffer=new byte[msg.payload.length];
            final ByteArrayInputStream data_stream=new ByteArrayInputStream(msg.payload);
            try {
                data_stream.read(buffer);
                ActionLog log=new EventsLogCompressor().decompressEventsLog(buffer, SUPPORTED_EVENT_CARDINALITY);
                callback.inform("Received log of length "+log.getEvents().size());
            } catch (IOException e) {
                callback.failure("IOError occured on log processing: "+e.getStackTrace());
            } catch (EventsLogCompressor.LogEncodingException e) {
                callback.failure("Error occured on log decoding: "+e.getStackTrace());
            }
        } else if ("ERROR".equals(msg.name)){
            callback.failure("received ERROR message");
        }
    }
}
