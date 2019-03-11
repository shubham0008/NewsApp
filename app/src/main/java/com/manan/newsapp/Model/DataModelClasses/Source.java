package com.manan.newsapp.Model.DataModelClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;


public class Source {
    @Embedded
    @SerializedName("id")
    String id;


    @Embedded
    @SerializedName("name")
    String name;

    public Source(String id, String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

