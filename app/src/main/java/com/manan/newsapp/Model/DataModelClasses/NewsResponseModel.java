package com.manan.newsapp.Model.DataModelClasses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsResponseModel {

    @SerializedName("status")
    String status;
    @SerializedName("totalResults")
    String totalResults;
    @SerializedName("articles")
    ArrayList<ArticleData> articles;

    public NewsResponseModel(String status, String totalResults, ArrayList<ArticleData> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public ArrayList<ArticleData> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<ArticleData> articles) {
        this.articles = articles;
    }
}
