package com.melay.sync.data;

/**
 * Created by matth on 1/31/2018.
 */

public interface Syncable {
    //this is the current status of a message
    public enum DataStatus {
        ON_DEVICE, //if it's on device
        CHANGED_ON_DEVICE, //if it's on the device and has been synced but is now changed
        SYNCED, //if it's on the server and the device
        CHANGED_ON_SERVER, //if we've updated it on the server
        ON_SERVER, //if it's only on the server
    };

    /**
     * Updates the state of the syncable data
     * @param newState
     */
    public void UpdateState(DataStatus newState);

    /* gets the state */
    public DataStatus GetState();


}
