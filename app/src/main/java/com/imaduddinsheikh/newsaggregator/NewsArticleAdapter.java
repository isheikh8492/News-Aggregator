package com.imaduddinsheikh.newsaggregator;

import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
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

        if (na.getAuthor().equals("null")) {
            holder.naBinding.naAuthorTxtView.setVisibility(View.GONE);
        } else {
            holder.naBinding.naAuthorTxtView.setVisibility(View.VISIBLE);
            holder.naBinding.naAuthorTxtView.setText(na.getAuthor());
        }
        holder.naBinding.naDateTxtView.setText(na.getPublishDate());
        if (na.getDescription().equals("null")) {
            holder.naBinding.naDescriptionTxtView.setVisibility(View.GONE);
        } else {
            holder.naBinding.naDescriptionTxtView.setVisibility(View.VISIBLE);
            holder.naBinding.naDescriptionTxtView.setText(na.getDescription());
            descriptionFont = Typeface.createFromAsset(mainActivity.getAssets(),
                    "fonts/Source_Serif_Pro/SourceSerifPro-Regular.ttf");
            holder.naBinding.naDescriptionTxtView.setTypeface(descriptionFont);
        }

        if (na.getImageUrl() != null) {
            Picasso.get().load(na.getImageUrl()).error(R.drawable.brokenimage).into(holder.naBinding.naImgView);
        } else {
            holder.naBinding.naImgView.setImageResource(R.drawable.noimage);
        }

        String current = String.valueOf(position + 1);
        String total = String.valueOf(getItemCount());
        String pageNumber = current + " of " + total;

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(pageNumber);
        StyleSpan boldSpan1 = new StyleSpan(Typeface.BOLD);
        spannableStringBuilder.setSpan(boldSpan1, 0, current.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        StyleSpan boldSpan2 = new StyleSpan(Typeface.BOLD);
        spannableStringBuilder.setSpan(boldSpan2, pageNumber.length() - total.length(), pageNumber.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        holder.naBinding.naPageNumber.setText(spannableStringBuilder);
    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }
}
