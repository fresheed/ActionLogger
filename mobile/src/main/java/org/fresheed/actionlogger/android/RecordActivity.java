package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.fresheed.actionlogger.data_channels.DataChannel;
import org.fresheed.actionlogger.data_channels.DropboxChannel;
import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.LoggerConfigException;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.utils.EventsWriter;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordActivity extends Activity{// implements MessageReceiver {

    DataChannel data_channel=new DropboxChannel();
    //private MessageDispatcher wear_dispatcher;

    EditText record_name_input;
    RadioGroup sensor_select;
    Button record_starter, record_stopper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //wear_dispatcher=new WearDataLayerDispatcher(this);
        //wear_dispatcher.addReceiver(this);

        LinearLayout record_layout = (LinearLayout) findViewById(R.id.record_layout);
        record_name_input=(EditText)record_layout.findViewById(R.id.record_name);
        sensor_select=(RadioGroup)record_layout.findViewById(R.id.sensor_type);
        record_starter=(Button)record_layout.findViewById(R.id.start_record);
        record_starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Starting record...", Toast.LENGTH_SHORT).show();
                startNewLog();
            }
        });
        record_stopper=(Button)record_layout.findViewById(R.id.stop_record);
        record_stopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sending test action log...", Toast.LENGTH_SHORT).show();
                sendLog();
            }
        });
    }

    private void startNewLog(){
//        current_listener=new DeviceActionLogger(this, Sensor.TYPE_ACCELEROMETER);
//        try{
//            current_listener.startLogging();
//        } catch (LoggerConfigException lce){
//            Toast.makeText(getApplicationContext(), "Failed to start log", Toast.LENGTH_SHORT).show();
//        }
    }

    private void sendLog(){
//        try{
//            current_listener.stopLogging();
//            List<ActionEvent> logged_events=current_listener.getLoggedEvents();
//            String log_data=new EventsWriter().writeEventsLog(logged_events);
//            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//            String time_suffix=date_format.format(new Date(System.currentTimeMillis()));
//            data_channel.send("Log_"+time_suffix, new ByteArrayInputStream(log_data.getBytes(Charset.forName("UTF-8"))));
//        } catch (LoggerConfigException lce){
//            Toast.makeText(getApplicationContext(), "Failed to stop log", Toast.LENGTH_SHORT).show();
//        }
    }

//    @Override
//    public void receive(byte[] raw_data) {
//        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String time_suffix=date_format.format(new Date(System.currentTimeMillis()));
//        final String name="Log_"+time_suffix;
//        final ByteArrayInputStream data_stream=new ByteArrayInputStream(raw_data);
//        new AsyncTask<Void, Void, Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                data_channel.send(name, data_stream);
//                return null;
//            }
//        }.execute();
//
//        Toast.makeText(getApplicationContext(), "Sent data...", Toast.LENGTH_SHORT).show();
//    }
}