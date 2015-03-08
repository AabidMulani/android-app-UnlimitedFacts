package com.probytemedia.amazingfacts;

import com.crashlytics.android.Crashlytics;
import com.orm.SugarApp;
import com.probytemedia.amazingfacts.activity.MainActivity;

import io.fabric.sdk.android.Fabric;

/**
 * Created by AABID on 20/4/14.
 */
public class BaseApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MainActivity.previousSelection = 0;
        MainActivity.selectedTab = -1;
    }
}
