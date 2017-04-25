package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionSource;
import org.fresheed.actionlogger.events.LoggingSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fresheed on 30.01.17.
 */

public class DeviceSensorActionSource implements ActionSource {

    private final SensorManager sensor_manager;
    private final Sensor sensor;
    private final List<ActionEvent> logged_events=new ArrayList<>();
    private final List<DeviceLoggingSession> sessions_to_cleanup=new ArrayList<>();

    public DeviceSensorActionSource(Activity owner, int sensor_type){
        sensor_manager =(SensorManager)owner.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensor_manager.getDefaultSensor(sensor_type);
        new LifecycleListener(){
            @Override
            public void onActivityStopped(Activity activity){
                Toast.makeText(activity, "Shutting down all logging sessions", Toast.LENGTH_SHORT);
                for (DeviceLoggingSession session: sessions_to_cleanup){
                    session.stopAndRetrieve();
                }
            }
        }.register(owner);
    }

    @Override
    public LoggingSession startLoggingSession() {
        DeviceLoggingSession session=new DeviceLoggingSession();
        sessions_to_cleanup.add(session);
        return session;
    }

    private class DeviceLoggingSession implements LoggingSession, SensorEventListener{
        private final List<ActionEvent> logged_events=new ArrayList<>();

        DeviceLoggingSession(){
            if  (!sensor_manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)){
                throw new RuntimeException("Failed to start listening for sensor events - processing not implemented");
            }
        }

        @Override
        public List<ActionEvent> stopAndRetrieve() {
            sensor_manager.unregisterListener(this);
            return logged_events;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != sensor.getType()) {
                return;
            }
            // seems like system passes same array here, so copy it
            logged_events.add(new ActionEvent(event.timestamp, event.values.clone()));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
}
