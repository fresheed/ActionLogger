package org.fresheed.actionlogger.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.transfer.MessageProcessedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fresheed on 30.05.17.
 */

public abstract class DebugActivity extends AppCompatActivity implements MessageProcessedCallback {

    protected TextView last_messages_view;
    protected final List<String> last_messages=new ArrayList<>();

    protected void updateLogs(String message){
        last_messages.add(0, message);
        if (last_messages.size()>=5){
            last_messages.remove(last_messages.size()-1);
        }
        last_messages_view.setText(TextUtils.join("\n", last_messages));
        last_messages_view=(TextView) findViewById(getLogViewId());

        setup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        Toolbar toolbar = (Toolbar) findViewById(R.id.navigation_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
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
