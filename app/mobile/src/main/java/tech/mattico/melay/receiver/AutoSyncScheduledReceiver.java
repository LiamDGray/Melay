package tech.mattico.melay.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tech.mattico.melay.services.AutoSyncScheduledService;

public class AutoSyncScheduledReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AutoSyncScheduledService.sendWakefulWork(context, AutoSyncScheduledService.class);
    }
}
