package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.data_channels.DataChannel;
import org.fresheed.actionlogger.data_channels.DropboxChannel;
import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogTransferScreen extends Activity implements MessageReceiver {

    private MessageDispatcher data_dispatcher;
    private Button record_starter, record_stopper;
    private final DataChannel data_channel=new DropboxChannel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        data_dispatcher=new WearMessageAPIDispatcher(this);
        data_dispatcher.addReceiver(this);
        record_starter=(Button)findViewById(R.id.start_record);
        record_starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Starting record...", Toast.LENGTH_SHORT).show();
                startNewLogSession();
            }
        });
        record_stopper=(Button)findViewById(R.id.stop_record);
        record_stopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "Sending test action log...", Toast.LENGTH_SHORT).show();
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
    public void receive(Message msg) {
        if ("ACTION_LOG".equals(msg.name)){
            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String time_suffix=date_format.format(new Date(System.currentTimeMillis()));
            final String name="Log_"+time_suffix;
            final ByteArrayInputStream data_stream=new ByteArrayInputStream(msg.payload);
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    data_channel.send(name, data_stream);
                    return null;
                }
            }.execute();
        } else if ("ERROR".equals(msg.name)){
            Toast.makeText(getApplicationContext(), "Error at Wear side", Toast.LENGTH_SHORT).show();
        }
    }

    static void log(String msg){
        Log.d("LTS", msg);
    }

}
