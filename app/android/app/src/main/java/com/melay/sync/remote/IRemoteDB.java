package com.melay.sync.remote;

import com.melay.sync.local.LocalDatabase;

/**
 * Created by matth on 1/30/2018.
 */

public abstract class IRemoteDB {

    protected LocalDatabase localMessages;
    /**
    Syncs the data to the database
     */
    public void Sync(){
        SyncMessages();
    }

    /**
     * Syncs messages data to the database
     */
    protected abstract void SyncMessages();
}
