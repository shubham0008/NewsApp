package com.manan.newsapp.Model.RemoteServices;

import com.manan.newsapp.Model.DataModelClasses.NewsResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {



    @GET("top-headlines")
    Call<NewsResponseModel> getHeadlines(@Query("country") String country,
                                         @Query("apiKey")String apiKey,
                                         @Query("q") String query,
                                         @Query("category") String category,
                                         @Query("pageSize") int pageSize);

}
