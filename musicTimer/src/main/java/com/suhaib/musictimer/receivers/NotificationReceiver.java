package com.suhaib.musictimer.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.suhaib.musictimer.TimerService;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(context, TimerService.class);
        context.stopService(serviceIntent);
    }

} 