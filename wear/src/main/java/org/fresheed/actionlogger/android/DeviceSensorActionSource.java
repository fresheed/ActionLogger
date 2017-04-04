package org.fresheed.actionlogger.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionRecorder;
import org.fresheed.actionlogger.events.ActionSource;

/**
 * Created by fresheed on 30.01.17.
 */

public class DeviceSensorActionSource implements ActionSource {

    private final SensorManager sensor_manager;
    private final Sensor sensor;
    private final SensorEventListener sensor_listener=new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() != sensor.getType()) {
                    return;
                }
                // seems like system passes same array here, so copy it
                recorder.addEvent(new ActionEvent(event.timestamp, event.values.clone()));
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
    private ActionRecorder recorder;

    public DeviceSensorActionSource(Context context, int sensor_type){
        sensor_manager =(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensor_manager.getDefaultSensor(sensor_type);
    }

    @Override
    public void setRecorder(ActionRecorder recorder) {
        this.recorder=recorder;
    }

    @Override
    public void activate() {
        sensor_manager.registerListener(sensor_listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void deactivate() {
        sensor_manager.unregisterListener(sensor_listener);
    }
}
