package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.data_channels.DataChannel;
import org.fresheed.actionlogger.data_channels.DropboxChannel;
import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActivityDetectionScreen extends Activity implements OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    // https://code.tutsplus.com/tutorials/google-fit-for-android-reading-sensor-data--cms-25723

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean auth_running = false;
    private GoogleApiClient api_client;

    private List<String> last_messages=new ArrayList<String>(){{
        add("1");
        add("2");
        add("3");
        add("4");
        add("5");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);

        if (savedInstanceState != null) {
            auth_running = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        api_client = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        api_client.connect();
    }

    static void log(String msg){
        Log.d("ADS", msg);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( //DataType.TYPE_STEP_COUNT_CUMULATIVE
                        DataType.TYPE_WORKOUT_EXERCISE
                )
                .build();
        log("created request");

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                log("got result:"+dataSourcesResult.getStatus());
                log("available sources:"+dataSourcesResult.getDataSources().size());
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    log("can read type:"+dataSource.getDataType().getName());
                    if( DataType.TYPE_WORKOUT_EXERCISE.equals( dataSource.getDataType() ) ) {
                        log("matching type found");
                        registerFitnessDataListener(dataSource, DataType.TYPE_WORKOUT_EXERCISE);
                    }
//                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType() ) ) {
//                        log("matching type found");
//                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
//                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(api_client, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {

        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                .setSamplingRate( 3, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add( api_client, request, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        log("final status:"+status.getStatus());
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
}

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            auth_running = false;
            if( resultCode == RESULT_OK ) {
                if( !api_client.isConnecting() && !api_client.isConnected() ) {
                    api_client.connect();
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if( !auth_running ) {
            try {
                auth_running = true;
                connectionResult.startResolutionForResult( ActivityDetectionScreen.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {
                Log.e("ADS", "Cannot start resolution for result: "+e.toString());
            }
        } else {
            Log.e("ADS", "authInProgress");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Fitness.SensorsApi.remove( api_client, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            api_client.disconnect();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, auth_running);
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        log("data point arrived");
        for(final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue( field );
            last_messages.add(0, "data: "+value);
            last_messages.remove(last_messages.size()-1);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView log_view= (TextView) findViewById(R.id.fit_log);
                log_view.setText(TextUtils.join("\n", last_messages));
            }
        });
    }
}
