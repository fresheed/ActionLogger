package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.transfer.ChunkPeer;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
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

public class ChunkLoggingScreen extends WearDebugScreen {

    private ActionsSource actions_source;
    private MessageReceiver wear_peer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getLogViewId() {
        return R.id.wear_last_messages;
    }

    @Override
    protected void setup() {
        actions_source =new DeviceSensorActionsSource(ChunkLoggingScreen.this, Sensor.TYPE_ACCELEROMETER);
        wear_peer=new ChunkPeer(data_dispatcher, ChunkLoggingScreen.this,
                actions_source, new AndroidTimer(1500, 1500));

    }

//    private static final String TAG="WearControlScreen";
//
//    private ActionsSource actions_source;
//
//    private WearMessageAPIDispatcher data_dispatcher;
//    private MessageReceiver wear_peer;
//
//    private TextView last_messages_view;
//    private final List<String> last_messages=new ArrayList<>();

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        data_dispatcher=new WearMessageAPIDispatcher(ChunkLoggingScreen.this);
//        actions_source =new DeviceSensorActionsSource(ChunkLoggingScreen.this, Sensor.TYPE_ACCELEROMETER);
//        wear_peer=new ChunkPeer(data_dispatcher, ChunkLoggingScreen.this,
//                actions_source, new AndroidTimer(1500, 1500));
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                last_messages_view=(TextView) findViewById(R.id.wear_last_messages);
//            }
//        });
//        // should be used for research purposes only!
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//    }



}

class AndroidTimer implements ScheduledTimer {
    private final int delay, period;
    private Runnable task=null;

    private Timer current_timer=null;


    AndroidTimer(int delay, int period){
        this.delay=delay;
        this.period=period;
    }

    @Override
    public void start() {
        if (current_timer!=null){
            throw new IllegalStateException("Already have running timer");
        }
        current_timer=new Timer();
        current_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay, period);
    }

    @Override
    public void stop() {
        if (current_timer==null){
            throw new IllegalStateException("No running timer now");
        }
        current_timer.cancel();
        current_timer=null;
    }

    @Override
    public void addTask(Runnable new_task) {
        if (task!=null){
            throw new IllegalStateException("Already have task to run");
        }
        task=new_task;
        }
};

