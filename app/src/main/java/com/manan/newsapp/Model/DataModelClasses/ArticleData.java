package com.manan.newsapp.Model.DataModelClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "tb_articles")
public class ArticleData {

    @PrimaryKey(autoGenerate = true)
    private int aid;


    @Embedded
    @SerializedName("source")
    Source source;

    @ColumnInfo(name = "author")
    @SerializedName("author")
    String author;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    String title;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    String description;

    @ColumnInfo(name = "url")
    @SerializedName("url")
    String url;

    @ColumnInfo(name = "urlToImage")
    @SerializedName("urlToImage")
    String urlToImage;

    @ColumnInfo(name = "publishedAt")
    @SerializedName("publishedAt")
    String publishedAt;

    @ColumnInfo(name = "content")
    @SerializedName("content")
    String content;


    public ArticleData() {
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public ArticleData(int aid, Source source, String author, String title, String description, String url, String urlToImage, String publishedAt, String content) {
        this.aid = aid;
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.content = content;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

