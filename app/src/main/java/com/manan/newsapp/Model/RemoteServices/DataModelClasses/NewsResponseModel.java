package com.manan.newsapp.Model.RemoteServices.DataModelClasses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NewsResponseModel {

          @SerializedName("status")
          String status;
          @SerializedName("totalResults")
          String totalResults;
          @SerializedName("articles")
          ArrayList<ArticleData> articles;
}
