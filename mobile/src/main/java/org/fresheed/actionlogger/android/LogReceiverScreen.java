package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.data_channels.DataChannel;
import org.fresheed.actionlogger.data_channels.DropboxChannel;
import org.fresheed.actionlogger.transfer.LogProcessingPeer;
import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;
import org.fresheed.actionlogger.transfer.MessageReceiver;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogReceiverScreen extends Activity implements MessageProcessedCallback{

    private MessageDispatcher data_dispatcher;
    private Button record_starter, record_stopper;
    private MessageReceiver processing_peer;

    private TextView last_messages_view;
    private final List<String> last_messages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        data_dispatcher=new WearMessageAPIDispatcher(this);
        //data_dispatcher.addReceiver(this);
        processing_peer=new LogProcessingPeer(data_dispatcher, LogReceiverScreen.this);
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
        last_messages_view= (TextView) findViewById(R.id.processing_messages);

    }

    private void startNewLogSession() {
        log("session started");
        data_dispatcher.sendAll(new Message("START"));
    }

    private void stopLogSession() {
        log("session stopped");
        data_dispatcher.sendAll(new Message("STOP"));
    }

    static void log(String msg){
        Log.d("LTS", msg);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                last_messages_view.setText(TextUtils.join("\n", last_messages));
            }
        });
    }
}
