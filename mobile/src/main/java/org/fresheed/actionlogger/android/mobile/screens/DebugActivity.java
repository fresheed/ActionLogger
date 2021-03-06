package org.fresheed.actionlogger.android.mobile.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.android.WearMessageAPIDispatcher;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fresheed on 30.05.17.
 */

public abstract class DebugActivity extends AppCompatActivity implements MessageProcessedCallback {

    protected TextView last_messages_view;
    protected final List<String> last_messages=new ArrayList<>();
    protected WearMessageAPIDispatcher data_dispatcher;

    protected void updateLogs(String message){
        last_messages.add(0, message);
        if (last_messages.size()>=5){
            last_messages.remove(last_messages.size()-1);
        }
        last_messages_view.setText(TextUtils.join("\n", last_messages));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data_dispatcher=new WearMessageAPIDispatcher(this);
        setContentView(getLayoutId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        toolbar.setSubtitle(getClass().getSimpleName());
        setSupportActionBar(toolbar);
        last_messages_view=(TextView) findViewById(getLogViewId());
        setup();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.err.println("onstart");
        data_dispatcher.startProcessing();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.err.println("onstop");
        data_dispatcher.stopProcessing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Class activity_to_call;
        switch (item.getItemId()) {
            case R.id.transfer_screen_link: activity_to_call=LogTransferScreen.class; break;
            case R.id.processing_screen_link: activity_to_call=LogProcessingScreen.class; break;
            default: return false;
        }
        Intent intent = new Intent(this, activity_to_call);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
        return true;
    }

    @Override
    public void inform(String info) {
        updateLogs(info);
    }

    @Override
    public void failure(String info) {
        updateLogs(info);
    }

    protected static void log(String msg){
        Log.d("DEBUG ACTIVITY", msg);
    }

    protected abstract void setup();
    protected abstract int getLogViewId();
    protected abstract int getLayoutId();
}
