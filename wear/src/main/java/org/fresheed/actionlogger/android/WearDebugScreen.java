package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.transfer.ChunkPeer;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.transfer.ScheduledTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fresheed on 02.02.17.
 */

public abstract class WearDebugScreen extends Activity implements MessageProcessedCallback {

    protected TextView last_messages_view;
    protected final List<String> last_messages=new ArrayList<>();
    protected WearMessageAPIDispatcher data_dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data_dispatcher=new WearMessageAPIDispatcher(this);
        setContentView(getLayoutId());
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final String header=getClass().getSimpleName();
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ((TextView)findViewById(R.id.wear_screen_id)).setText(header);
                last_messages_view=(TextView) findViewById(getLogViewId());
                Button to_transfer_button=(Button) findViewById(R.id.switch_to_transfer);
                to_transfer_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchToActivity(WearControlScreen.class);
                    }
                });
                Button to_processing_button=(Button) findViewById(R.id.switch_to_processing);
                to_processing_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchToActivity(ChunkLoggingScreen.class);
                    }
                });
            }
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setup();
    }

    protected abstract int getLayoutId();
    protected abstract int getLogViewId();
    protected abstract void setup();

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                last_messages_view.setText(TextUtils.join("\n", last_messages));
            }
        });
    }

    private void switchToActivity(Class activity_to_call){
        Intent intent = new Intent(this, activity_to_call);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        data_dispatcher.startProcessing();
    }

    @Override
    protected void onStop() {
        super.onStop();
        data_dispatcher.stopProcessing();
    }


}
