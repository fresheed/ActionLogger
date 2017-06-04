package org.fresheed.actionlogger.android.mobile.screens;

import android.view.View;
import android.widget.Button;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.android.WearMessageAPIDispatcher;
import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.transfer.ProcessingPeer;

public class LogProcessingScreen extends DebugActivity{

    private MessageDispatcher data_dispatcher;
    private Button record_starter, record_stopper;
    private MessageReceiver processing_peer;

    @Override
    protected void setup() {
        data_dispatcher=new WearMessageAPIDispatcher(this);
        processing_peer=new ProcessingPeer(data_dispatcher, LogProcessingScreen.this);
        record_starter=(Button)findViewById(R.id.start_record);
        record_starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewLogSession();
            }
        });
        record_stopper=(Button)findViewById(R.id.stop_record);
        record_stopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLogSession();
            }
        });
    }

    private void startNewLogSession() {
        log("session started");
        data_dispatcher.sendAll(new Message("START"));
    }

    private void stopLogSession() {
        log("session stopped");
        data_dispatcher.sendAll(new Message("STOP"));
    }

    @Override
    protected int getLogViewId() {
        return R.id.log_view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record;
    }

}
