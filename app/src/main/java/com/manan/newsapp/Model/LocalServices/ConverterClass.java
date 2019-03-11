package com.manan.newsapp.Model.LocalServices;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.manan.newsapp.Model.DataModelClasses.Source;

import java.lang.reflect.Type;

public class ConverterClass {
    @TypeConverter
    public static Source fromString(String value) {
        Type listType = new TypeToken<Source>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    @TypeConverter
    public static String fromArrayLisr(Source source) {
        Gson gson = new Gson();
        String json = gson.toJson(source);
        return json;
    }
}
