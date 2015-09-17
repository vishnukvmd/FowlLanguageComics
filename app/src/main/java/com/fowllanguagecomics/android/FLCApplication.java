package com.fowllanguagecomics.android;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class FLCApplication extends Application {
    private static final String PARSE_APPLICATION_ID = "iSsDzFH66ybndoGKzWVr9RH6P7KYwsscDVWVkAYr";
    private static final String PARSE_CLIENT_KEY = "9GNHPYb7U002UJfDc3QWwHErS7zQJDR3WLHnQadq";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.setLogLevel(Log.VERBOSE);
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
