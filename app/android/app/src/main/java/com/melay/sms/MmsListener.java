package com.melay.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.melay.sync.MailboxArchiveService;

/**
 * Created by matth on 1/26/2018.
 */

public class MmsListener extends BroadcastReceiver {

    static String TAG = SmsListener.class.getCanonicalName();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        //TODO log these comments out to a file so we can read the debugging output
        Log.i(TAG, "Received a MMS message, starting archiving service in 1 second.");

        // give it a second to put the sms that just arrived into the inbox
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        context.startService(new Intent(context, MailboxArchiveService.class));
    }
}