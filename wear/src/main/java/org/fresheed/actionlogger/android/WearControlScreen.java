package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.text.TextUtils;
import android.view.WindowManager;
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

public class WearControlScreen extends WearDebugScreen {

    private ActionsSource actions_source;
    private MessageReceiver wear_peer;

    @Override
    protected void setup() {
        actions_source =new DeviceSensorActionsSource(WearControlScreen.this, Sensor.TYPE_ACCELEROMETER);
        wear_peer=new WearPeer(data_dispatcher, actions_source, WearControlScreen.this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getLogViewId() {
        return R.id.wear_last_messages;
    }

}
