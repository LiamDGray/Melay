package com.melay.sync.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by matth on 1/30/2018.
 */

public class RemoteDBFactory {
    static String TAG = RemoteDBFactory.class.getCanonicalName();

    public RemoteDBFactory(Context context){
        //Loads preferences from settings
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String language = settings.getString("AZURE_CONN_STRING", "");
        Log.i(TAG,"Connecting to Azure with this "+language);
    }
    /**
     * Reads the preferences and creates the correct databse
     * @return
     */
    public IRemoteDB CreateDB(){
        return new RemoteAzureDB();
    }
}
