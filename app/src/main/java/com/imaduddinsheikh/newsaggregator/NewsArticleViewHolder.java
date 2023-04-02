package com.imaduddinsheikh.newsaggregator;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.imaduddinsheikh.newsaggregator.databinding.NewsArticleEntryBinding;

import org.w3c.dom.Text;

public class NewsArticleViewHolder extends RecyclerView.ViewHolder {

    TextView title;

    TextView publishDate;

    TextView author;
    TextView description;

    NewsArticleEntryBinding naBinding;

    public NewsArticleViewHolder(@NonNull NewsArticleEntryBinding binding) {
        super(binding.getRoot());
        naBinding = binding;


        title = naBinding.naTitleTxtView;
        publishDate = naBinding.naDateTxtView;
        author = naBinding.naAuthorTxtView;
        description = naBinding.naDescriptionTxtView;
    }
}
