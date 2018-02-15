package com.melay.sync;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Created by matth on 2/15/2018.
 */

public class SyncLog {
    private static BufferedWriter out;
    private static BufferedWriter createFileOnDevice(Boolean append) throws IOException {
                /*
                 * Function to initially create the log file and it also writes the time of creation to file.
                 */
        File Root = Environment.getExternalStorageDirectory();
        if(Root.canWrite()){
            File LogFile = new File(Root, "SyncLog.txt");
            FileWriter LogWriter = new FileWriter(LogFile, append);
            out = new BufferedWriter(LogWriter);
            Date date = new Date();
            out.write("Logged at" + String.valueOf(date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds() + "\n"));
            return out;
        }
        return null;
    }
    public static void write(String TAG, String message) {
        try {
            BufferedWriter out = createFileOnDevice(true);
            out.write(message + "\n");
            Log.i(TAG, "ArchivingService started!");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void write(String message) {
        write("SYNCLOG",message);
    }
}
