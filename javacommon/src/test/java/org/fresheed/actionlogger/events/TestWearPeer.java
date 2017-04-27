package org.fresheed.actionlogger.events;

import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;
import org.fresheed.actionlogger.transfer.WearPeer;
import org.fresheed.actionlogger.utils.EventsLogCompressor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
/**
 * Created by fresheed on 25.04.17.
 */

public class TestWearPeer {

    @Test
    public void shouldRejectUnknownMessage(){
        MockMessagingCallback callback=new MockMessagingCallback();
        WearPeer wear=new WearPeer(new MockMessageDispatcher(), new MockActionsSource(), callback);
        wear.receive(new Message("UNKNOWN"));
        assertThat(callback.logged_failures, is(1));
    }

    @Test
    public void shouldSendRecordedMessages() throws EventsLogCompressor.LogEncodingException {
        List<ActionEvent> events=new ArrayList<ActionEvent>(){{
            add(new ActionEvent(1, new float[]{1.0f, 2.0f, 3.0f}));
            add(new ActionEvent(2, new float[]{4.0f, 5.0f, 6.0f}));
        }};
        MockMessagingCallback callback=new MockMessagingCallback();
        MockActionsSource source=new MockActionsSource(events);
        MockMessageDispatcher dispatcher=new MockMessageDispatcher();
        WearPeer wear=new WearPeer(dispatcher, source, callback);
        wear.receive(new Message("START"));
        wear.receive(new Message("STOP"));
        assertThat(dispatcher.logged_messages.size(), is(1));
        assertThat(dispatcher.logged_messages.get(0).name, is("ACTION_LOG"));
        assertThat(dispatcher.logged_messages.get(0).payload.length, is(EventsLogCompressor.getEntrySize(3)*2));
        assertThat(callback.logged_failures, is(0));

    }

    @Test
    public void shouldRestartLoggingNormally() throws EventsLogCompressor.LogEncodingException {
        List<ActionEvent> events=new ArrayList<ActionEvent>(){{
            add(new ActionEvent(1, new float[]{1.0f, 2.0f, 3.0f}));
            add(new ActionEvent(2, new float[]{4.0f, 5.0f, 6.0f}));
        }};
        MockActionsSource source=new MockActionsSource(events);
        MockMessageDispatcher dispatcher=new MockMessageDispatcher();
        MockMessagingCallback callback=new MockMessagingCallback();
        WearPeer wear=new WearPeer(dispatcher, source, callback);
        wear.receive(new Message("START"));
        wear.receive(new Message("STOP"));
        wear.receive(new Message("START"));
        wear.receive(new Message("STOP"));
        assertThat(dispatcher.logged_messages.size(), is(2));
        assertThat(dispatcher.logged_messages.get(1).name, is("ACTION_LOG"));
        assertThat(dispatcher.logged_messages.get(1).payload.length, is(EventsLogCompressor.getEntrySize(3)*2));
        assertThat(callback.logged_failures, is(0));
    }

    @Test
    public void shouldFailOnEmptyLog() throws EventsLogCompressor.LogEncodingException {
        List<ActionEvent> events=new ArrayList<ActionEvent>();
        MockActionsSource source=new MockActionsSource(events);
        MockMessageDispatcher dispatcher=new MockMessageDispatcher();
        MockMessagingCallback callback=new MockMessagingCallback();
        WearPeer wear=new WearPeer(dispatcher, source, callback);
        wear.receive(new Message("START"));
        wear.receive(new Message("STOP"));
        assertThat(callback.logged_failures, is(1));
    }

    @Test
    public void shouldIgnoreSecondStart() throws EventsLogCompressor.LogEncodingException {
        List<ActionEvent> events=new ArrayList<ActionEvent>(){{
            add(new ActionEvent(1, new float[]{1.0f, 2.0f, 3.0f}));
            add(new ActionEvent(2, new float[]{4.0f, 5.0f, 6.0f}));
        }};
        MockActionsSource source=new MockActionsSource(events);
        MockMessageDispatcher dispatcher=new MockMessageDispatcher();
        MockMessagingCallback callback=new MockMessagingCallback();
        WearPeer wear=new WearPeer(dispatcher, source, callback);
        wear.receive(new Message("START"));
        wear.receive(new Message("START"));
        wear.receive(new Message("STOP"));
        assertThat(dispatcher.logged_messages.size(), is(1));
        assertThat(dispatcher.logged_messages.get(0).name, is("ACTION_LOG"));
        assertThat(dispatcher.logged_messages.get(0).payload.length, is(EventsLogCompressor.getEntrySize(3)*2));
        assertThat(callback.logged_failures, is(1));
    }

    @Test
    public void shouldIgnoreUnexpectedStop() throws EventsLogCompressor.LogEncodingException {
        MockActionsSource source=new MockActionsSource();
        MockMessageDispatcher dispatcher=new MockMessageDispatcher();
        MockMessagingCallback callback=new MockMessagingCallback();
        WearPeer wear=new WearPeer(dispatcher, source, callback);
        wear.receive(new Message("STOP"));
        assertThat(dispatcher.logged_messages.size(), is(0));
        assertThat(callback.logged_failures, is(1));
    }
}

class MockActionsSource implements ActionsSource{
    private final List<ActionEvent> mock_events;
    private static final int TEST_CARDINALITY=3;

    MockActionsSource(List<ActionEvent> mock_events){
        this.mock_events=mock_events;
    }

    MockActionsSource(){
        this.mock_events=new ArrayList<>();
    }
    @Override
    public LoggingSession startLoggingSession() {
        return new LoggingSession() {
            @Override
            public ActionLog stopAndRetrieve() {
                ActionLog log=new ActionLog(TEST_CARDINALITY);
                for (ActionEvent event: mock_events){
                    try {
                        log.addEvent(event);
                    } catch (LoggingException ignored) {}
                }
                return log;
            }
        };
    }
}

class MockMessageDispatcher implements MessageDispatcher {
    public List<Message> logged_messages=new ArrayList<>();
    @Override
    public void sendAll(Message msg) {
        logged_messages.add(msg);
    }

    @Override
    public void addReceiver(MessageReceiver receiver) {}
    @Override
    public void removeReceiver(MessageReceiver receiver) {}
}


class MockMessagingCallback implements MessageProcessedCallback {
    public int logged_failures=0;
    @Override
    public void inform(String data) {

    }

    @Override
    public void failure(String info) {
        logged_failures++;
    }
}

