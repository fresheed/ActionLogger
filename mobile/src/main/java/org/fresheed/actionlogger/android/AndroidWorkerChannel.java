package org.fresheed.actionlogger.android;

import android.os.AsyncTask;

import org.fresheed.actionlogger.data_channels.DataChannel;

import java.io.InputStream;

/**
 * Created by fresheed on 31.01.17.
 */

public class AndroidWorkerChannel implements DataChannel {
    DataChannel wrapped;

     private class CallInBackgroundTask extends AsyncTask<Void, Void, Void> {
         String name;
         InputStream data;

         CallInBackgroundTask(String name, InputStream data){
             super();
             this.name=name;
             this.data=data;
         }

         protected Void doInBackground(Void ... args) {
             wrapped.send(name, data);
             return null;
         }
     }

    public AndroidWorkerChannel(DataChannel w){
        wrapped=w;
    }

    @Override
    public void send(String name, InputStream data) {
        new CallInBackgroundTask(name, data).execute();
    }
}
