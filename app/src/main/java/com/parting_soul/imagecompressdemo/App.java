package com.parting_soul.imagecompressdemo;

import android.app.Application;
import android.content.Context;

/**
 * @author parting_soul
 * @date 2019/4/2
 */
public class App extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }


}
