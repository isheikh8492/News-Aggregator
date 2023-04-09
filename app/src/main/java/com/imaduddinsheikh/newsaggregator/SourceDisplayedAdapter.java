package com.imaduddinsheikh.newsaggregator;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.imaduddinsheikh.newsaggregator.databinding.DrawerItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SourceDisplayedAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> sourceDisplayed;
    private final HashMap<String, Integer> categoryColor;
    private final HashMap<String, String> nameToCategory;

    public SourceDisplayedAdapter(Context context, List<String> sourceDisplayed,
                                  HashMap<String, Integer> categoryColor,
                                  HashMap<String, String> nameToCategory) {
        super(context, R.layout.drawer_item, sourceDisplayed);
        this.context = context;
        this.sourceDisplayed = sourceDisplayed;
        this.categoryColor = categoryColor;
        this.nameToCategory = nameToCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerItemBinding binding;
        if (convertView == null) {
            binding = DrawerItemBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (DrawerItemBinding) convertView.getTag();
        }

        String sourceName = sourceDisplayed.get(position);
        String category = nameToCategory.get(sourceName);
        int color = categoryColor.get(category);

        binding.drawerListElementTxtView.setText(sourceName);
        binding.drawerListElementTxtView.setTextColor(color);

        return convertView;
    }
}