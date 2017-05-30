package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.data_channels.DataChannel;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by fresheed on 30.05.17.
 */

public class ProcessingPeer implements MessageReceiver {
    public static final int SUPPORTED_EVENT_CARDINALITY = 3;
    private final MessageDispatcher dispatcher;
    private final MessageProcessedCallback callback;

    public ProcessingPeer(MessageDispatcher dispatcher, MessageProcessedCallback callback){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.callback=callback;
    }

    @Override
    public void receive(Message msg) {
        if ("ACTION_LOG".equals(msg.name)){
            final ByteArrayInputStream data_stream=new ByteArrayInputStream(msg.payload);
            byte[] buffer=new byte[msg.payload.length];
            try {
                data_stream.read(buffer);
                ActionLog log=new EventsLogCompressor().decompressEventsLog(buffer, SUPPORTED_EVENT_CARDINALITY);
                callback.inform("action log received with length"+log.getEvents().size());
            } catch (IOException e) {
                callback.failure("IOException on log reading:"+e.getStackTrace());
            } catch (EventsLogCompressor.LogEncodingException e) {
                callback.failure("Error on log decoding:"+e.getStackTrace());
            }
        } else if ("ERROR".equals(msg.name)){
            callback.inform("error received "+new Random().nextInt());
        }
    }


}
