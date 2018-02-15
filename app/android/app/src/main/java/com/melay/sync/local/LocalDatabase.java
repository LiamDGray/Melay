package com.melay.sync.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.melay.sync.SyncLog;
import com.melay.sync.data.MelayMessage;
import com.melay.sync.data.Syncable;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.Date;
import java.util.List;

/**
 * Created by matth on 1/30/2018.
 * TODO create transparent encryption and decryption service
 */
@Database(name = LocalDatabase.NAME, version = LocalDatabase.VERSION)

public class LocalDatabase {
    //get the name of the database
    public static final String NAME = "AppDatabase";

    public static final int VERSION = 1;


    private Context context;

    public void setContext(Context con){
        context = con;
    }

    public Date GetLastRun() {
        Date time = new Date();
        return time;
    }

    public boolean ProcessMessages(List<MelayMessage> data){
        SyncLog.write(NAME,"Processing messages "+data.size());
        try {
            DatabaseDefinition db = FlowManager.getDatabase(LocalDatabase.class);
            ModelAdapter<MelayMessage> adapter = FlowManager.getModelAdapter(MelayMessage.class);

            int lastReadID = -1;
            for (MelayMessage message : data) {
                SyncLog.write(NAME, message.toString());
                if (message.isRead() && message.getId() > lastReadID) {
                    lastReadID = message.getId();
                }
                message.UpdateState(Syncable.DataStatus.ON_DEVICE);
                //insert it into the database
                try {
                    long result = adapter.insert(message);
                    SyncLog.write(NAME, "Insert result" + result);
                }
                catch (Exception e){ //TODO only catch SQL exceptions
                    //there's already an entry in the database
                    //let's query it and update it if needed
                    //TODO compare hashcodes? check if we are actually changed

                    //TODO check if we are already synced
                    boolean result = adapter.update(message);
                    SyncLog.write(NAME, "Update result" + result);
                }

            }
            SyncLog.write(NAME, "Last read id is" + lastReadID);
            if (lastReadID > 0) {
                StoreLastReadMessageID(lastReadID);
            }
            return true;
        }
        catch (Exception e){
            SyncLog.write(NAME,"ERROR:"+e.toString());
            return false;
        }
    }

    private void StoreLastReadMessageID(int id){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = settings.edit();
        edit.putInt("MELAY_LAST_READ_ID", id);
        edit.commit();

        //int id = settings.getInt("MELAY_LAST_READ_ID", 0);

        SyncLog.write(NAME,"Storing last read id  "+id);
    }

    /**
     * Gets the last read message id
     *
     * @return
     */
    public int GetLastReadMessageId() {
        //TODO get the last read message from shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int id = settings.getInt("MELAY_LAST_READ_ID", 0);
        SyncLog.write(NAME,"Reading last read id  "+id);
        return id;
    }
}
