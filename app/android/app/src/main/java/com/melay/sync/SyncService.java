package com.melay.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.melay.sync.remote.IRemoteDB;
import com.melay.sync.remote.RemoteDBFactory;

/**
 * Created by matth on 1/26/2018.
 * This class syncs to the server in your preferences
 */

public class SyncService extends IntentService {

    static String TAG = SyncService.class.getCanonicalName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncService(String name) {
        super(name);
    }

    public SyncService() {
        this("SyncService");
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        Log.i(TAG, "Handling Intent");

        //TODO if we have run within the last 30 seconds, we should not update
        //...
        RemoteDBFactory factory = new RemoteDBFactory(this);
        IRemoteDB db = factory.CreateDB();
        // Do work here, based on the contents of dataString
        //...
        Log.i(TAG, "Finished Handling Intent");
    }
}
