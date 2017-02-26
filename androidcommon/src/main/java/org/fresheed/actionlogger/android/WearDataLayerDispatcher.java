package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by fresheed on 05.02.17.
 */

public class WearDataLayerDispatcher implements MessageDispatcher, GoogleApiClient.ConnectionCallbacks,
        DataApi.DataListener, GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG="WDLDispatcher";
    private static final String DATA_MAP ="/LOG_DATA_MAP";
    private static final String MESSAGE_NAME_ITEM="MESSAGE_NAME";
    private static final String MESSAGE_PAYLOAD_ITEM="MESSAGE_PAYLOAD";
    private static final String ARTIFICAL_CHANGE_ITEM="MESSAGE_RANDOM";

    private GoogleApiClient api_client;
    private Context android_context;

    private final Set<MessageReceiver> receivers=new HashSet<>();

    public WearDataLayerDispatcher(final Activity owner){
        android_context=owner;
        api_client = new GoogleApiClient.Builder(android_context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        if (!api_client.isConnected()) {
            api_client.connect();
        }
        owner.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStarted(Activity activity) {
                if (activity.getPackageName().equals(owner.getPackageName())){
                    if (!api_client.isConnected()) {
                        api_client.connect();
                    }
                }

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity.getPackageName().equals(owner.getPackageName())){
                    Wearable.DataApi.removeListener(api_client, WearDataLayerDispatcher.this);
                    api_client.disconnect();
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            @Override
            public void onActivityResumed(Activity activity) {}
            @Override
            public void onActivityPaused(Activity activity) {}
            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
        });
    }

    @Override
    public void sendAll(Message msg) {
        PutDataMapRequest data_request = PutDataMapRequest.create(DATA_MAP);
        data_request.getDataMap().putString(MESSAGE_NAME_ITEM, msg.name);
        data_request.getDataMap().putByteArray(MESSAGE_PAYLOAD_ITEM, msg.payload);
        data_request.getDataMap().putInt(ARTIFICAL_CHANGE_ITEM, new Random().nextInt());
        PutDataRequest putDataReq = data_request.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pending_result = Wearable.DataApi.putDataItem(api_client, putDataReq);
        pending_result.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult result) {
                System.out.println("result:");
                System.out.println(result.getStatus());
                if(result.getStatus().isSuccess()) {
                    Log.d(TAG, "Data item set: " + result.getDataItem().getUri());
                }
            }
        });
        Toast.makeText(android_context, "Send requested", Toast.LENGTH_SHORT).show();
    }

    private void notifyReceivers(Message msg){
        for (MessageReceiver receiver: receivers){
            log("notifying");
            receiver.receive(msg);
        }
    }

    @Override
    public void addReceiver(MessageReceiver receiver) {
        receivers.add(receiver);
    }

    @Override
    public void removeReceiver(MessageReceiver receiver) {
        receivers.remove(receiver);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);
        Wearable.DataApi.addListener(api_client, this);
        Toast.makeText(android_context, "Connection established", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        log("data changed");
        for (DataEvent event: dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals(DATA_MAP)) {
                    DataMap data_map = DataMapItem.fromDataItem(item).getDataMap();
                    String message_name=data_map.getString(MESSAGE_NAME_ITEM);
                    byte[] raw_data=data_map.getByteArray(MESSAGE_PAYLOAD_ITEM);
                    notifyReceivers(new Message(message_name, raw_data));
                }
            }
        }
    }

    static void log(String msg){
        Log.d("WDLD", msg);
    }
}
