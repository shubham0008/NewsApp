package com.manan.newsapp.Model.DataModelClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;

import com.google.gson.annotations.SerializedName;

@Dao
public class Source {
    @ColumnInfo(name = "id")
    @SerializedName("id")
    String id;

    @ColumnInfo(name = "name")
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

