package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Toast;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.events.ActionsSource;
import org.fresheed.actionlogger.events.LoggingException;
import org.fresheed.actionlogger.events.LoggingSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fresheed on 30.01.17.
 */

public class DeviceSensorActionsSource implements ActionsSource {

    private static final Map<Integer, Integer> sensors_cardinalities=new HashMap<Integer, Integer>(){{
        put(Sensor.TYPE_ACCELEROMETER, 3);
    }};

    private final SensorManager sensor_manager;
    private final Sensor sensor;
    private final List<DeviceLoggingSession> sessions_to_cleanup=new ArrayList<>();

    public DeviceSensorActionsSource(final Activity owner, int sensor_type){
        sensor_manager =(SensorManager)owner.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensor_manager.getDefaultSensor(sensor_type);
        new LifecycleListener(){
            @Override
            public void onStoppedCallback(){
                Toast.makeText(owner, "Shutting down all logging sessions", Toast.LENGTH_SHORT);
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
        private final ActionLog log=new ActionLog(sensors_cardinalities.get(sensor.getType()));

        DeviceLoggingSession(){
            if  (!sensor_manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)){
                throw new RuntimeException("Failed to start listening for sensor events; error processing not implemented");
            }
        }

        @Override
        public ActionLog stopAndRetrieve() {
            sensor_manager.unregisterListener(this);
            return log;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != sensor.getType()) {
                return;
            }
            // seems like system passes same array here, so copy it
            try {
                log.addEvent(new ActionEvent(event.timestamp, event.values.clone()));
            } catch (LoggingException e) {
                stopAndRetrieve();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
}
