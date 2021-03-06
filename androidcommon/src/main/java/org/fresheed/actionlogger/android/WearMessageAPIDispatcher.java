package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fresheed on 05.02.17.
 */

public class WearMessageAPIDispatcher implements MessageDispatcher, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, MessageApi.MessageListener {

    private static final String TAG="WMAPID";
    private static final String MESSAGES_PATH_PREFIX ="/LOGGER_MESSAGES-";
    private static final int MAX_PAYLOAD_LENGTH=100000;

    private final GoogleApiClient api_client;
    private final Context android_context;

    private final Set<MessageReceiver> receivers=new HashSet<>();

    public WearMessageAPIDispatcher(final Activity owner){
        android_context=owner;
        api_client = new GoogleApiClient.Builder(android_context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
    }

    // These methods break incapsulation, but since lifecycle listeners don't work well
    // we have to control dispatcher manually

    public void startProcessing(){
        if (!api_client.isConnected()) {
            api_client.connect();
            System.err.println("Calling connect from dispatcher");
        }
    }

    public void stopProcessing(){
        Wearable.MessageApi.removeListener(api_client, WearMessageAPIDispatcher.this);
        if (!api_client.isConnected()) {
            api_client.disconnect();
        }
    }

    @Override
    public void sendAll(final Message msg) {
        final String full_path= MESSAGES_PATH_PREFIX +msg.name;
        if (msg.payload.length>MAX_PAYLOAD_LENGTH){
            throw new IllegalArgumentException("Payload too large - "+msg.payload.length);
        }
        new AsyncTask<Void, Void, Void>(){
            protected Void doInBackground(Void... urls) {
                for  (Node node: Wearable.NodeApi.getConnectedNodes(api_client).await().getNodes()){
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(api_client, node.getId(),
                            full_path, msg.payload).await();
                    if (result.getStatus().isSuccess()) {
                        log("successfully sent message");
                    } else {
                        log("error on send");
                    }
                }
                return null;
            }
        }.execute();
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
        Wearable.MessageApi.addListener(api_client, this);
        System.err.println("OnConnected");
        //Thread.dumpStack();
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

    static void log(String msg){
        Log.i("WDLD", msg);
    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        if (event.getPath().startsWith(MESSAGES_PATH_PREFIX)) {
            String name=event.getPath().replace(MESSAGES_PATH_PREFIX, "");
            byte[] payload=event.getData();
            notifyReceivers(new Message(name, payload));
        }
    }
}
