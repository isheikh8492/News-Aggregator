package com.imaduddinsheikh.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.imaduddinsheikh.newsaggregator.databinding.ActivityMainBinding;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private ArrayList <NewsSource> currentNSourcesList;
    private ArrayList <NewsArticle> currentNArticlesList = new ArrayList<>();

    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private NewsArticleAdapter nArticlesAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ViewPager2 viewPager;
    private List<String> sourceDisplayed = new ArrayList<>();

    private final HashMap<String, String> nameToId = new HashMap<>();

    private HashMap<String, ArrayList<String>> sourceCategoriesToName
            = new HashMap<>();

    private static HashMap<String, Integer> currentCategoryColor = new HashMap<>();

    private static HashMap<String, String> nameToCategory = new HashMap<>();

    private List<String> oldSourceDisplayed;

    private ArrayList <NewsSource> oldNSourcesList;

    private int oldArticlePosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("News Gateway");

        if (currentNSourcesList == null || oldNSourcesList == null) {
            new Thread(new NewsSourcesLoaderRunnable(this)).start();
        }
        mDrawerLayout = binding.drawerLayout;
        mDrawerList = binding.drawerList;

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new CustomActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        nArticlesAdapter = new NewsArticleAdapter(this, currentNArticlesList);
        viewPager = binding.viewpager;
        viewPager.setAdapter(nArticlesAdapter);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        if (!item.getTitle().toString().equals("All")) {
            sourceDisplayed.clear();
            if (sourceCategoriesToName.get(item.getTitle().toString()) != null) {
                sourceDisplayed.addAll(Objects.requireNonNull(sourceCategoriesToName.get(item.getTitle().toString())));
            }
            arrayAdapter.notifyDataSetChanged();
            if (viewPager.getCurrentItem() == 0 && getTitle().toString().matches("News Gateway.*")) {
                changeTitle();
            }
        } else {
            sourceDisplayed.clear();
            for (NewsSource ns : currentNSourcesList) {
                sourceDisplayed.add(ns.getName());
            }
            arrayAdapter.notifyDataSetChanged();
            if (viewPager.getCurrentItem() == 0 && getTitle().toString().matches("News Gateway.*")) {
                changeTitle();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        opt_menu = menu;
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectItem(int position) {
        viewPager.setBackground(null);
        if (sourceDisplayed.isEmpty()) {
            sourceDisplayed = oldSourceDisplayed;
        }
        String selectedSource = sourceDisplayed.get(position);
        currentNArticlesList.clear();
        String selectedSourceId = nameToId.get(selectedSource);
        new Thread(new NewsArticlesLoaderRunnable(this, selectedSourceId, selectedSource)).start();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void downloadFailed() {
        Log.d(TAG, "downloadFailed: ");
    }

    public Runnable updateData(ArrayList<NewsArticle> naList, String sourceId, String sourceName) {
        currentNArticlesList.addAll(naList);
        nArticlesAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);

        if (currentNArticlesList.isEmpty()) {
            Toast.makeText(this,
                    MessageFormat.format("No articles found for {0}", sourceName),
                    Toast.LENGTH_LONG).show();
        }

        mDrawerLayout.closeDrawer(mDrawerList);
        changeTitle(sourceName);
        return null;
    }

    public Runnable updateNewsSourceData(ArrayList<NewsSource> newsSourceList,
                                         HashMap<String, ArrayList<String>> categoryToName,
                                         HashMap<String, Integer> categoryColor) {
        currentCategoryColor = categoryColor;
        currentNSourcesList = new ArrayList<>();
        for (NewsSource ns : newsSourceList) {
            sourceDisplayed.add(ns.getName());
            if (!nameToId.containsKey(ns.getName()))
                nameToId.put(ns.getName(), ns.getId());
            currentNSourcesList.add(ns);
            if (!nameToCategory.containsKey(ns.getName()))
                nameToCategory.put(ns.getName(), ns.getCategory());
        }

        if (oldSourceDisplayed != null) {
            sourceDisplayed = oldSourceDisplayed;
        }
        Collections.sort(sourceDisplayed);

        sourceCategoriesToName = categoryToName;
        if (opt_menu.size() == 0) {
            ArrayList<String> tempList = new ArrayList<>(sourceCategoriesToName.keySet());
            tempList.add(0, "All");
            Collections.sort(tempList);
            int itemId = 0;
            for (String c : tempList) {
                MenuItem menuItem = opt_menu.add(Menu.NONE, itemId, Menu.NONE, c);

                if (!Objects.equals(c, "All") && currentCategoryColor.containsKey(c)) {
                    SpannableString spannableString = new SpannableString(c);
                    ForegroundColorSpan textColorSpan = new ForegroundColorSpan(currentCategoryColor.get(c));
                    spannableString.setSpan(textColorSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    menuItem.setTitle(spannableString);
                }
                itemId++;
            }
        }
        changeTitle();

        arrayAdapter = new SourceDisplayedAdapter(this,
                sourceDisplayed, currentCategoryColor, nameToCategory);
        mDrawerList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        return null;
    }

    private void changeTitle(String... args) {
        if (args.length > 0) {
            setTitle(args[0]);
        } else {
            setTitle("News Gateway (" + sourceDisplayed.size() + ")");
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentNSourcesList", currentNSourcesList);
        outState.putStringArrayList("sourceDisplayed", new ArrayList<>(sourceDisplayed));
        outState.putInt("viewPagerPosition", viewPager.getCurrentItem());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        oldSourceDisplayed = savedInstanceState.getStringArrayList("sourceDisplayed");
        oldNSourcesList = (ArrayList<NewsSource>) savedInstanceState.getSerializable("currentNSourcesList");
        oldArticlePosition = savedInstanceState.getInt("viewPagerPosition");
    }
}