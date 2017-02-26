package org.fresheed.actionlogger.android;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.fresheed.actionlogger.events.ActionEvent;
import org.fresheed.actionlogger.events.ActionRecorder;

/**
 * Created by fresheed on 30.01.17.
 */

public class AndroidSensorActionRecorder extends ActionRecorder {

    private final SensorManager sensor_manager;
    private final Sensor sensor;
    private final SensorEventListener sensor_listener=new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() != sensor.getType()) {
                    return;
                }
                // seems like system passes same array here, so copy it
                addEvent(new ActionEvent(event.timestamp, event.values.clone()));
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };

    public AndroidSensorActionRecorder(Context context, int sensor_type){
        sensor_manager =(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensor_manager.getDefaultSensor(sensor_type);
    }

    @Override
    protected void setupSpecific() {
        sensor_manager.registerListener(sensor_listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void stopSpecific() {
        sensor_manager.unregisterListener(sensor_listener);
    }
}
