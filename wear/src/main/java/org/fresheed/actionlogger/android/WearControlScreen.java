package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;
import org.fresheed.actionlogger.transfer.WearPeer;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

/**
 * Created by fresheed on 02.02.17.
 */

public class WearControlScreen extends Activity implements MessageProcessedCallback {

    private static final String TAG="WearControlScreen";

    private ActionsSource actions_source;

    private MessageDispatcher data_dispatcher;
    private MessageReceiver wear_peer;

    private TextView last_messages_view;
    private final List<String> last_messages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                data_dispatcher=new WearMessageAPIDispatcher(WearControlScreen.this);
                actions_source =new DeviceSensorActionsSource(WearControlScreen.this, Sensor.TYPE_ACCELEROMETER);
                wear_peer=new WearPeer(data_dispatcher, actions_source, WearControlScreen.this);
                last_messages_view=(TextView) findViewById(R.id.wear_last_messages);
            }
        });
    }
    @Override
    public void inform(String message) {
        updateLogs(message);
    }

    @Override
    public void failure(String message) {
        updateLogs(message);
    }

    private void updateLogs(String message){
        last_messages.add(0, message);
        if (last_messages.size()>=5){
            last_messages.remove(last_messages.size()-1);
        }
        last_messages_view.setText(TextUtils.join("\n", last_messages));
    }
}
