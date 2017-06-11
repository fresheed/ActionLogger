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

    private final ActionsSource actions_source;
    private LoggingSession current_session=null;

    public ChunkPeer(MessageDispatcher dispatcher, MessageProcessedCallback callback,
                     ActionsSource actions_source, ScheduledTimer timer){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.callback=callback;
        this.actions_source=actions_source;
        this.timer=timer;
        this.timer.addTask(restart_logging_task);
    }

    private final Runnable restart_logging_task=new Runnable() {
        @Override
        public void run() {
            //callback.inform("Timer task occured: "+new Random().nextInt());
            //dispatcher.sendAll(new Message("ERROR"));

//            ActionLog test_log=new ActionLog(3);
//            try {
//                test_log.addEvent(new ActionEvent(100, new float[]{1.0f, 2.0f, 3.0f}));
//                dispatcher.sendAll(new Message("ACTION_LOG", new EventsLogCompressor().compressEventsLog(test_log)));
//            } catch (LoggingException e) {
//                throw new RuntimeException("should not happen");
//            }

            if (current_session!=null){
                ActionLog log=current_session.stopAndRetrieve();
                byte[] payload=new EventsLogCompressor().compressEventsLog(log);
                dispatcher.sendAll(new Message("ACTION_LOG", payload));
                callback.inform("sent log");
            }
            current_session=actions_source.startLoggingSession();
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
                if (current_session!=null){
                    current_session.stopAndRetrieve();
                }
                callback.inform("stopped log collecting");
                state= CurrentState.WAITING;
            } else {
                callback.failure("No running session to stop");
            }
        } else {
            callback.failure("Unknown message: "+msg.name);
        }
    }
}
