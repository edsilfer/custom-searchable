package br.com.customsearchable.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by edgar on 6/21/15.
 */
public class CustomSearchableContext extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        CustomSearchableContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return CustomSearchableContext.context;
    }
}