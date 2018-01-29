package com.melay.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by matth on 1/26/2018.
 */

public class SyncService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        //...
        // Do work here, based on the contents of dataString
        //...
    }
}
