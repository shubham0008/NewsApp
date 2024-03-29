package com.manan.newsapp.MainFeedScreen;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manan.newsapp.Model.DataModelClasses.ArticleData;
import com.manan.newsapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArticlesTileAdapter extends RecyclerView.Adapter<ArticlesTileAdapter.MyViewHolder> {

    private List<ArticleData> articleDataArrayList;
    private Context context;

    public ArticlesTileAdapter(List<ArticleData> articleDataArrayList, Context context) {
        this.articleDataArrayList = articleDataArrayList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvCat, tvDesc, tvSource, tvDate;
        public ImageView ivBack;

        public MyViewHolder(@NonNull View itemView) {
            super( itemView );
            tvTitle = itemView.findViewById(R.id.art_title);
            tvCat = itemView.findViewById(R.id.art_cat);
            tvDesc = itemView.findViewById(R.id.art_desc);
            tvSource = itemView.findViewById(R.id.art_source);
            tvDate = itemView.findViewById(R.id.art_date );
            ivBack = itemView.findViewById(R.id.art_background );
        }
    }

    @NonNull
    @Override
    public ArticlesTileAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemView = LayoutInflater.from( viewGroup.getContext() )
                .inflate( R.layout.article_item, viewGroup, false );

        return new MyViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesTileAdapter.MyViewHolder myViewHolder, int i) {
         myViewHolder.tvTitle.setText(articleDataArrayList.get(i).getTitle()+"");
         myViewHolder.tvCat.setText(articleDataArrayList.get(i).getSource().getName()+"");
         myViewHolder.tvDesc.setText(articleDataArrayList.get(i).getTitle()+"");
         myViewHolder.tvSource.setText(articleDataArrayList.get(i).getAuthor()+"");
         myViewHolder.tvDate.setText(articleDataArrayList.get(i).getPublishedAt().substring( 0,10 )+"");
        Picasso.get().load( articleDataArrayList.get( i ).getUrlToImage() ).into( myViewHolder.ivBack );
       //  myViewHolder.ivBack.setText(articleDataArrayList.get(i).getUrlToImag+""e());


    }

    @Override
    public int getItemCount() {
        return articleDataArrayList.size();
    }

    public List<ArticleData> getArticleDataArrayList()
    {
        return  articleDataArrayList;
    }


}
