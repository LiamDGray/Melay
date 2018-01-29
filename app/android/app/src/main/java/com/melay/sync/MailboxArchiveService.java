package com.melay.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.melay.sync.data.MelayMessage;

/**
 * Created by matth on 1/28/2018.
 * This class reads the mailbox
 */

public class MailboxArchiveService extends Service {


    static String TAG = MailboxArchiveService.class.getCanonicalName();

    ArchivingThread thread;

    public MailboxArchiveService() {

    }


    private void ensureSetup() {
        if (thread == null) {
            thread = new ArchivingThread();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "ArchivingService started!");

        ensureSetup();
        thread.start();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "ArchivingService stopped!");
    }

    class ArchivingThread extends Thread {

        @Override
        public void run() {
            try {

                List<MelayMessage> data = new ArrayList<MelayMessage>();
                int cutoffId = 0;//preferences.getArchivingCutoffId();

                int newCutoffId = Math.max(
                        addUnarchived(data, "content://sms/inbox", cutoffId, true),
                        addUnarchived(data, "content://sms/sent", cutoffId, false)
                );

                if (data.size() > 0) {
                    //client.archive(data.toArray(new Sms[] {}));
                    //preferences.setArchivingCutoffId(newCutoffId);
                    Log.i(TAG, "Successfully archived " + data.size() + " messages.");
                } else {
                    Log.i(TAG, "Nothing to archive.");
                }


            } catch (Exception ex) {
                Log.e(TAG, "error archiving messages", ex);
            } finally {
                stopSelf();
            }
        }

        private int addUnarchived(List<MelayMessage> data, String contentUri, int cutoffId, boolean incoming) {

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse(contentUri),
                    new String[]{"_id", "address", "body", "date"},
                    "_id > ?", new String[]{cutoffId + ""}, "_id");

            while (cursor.moveToNext()) {
                data.add(fromCursor(cursor, incoming));
                cutoffId = cursor.getInt(0);
            }

            cursor.close();

            return cutoffId;
        }

        private MelayMessage fromCursor(Cursor cursor, boolean incoming) {
            int paramIdx = 0;
            return new MelayMessage(
                    -1,
                    cursor.getInt(paramIdx++),
                    cursor.getString(paramIdx++),
                    cursor.getString(paramIdx++),
                    new Date(cursor.getLong(paramIdx++)),
                    incoming);
        }
    }
}
