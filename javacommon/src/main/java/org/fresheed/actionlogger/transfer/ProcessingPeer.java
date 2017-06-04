package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.classification.ActivityKind;
import org.fresheed.actionlogger.classification.LogClassifier;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by fresheed on 30.05.17.
 */

public class ProcessingPeer implements MessageReceiver {
    public static final int SUPPORTED_EVENT_CARDINALITY = 3;
    private final MessageDispatcher dispatcher;
    private final MessageProcessedCallback callback;
    private final LogClassifier classifier;

    public ProcessingPeer(MessageDispatcher dispatcher, MessageProcessedCallback callback, LogClassifier classifier){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.callback=callback;
        this.classifier=classifier;
    }

    @Override
    public void receive(Message msg) {
        if ("ACTION_LOG".equals(msg.name)){
            final ByteArrayInputStream data_stream=new ByteArrayInputStream(msg.payload);
            byte[] buffer=new byte[msg.payload.length];
            try {
                data_stream.read(buffer);
                ActionLog log=new EventsLogCompressor().decompressEventsLog(buffer, SUPPORTED_EVENT_CARDINALITY);
                System.out.println("before cls");
                ActivityKind kind=classifier.classify(log);
                System.out.println("after cls");
                callback.inform("Current activity: "+kind.toString());
                //callback.inform("chunk len:"+log.getEvents().size());
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
