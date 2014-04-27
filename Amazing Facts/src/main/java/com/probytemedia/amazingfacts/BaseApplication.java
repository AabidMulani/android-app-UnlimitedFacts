package com.probytemedia.amazingfacts;

import com.orm.SugarApp;
import com.probytemedia.amazingfacts.activity.MainActivity;

/**
 * Created by AABID on 20/4/14.
 */
public class BaseApplication extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
//        BugSenseHandler.initAndStartSession(getApplicationContext(), "634bac96");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        MainActivity.previousSelection = 0;
        MainActivity.selectedTab = -1;
    }
}
