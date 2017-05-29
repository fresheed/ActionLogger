package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.events.LoggingException;
import org.fresheed.actionlogger.events.LoggingSession;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

import java.util.Random;

import sun.rmi.runtime.Log;

/**
 * Created by fresheed on 25.04.17.
 */
public class ChunkPeer implements MessageReceiver {
    private enum CurrentState {
        WAITING, LOGGING
    }
    private CurrentState state= CurrentState.WAITING;
    private LoggingSession current_session;

    private final MessageDispatcher dispatcher;
    private final MessageProcessedCallback callback;
    private final ScheduledTimer timer;
    private final ActionsSource source;

    public ChunkPeer(MessageDispatcher dispatcher, ActionsSource source, MessageProcessedCallback callback, ScheduledTimer timer){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.source=source;
        this.callback=callback;
        this.timer=timer;
        this.timer.addTask(restart_logging_task);
    }

    private final Runnable restart_logging_task=new Runnable() {
        @Override
        public void run() {
            //callback.inform("Timer task occured: "+new Random().nextInt());
            if (current_session!=null){
                //ActionLog log=current_session.stopAndRetrieve();
                //callback.inform("Total logged: "+log.getEvents().size());
                ActionLog log=current_session.stopAndRetrieve();
                if (log.getEvents().size()!=0){
                    byte[] compressed_log=new EventsLogCompressor().compressEventsLog(log);
                    dispatcher.sendAll(new Message("ACTION_LOG", compressed_log));
                    callback.inform("STOP processed");
                } else {
                    callback.failure("Zero-length log received - falling back to initial condition");
                }
            }
            current_session=source.startLoggingSession();
        }
    };

    @Override
    public void receive(Message msg) {
        System.out.println("CP: received msg:"+msg);
        if ("START".equals(msg.name)){
            if (state== CurrentState.WAITING){
                state = CurrentState.LOGGING;
                timer.start();
                callback.inform("START processed");
            } else {
                callback.failure("Logging already runs");
            }
        } else if ("STOP".equals(msg.name)){
            if (state== CurrentState.LOGGING){
                timer.stop();
                current_session.stopAndRetrieve();
                state= CurrentState.WAITING;
            } else {
                callback.failure("No running session to stop");
            }
        } else {
            callback.failure("Unknown message: "+msg.name);
        }
    }
}
