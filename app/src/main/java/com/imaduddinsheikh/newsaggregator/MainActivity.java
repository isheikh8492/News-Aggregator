package com.imaduddinsheikh.newsaggregator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.imaduddinsheikh.newsaggregator.databinding.ActivityMainBinding;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    private ArrayList <NewsSource> currentNSourcesList;
    private ArrayList <NewsArticle> currentNArticlesList =  new ArrayList<>();

    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private NewsArticleAdapter nArticlesAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ViewPager2 viewPager;

    private final ArrayList<String> sourceDisplayed = new ArrayList<>();

    private final HashMap<String, ArrayList<NewsArticle>> sourceToArticles = new HashMap<>();

    private final HashMap<String, String> nameToId = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("News Gateway");

        if (currentNSourcesList == null) {
            new Thread(new NewsSourcesLoaderRunnable(this)).start();
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        mDrawerList.setOnItemClickListener(
                (parent, view, position, id) -> {
                    selectItem(position);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
        );

        // Create the drawer toggle
        mDrawerToggle = new ActionBarDrawerToggle(
                this,            /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        nArticlesAdapter = new NewsArticleAdapter(this, currentNArticlesList);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(nArticlesAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectItem(int position) {
        viewPager.setBackground(null);
        String selectedSource = sourceDisplayed.get(position);
        currentNArticlesList.clear();
        String selectedSourceId = nameToId.get(selectedSource);
        ArrayList<NewsArticle> articles = sourceToArticles.get(selectedSourceId);
        if (articles == null) {
            Toast.makeText(this,
                    MessageFormat.format("No articles found for {0}", selectedSource),
                    Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(new NewsArticlesLoaderRunnable(this)).start();

        currentNArticlesList.addAll(articles);
        nArticlesAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);

        mDrawerLayout.closeDrawer(mDrawerList);

        setTitle(selectedSource + " (" + articles.size() + ")");

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void downloadFailed() {
        Log.d(TAG, "downloadFailed: ");
    }

//    public Runnable updateData(ArrayList<NewsSource> nsList) {
//        for (NewsSource ns : nsList) {
//            tempList.add(ns.getName());
//        }
//        Collections.sort(tempList);
//
//        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, tempList);
//        mDrawerList.setAdapter(arrayAdapter);
//        arrayAdapter.notifyDataSetChanged();
//        setTitle(getTitle() + " (" + arrayAdapter.getCount() + ")");
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//        }
//        return null;
//    }

    public Runnable updateData(ArrayList<NewsArticle> naList) {
        currentNArticlesList.addAll(naList);
        for (NewsArticle na : naList) {
            if (!sourceToArticles.containsKey(na.getSource()[0]))
                sourceToArticles.put(na.getSource()[0], new ArrayList<>());
            Objects.requireNonNull(sourceToArticles.get(na.getSource()[0])).add(na);

        }
        return null;
    }

    public Runnable updateNewsSourceData(ArrayList<NewsSource> newsSourceList) {
        Log.d(TAG, "updateNewsSourceData: " + newsSourceList.toString());
        currentNSourcesList = new ArrayList<>();
        for (NewsSource ns : newsSourceList) {
            sourceDisplayed.add(ns.getName());
            if (!nameToId.containsKey(ns.getName()))
                nameToId.put(ns.getName(), ns.getId());
        }
        Collections.sort(sourceDisplayed);

        arrayAdapter = new ArrayAdapter<>(this, R.layout.drawer_item, sourceDisplayed);
        mDrawerList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        return null;
    }
}