package com.melay.sync.local;

import java.util.Date;

/**
 * Created by matth on 1/30/2018.
 * TODO create transparent encryption and decryption service
 */

public class LocalMessageDatabase {
    /**
     * Gets the last read message id
     * @return
     */
    public int GetLastReadMessageId() {
        return 0;
    }

    public Date GetLastRun() {
        Date time =  new Date();
        return time;
    }
}
