package com.melay.sync.data;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.ArrayList;

/**
 * Created by matth on 2/5/2018.
 */

@com.raizlabs.android.dbflow.annotation.TypeConverter
public class StringArrayConverter extends TypeConverter<String, String[]> {

    @Override
    public String getDBValue(String[] model) {
        //concatendate all the data together
        return model[0];
    }

    @Override
    public String[] getModelValue(String data) {
        //get number of phone numbers in string
        int phoneNumbers = 3;
        String[] array = new String[phoneNumbers];
        array[0] = data;
        return array;
    }
}
