package com.manan.newsapp.Model.LocalServices;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.manan.newsapp.Model.DataModelClasses.ArticleData;

import java.util.List;

@Dao
public interface ArticleDao {

    @Query("SELECT * FROM tb_articles")
    List<ArticleData> getAll();


    @Query("SELECT COUNT(*) from tb_articles")
    int countUsers();

    @Query("DELETE FROM tb_articles")
    void deleteAll();

    @Insert
    void insertAll(ArticleData... articles);

    @Delete
    void delete(ArticleData articles);
}
