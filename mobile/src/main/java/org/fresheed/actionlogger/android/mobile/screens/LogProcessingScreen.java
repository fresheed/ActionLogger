package org.fresheed.actionlogger.android.mobile.screens;

import android.view.View;
import android.widget.Button;

import org.fresheed.actionlogger.R;
import org.fresheed.actionlogger.android.WearMessageAPIDispatcher;
import org.fresheed.actionlogger.android.mobile.classification.ProcessorsProvider;
import org.fresheed.actionlogger.classification.ActivityKind;
import org.fresheed.actionlogger.classification.LogClassifier;
import org.fresheed.actionlogger.events.ActionLog;
import org.fresheed.actionlogger.transfer.Message;
import org.fresheed.actionlogger.transfer.MessageDispatcher;
import org.fresheed.actionlogger.transfer.MessageReceiver;
import org.fresheed.actionlogger.transfer.ProcessingPeer;

public class LogProcessingScreen extends DebugActivity{

    private MessageDispatcher data_dispatcher;
    private Button record_starter, record_stopper;
    private MessageReceiver processing_peer;

    @Override
    protected void setup() {
        data_dispatcher=new WearMessageAPIDispatcher(this);
        LogClassifier classifier=getActivityClassifier();
        processing_peer=new ProcessingPeer(data_dispatcher, LogProcessingScreen.this, classifier);
        record_starter=(Button)findViewById(R.id.start_record);
        record_starter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewLogSession();
            }
        });
        record_stopper=(Button)findViewById(R.id.stop_record);
        record_stopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLogSession();
            }
        });
    }

    private void startNewLogSession() {
        log("session started");
        data_dispatcher.sendAll(new Message("START"));
    }

    private void stopLogSession() {
        log("session stopped");
        data_dispatcher.sendAll(new Message("STOP"));
    }

    @Override
    protected int getLogViewId() {
        return R.id.log_view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record;
    }

    private LogClassifier getActivityClassifier() {
        ProcessorsProvider provider=new ProcessorsProvider(getResources());
        String classifier_code="RAW_MLP";
        try {
            LogClassifier classifier=provider.getSelectedClassifier(classifier_code);
            return classifier;
        } catch (ProcessorsProvider.ClassifierLoadException e) {
            failure("CANNOT RETRIEVE CLASSIFIER BY CODE "+classifier_code);
            LogClassifier mock=new LogClassifier() {
                @Override
                public ActivityKind classify(ActionLog log) {
                    return ActivityKind.PUSHUPS;
                }
            };
            return mock;
        }
    }
}
