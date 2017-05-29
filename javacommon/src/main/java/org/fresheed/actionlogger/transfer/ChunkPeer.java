package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.events.LoggingSession;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

import java.util.Random;

/**
 * Created by fresheed on 25.04.17.
 */
public class ChunkPeer implements MessageReceiver {
    private enum CurrentState {
        WAITING, LOGGING
    }
    private CurrentState state= CurrentState.WAITING;

    private final MessageDispatcher dispatcher;
    private final MessageProcessedCallback callback;
    private final ScheduledTimer timer;

    public ChunkPeer(MessageDispatcher dispatcher, MessageProcessedCallback callback, ScheduledTimer timer){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.callback=callback;
        this.timer=timer;
        this.timer.addTask(restart_logging_task);
    }

    private final Runnable restart_logging_task=new Runnable() {
        @Override
        public void run() {
            callback.inform("Timer task occured: "+new Random().nextInt());
        }
    };

    @Override
    public void receive(Message msg) {
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
                state= CurrentState.WAITING;
            } else {
                callback.failure("No running session to stop");
            }
        } else {
            callback.failure("Unknown message: "+msg.name);
        }
    }
}
