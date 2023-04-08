package com.imaduddinsheikh.newsaggregator;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.imaduddinsheikh.newsaggregator.databinding.NewsArticleEntryBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleViewHolder> {
    private final MainActivity mainActivity;
    private ArrayList<NewsArticle> newsArticlesList;

    private Typeface titleFont;

    private Typeface descriptionFont;

    public NewsArticleAdapter(MainActivity mainActivity, ArrayList<NewsArticle> newsArticlesList) {
        this.mainActivity = mainActivity;
        this.newsArticlesList = newsArticlesList;
    }

    @NonNull
    @Override
    public NewsArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout and create the binding instance
        NewsArticleEntryBinding binding = NewsArticleEntryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NewsArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsArticleViewHolder holder, int position) {
        NewsArticle na = newsArticlesList.get(position);

        titleFont = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Source_Serif_Pro/SourceSerifPro-Bold.ttf");
        holder.naBinding.naTitleTxtView.setText(na.getTitle());
        holder.naBinding.naTitleTxtView.setTypeface(titleFont);

        holder.naBinding.naAuthorTxtView.setText(na.getAuthor());
        holder.naBinding.naDateTxtView.setText(na.getPublishDate());
        descriptionFont = Typeface.createFromAsset(mainActivity.getAssets(), "fonts/Source_Serif_Pro/SourceSerifPro-Regular.ttf");
        holder.naBinding.naDescriptionTxtView.setText(na.getDescription());
        holder.naBinding.naDescriptionTxtView.setTypeface(descriptionFont);

        if (na.getImageUrl() != null) {
            Picasso.get().load(na.getImageUrl()).error(R.drawable.brokenimage).into(holder.naBinding.naImgView);
        } else {
            holder.naBinding.naImgView.setImageResource(R.drawable.noimage);
        }
    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }
}
