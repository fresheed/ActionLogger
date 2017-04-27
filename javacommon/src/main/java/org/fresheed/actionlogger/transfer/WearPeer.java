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
            if (state==CurrentState.LOGGING){
                callback.inform("Logging already runs");
            }
            state=CurrentState.LOGGING;
            current_session=actions_source.startLoggingSession();
            actions_source.startLoggingSession();
        } else if ("STOP".equals(msg.name)){
            if (state==CurrentState.WAITING){
                callback.inform("No running session to stop");
                return;
            }
            ActionLog log=current_session.stopAndRetrieve();
            if (log.getEvents().size()==0){
                callback.inform("Zero-length log received - falling back to initial condition");
                return;
            }
            byte[] compressed_log=new EventsLogCompressor().compressEventsLog(log);
            dispatcher.sendAll(new Message("ACTION_LOG", compressed_log));
            state=CurrentState.WAITING;
        } else {
            callback.inform("Unknown message: "+msg.name);
        }
    }
}
