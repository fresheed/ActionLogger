package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionSource;
import org.fresheed.actionlogger.events.LoggingSession;
import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.utils.EventsLogCompressor;

import java.util.List;

/**
 * Created by fresheed on 02.02.17.
 */

public class WearControlScreen extends Activity implements MessageReceiver {

    private static final String TAG="WearControlScreen";

    private ActionSource actions_source;
    private LoggingSession current_session;

    private MessageDispatcher data_dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("oncreate");
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                data_dispatcher=new WearMessageAPIDispatcher(WearControlScreen.this);
                data_dispatcher.addReceiver(WearControlScreen.this);
                actions_source =new DeviceSensorActionSource(WearControlScreen.this, Sensor.TYPE_ACCELEROMETER);
            }
        });
    }

    @Override
    public void receive(Message msg) {
        if ("START".equals(msg.name)){
            current_session=actions_source.startLoggingSession();
            actions_source.startLoggingSession();
        } else if ("STOP".equals(msg.name)){
            List<ActionEvent> events=current_session.stopAndRetrieve();
            try {
                byte[] compressed_log=new EventsLogCompressor().compressEventsLog(events, events.get(0).getValues().length);
                data_dispatcher.sendAll(new Message("ACTION_LOG", compressed_log));
            } catch (EventsLogCompressor.LogEncodingException e) {
                data_dispatcher.sendAll(new Message("ERROR")); //29485242110394
            }

        }
    }

    static void log(String msg){
        Log.d("WCS", msg);
    }

}
