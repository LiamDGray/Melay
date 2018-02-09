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
import com.melay.sync.data.Syncable;
import com.melay.sync.local.LocalDatabase;

/**
 * Created by matth on 1/28/2018.
 * This class reads the SMS/MMS mailbox and adds them to the local database
 */

//TODO: Add to a local database
//TODO: keep track of the last read ID
//TODO: properly parse MMS messages

//TODO: this should run on a schedule
public class MailboxArchiveService extends Service {


    static String TAG = MailboxArchiveService.class.getCanonicalName();

    ArchivingThread thread;

    LocalDatabase localDB;

    public MailboxArchiveService() {

    }


    private void ensureSetup() {
        if (thread == null) {
            thread = new ArchivingThread();
        }

        if (localDB == null) {
            localDB = new LocalDatabase();
            localDB.setContext(this);
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

                Date lastRun = localDB.GetLastRun();
                //TODO if we have run within the last 30 seconds, we should not update
                //TODO we should only import a max number at time otherwise wait for the job scheduler to fire
                List<MelayMessage> data = new ArrayList<MelayMessage>();

                int cutoffId = localDB.GetLastReadMessageId();
                Log.i(TAG, "Reading all messages greater than " + cutoffId);

                addUnarchivedSMS(data, "content://sms/inbox", cutoffId, true);
                addUnarchivedSMS(data, "content://sms/sent", cutoffId, false);


                //TODO add functionality for drafts
                //addUnarchivedSMS(data, "content://sms/drafts", cutoffId, true)

                //TODO add functionality for MMS
                /*newCutoffId = Math.max(
                        newCutoffId,
                        addUnarchivedMMS(data, "content://mms/inbox", cutoffId, false)
                );

                newCutoffId = Math.max(
                        newCutoffId,
                        addUnarchivedMMS(data, "content://mms/sent", cutoffId, true)
                );*/

                if (data.size() > 0) {
                    boolean archiveSuccess = localDB.ProcessMessages(data);

                    //preferences.setArchivingCutoffId(newCutoffId);
                    Log.i(TAG, "Successfully archived " + data.size() + " messages.");
                } else {
                    Log.i(TAG, "Nothing to archive.");
                }


            } catch (Exception ex) {
                Log.e(TAG, "error archiving messages", ex);
            } finally {
                //TODO call sync service?
                stopSelf();
            }

        }

        private int addUnarchivedSMS(List<MelayMessage> data, String contentUri, int cutoffId, boolean incoming) {

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse(contentUri),
                    new String[]{"_id", "address", "body", "date", "seen"},
                    "_id > ?", new String[]{cutoffId + ""}, "_id");

            while (cursor.moveToNext()) {
                data.add(smsFromCursor(cursor, incoming));
                MelayMessage newMessage = smsFromCursor(cursor, incoming);
                newMessage.UpdateState(Syncable.DataStatus.CHANGED_ON_DEVICE);
                data.add(newMessage);
                //TODO figure out where to encrypt it
            }

            cursor.close();
            //return the last seen ID
            return cutoffId;
        }

        private int addUnarchivedMMS(List<MelayMessage> data, String contentUri, int cutoffId, boolean incoming) {

            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(Uri.parse(contentUri),
                    new String[]{"message_id", "address", "content_class", "content_location", "content_type", "body", "date", "text_only", "seen", "creator"},
                    "message_id > ?", new String[]{cutoffId + ""}, "message_id");

            while (cursor.moveToNext()) {
                MelayMessage newMessage = smsFromCursor(cursor, incoming);
                newMessage.UpdateState(Syncable.DataStatus.CHANGED_ON_DEVICE);
                data.add(newMessage);
                //TODO figure out where to encrypt it

            }

            cursor.close();

            return cutoffId;
        }

        private MelayMessage smsFromCursor(Cursor cursor, boolean incoming) {
            int paramIdx = 0;
            return new MelayMessage(
                    -1,
                    cursor.getInt(paramIdx++), //id
                    cursor.getString(paramIdx++), //address
                    cursor.getString(paramIdx++), //body
                    new Date(cursor.getLong(paramIdx++)), //date
                    cursor.getInt(paramIdx++),
                    incoming);
        }
    }
}
