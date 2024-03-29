package com.fowllanguagecomics.android;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fowllaguagecomics.android.R;
import com.fowllanguagecomics.android.services.ComicLoaderService;
import com.parse.ParsePushBroadcastReceiver;

public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final String LOG_TAG = PushBroadcastReceiver.class.getSimpleName();

    @Override
    protected int getSmallIconId(Context context, Intent intent) {
        return R.drawable.dicky;
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);
        Log.d(LOG_TAG, "Push received!");
        ComicLoaderService.createInstance(context);
        ComicLoaderService.getInstance().fetchComics();
    }
}
