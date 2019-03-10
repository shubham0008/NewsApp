package com.manan.newsapp.Model.LocalServices;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.manan.newsapp.Model.DataModelClasses.ArticleData;

import java.util.List;

public class DatabaseInitializer
{
    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db,List<ArticleData> data) {
        PopulateDbAsync task = new PopulateDbAsync(db,data);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db,List<ArticleData> data) {
        populateWithData(db,data);
    }

    private static ArticleData addUser(final AppDatabase db, ArticleData articleDataItem) {
        db.articleDao().insertAll(articleDataItem);
        return articleDataItem;
    }

    private static void populateWithData(AppDatabase db, List<ArticleData> data) {

        db.articleDao().deleteAll();
        for(int i=0;i< data.size();i++) {
            addUser( db, data.get( i ) );
        }

        List<ArticleData> artList = db.articleDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + artList.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private final List<ArticleData> mData;

        PopulateDbAsync(AppDatabase db, List<ArticleData> data) {
            mDb = db;
            this.mData = data;

        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithData( mDb, mData );
            return null;

        }    }

}
