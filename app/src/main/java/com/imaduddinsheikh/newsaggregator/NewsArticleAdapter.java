package com.imaduddinsheikh.newsaggregator;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.imaduddinsheikh.newsaggregator.databinding.NewsArticleEntryBinding;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        try {
            holder.naBinding.naDateTxtView.setVisibility(View.VISIBLE);
            holder.naBinding.naDateTxtView.setText(convertDateTime(na.getPublishDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.naBinding.naDateTxtView.setVisibility(View.GONE);
        }
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

        if (!na.getUrl().equals("null")) {
            holder.naBinding.naTitleTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickNAWebsite(v, na.getUrl());
                }
            });
            holder.naBinding.naImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickNAWebsite(v, na.getUrl());
                }
            });
            holder.naBinding.naDescriptionTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickNAWebsite(v, na.getUrl());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return newsArticlesList.size();
    }

    private static String convertDateTime(String input) throws ParseException {
        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        inputFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = inputFormatter.parse(input);

        // Get the default timezone set on the device
        TimeZone deviceTimeZone = TimeZone.getDefault();

        // Set the output format and timezone
        SimpleDateFormat outputFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        outputFormatter.setTimeZone(deviceTimeZone);

        return outputFormatter.format(date);
    }

    private void clickNAWebsite(View v, String website) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(website));
        if (intent.resolveActivity(mainActivity.getPackageManager()) != null) {
            mainActivity.startActivity(intent);
        }
    }
}
