package com.melay.sync.data;

import android.util.Log;

import com.melay.sync.local.LocalDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by matth on 1/28/2018.
 *
 * The base class for a message that is stored locally
 */


@Table(database = LocalDatabase.class)
public class MelayMessage extends Encryptable implements Syncable {
    @PrimaryKey
    int id; //the message ID
    @Column
    int threadId; //the thread that it's a part of
    @Column
    String from; //where the message is to- if we sent this, it is blank
    @Column(typeConverter=StringArrayConverter.class)
    String[] to; //if it's to anyone else (numerical order) our phone number is never in this
    @Column
    String body; //the actual message
    @Column
    Date sent; //when it was sent

    //not encrypted
    @Column
    boolean unsent = false;
    @Column
    boolean read = false; //if the user has read it
    @Column
    boolean incoming = false; //if the user sent it

    @Column
    DataStatus status = DataStatus.ON_DEVICE; //this should not be encrypted


    //----------------------

    @Override
    public void Encrypt(String key) {
        //encrypt the various methods on the data
        from = this.EncryptString(from, key);
        body = this.EncryptString(body, key);
        to = this.EncryptString(to, key);
        encrypted = true;
    }

    @Override
    public String toString() {
        return "MelayMessage{" +
                "id=" + id +
                ", threadId=" + threadId +
                ", from='" + from + '\'' +
                ", to=" + Arrays.toString(to) +
                ", encrypted=" + encrypted +
                ", body='" + body + '\'' +
                ", sent=" + sent +
                ", read=" + read +
                ", incoming=" + incoming +
                ", status=" + status +
                ", IV="+iv+
                '}';
    }

    @Override
    public boolean Decrypt(String key) {
        encrypted = false;
        return false;
    }


    static String TAG = MelayMessage.class.getCanonicalName();

    //Default constructor, do not use
    public MelayMessage(){
        //set everything to zero
        this(0,0,"NONE","NONE",new Date(),false,false);
    }

    public MelayMessage(int anInt, int deviceID, String from, String body, Date date, int seenInt, boolean incoming) {
        this(anInt, deviceID, from, body, date, true, incoming);
        read = (seenInt == 1);
    }

    //creates a new message
    public MelayMessage(int anInt, int deviceID, String from, String body, Date date, boolean incoming) {
        this(anInt, deviceID, from, body, date, true, incoming);
    }

    //creates a new message
    public MelayMessage(int anInt, int deviceID, String from, String body, Date date, boolean read, boolean incoming) {
        id = deviceID;
        this.from = from;
        this.body = body;
        Log.i(TAG, "Created a new message from" + from + " at index " + deviceID + " string1: " + body + " : " + anInt);
        sent = date;
        this.incoming = incoming;
    }

    public void MarkRead(boolean read) {
        this.read = read;
    }


    @Override
    public void UpdateState(DataStatus newState) {
        status = newState;
    }

    @Override
    public DataStatus GetState() {
        return status;
    }

    @Override
    public boolean isDataEqual(Object o) {
        MelayMessage other = (MelayMessage) o;
        if (other == null) return false;
        if (other.getId() != id) return false;
        if (other.isRead() != read) return false;
        if (other.sent != sent) return false;

        //don't compare body since that's possibly encrypted
        return true;
    }


    //GETTERS AND SETTERS

    public boolean isRead() {
        return read;
    }

    public int getId() {
        return id;
    }
}
