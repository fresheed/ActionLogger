package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by fresheed on 25.04.17.
 */

abstract public class LifecycleListener {

    private LCListener listener;
    private Activity owner;

    public void register(Activity activity){
        listener=new LCListener();
        owner=activity;
        activity.getApplication().registerActivityLifecycleCallbacks(listener);
    }

    abstract public void onStoppedCallback();


    private class LCListener implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity.getClass().getSimpleName().equals(owner.getClass().getSimpleName())){
                LifecycleListener.this.onStoppedCallback();
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }


}
