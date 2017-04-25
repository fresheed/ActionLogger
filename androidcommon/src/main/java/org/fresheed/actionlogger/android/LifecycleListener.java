package org.fresheed.actionlogger.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by fresheed on 25.04.17.
 */

public class LifecycleListener implements Application.ActivityLifecycleCallbacks {


    public void register(Activity activity){
        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

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

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
