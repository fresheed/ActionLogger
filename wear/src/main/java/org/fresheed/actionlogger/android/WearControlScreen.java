package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;
import org.fresheed.actionlogger.transfer.WearPeer;

/**
 * Created by fresheed on 02.02.17.
 */

public class WearControlScreen extends Activity implements MessageProcessedCallback {

    private static final String TAG="WearControlScreen";

    private ActionsSource actions_source;

    private MessageDispatcher data_dispatcher;
    private MessageReceiver wear_peer;

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
                actions_source =new DeviceSensorActionsSource(WearControlScreen.this, Sensor.TYPE_ACCELEROMETER);
                wear_peer=new WearPeer(data_dispatcher, actions_source, WearControlScreen.this);
            }
        });
    }

    static void log(String msg){
        Log.d("WCS", msg);
    }

    @Override
    public void inform() {
        Toast.makeText(this, "Error occured on message processing", Toast.LENGTH_SHORT);
    }
}
