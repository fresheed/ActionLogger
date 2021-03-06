package org.fresheed.actionlogger.transfer;

import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.events.LoggingSession;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

/**
 * Created by fresheed on 25.04.17.
 */
public class WearPeer implements MessageReceiver {
    private enum CurrentState {
        WAITING, LOGGING
    }
    private CurrentState state=CurrentState.WAITING;

    private final MessageDispatcher dispatcher;

    private final ActionsSource actions_source;
    private LoggingSession current_session;

    private final MessageProcessedCallback callback;

    public WearPeer(MessageDispatcher dispatcher, ActionsSource actions_source, MessageProcessedCallback callback){
        this.dispatcher=dispatcher;
        dispatcher.addReceiver(this);
        this.actions_source=actions_source;
        this.callback=callback;
    }

    @Override
    public void receive(Message msg) {
        if ("START".equals(msg.name)){
            if (state==CurrentState.WAITING){
                state = CurrentState.LOGGING;
                current_session = actions_source.startLoggingSession();
                callback.inform("START processed");
            } else {
                callback.failure("Logging already runs");
            }
        } else if ("STOP".equals(msg.name)){
            if (state==CurrentState.LOGGING){
                ActionLog log=current_session.stopAndRetrieve();
                if (log.getEvents().size()!=0){
                    byte[] compressed_log=new EventsLogCompressor().compressEventsLog(log);
                    dispatcher.sendAll(new Message("ACTION_LOG", compressed_log));
                    callback.inform("STOP processed");
                } else {
                    callback.failure("Zero-length log received - falling back to initial condition");
                }
                state=CurrentState.WAITING;
            } else {
                callback.failure("No running session to stop");
            }
        } else {
            callback.failure("Unknown message: "+msg.name);
        }
    }
}
