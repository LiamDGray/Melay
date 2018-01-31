package com.melay.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.melay.sync.MailboxArchiveService;

/**
 * Created by matth on 1/26/2018.
 */

public class SmsListener extends WakefulBroadcastReceiver {

    static String TAG = SmsListener.class.getCanonicalName();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Log.i(TAG, "Received a message, starting archiving service in 1 second.");
        }
        else {
            Log.i(TAG, "Received a "+intent.getAction()+", starting archiving service in 1 second.");
        }

        // give it a second to put the sms that just arrived into the inbox
        try {
            Thread.sleep(1000);
        } catch(InterruptedException ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }

        context.startService(new Intent(context, MailboxArchiveService.class));
    }
}