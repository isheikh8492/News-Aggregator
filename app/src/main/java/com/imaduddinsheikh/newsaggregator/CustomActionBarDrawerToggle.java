package com.imaduddinsheikh.newsaggregator;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;

public class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {

    public CustomActionBarDrawerToggle(MainActivity activity, DrawerLayout drawerLayout, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
        super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
    }

    @Override
    public void setDrawerIndicatorEnabled(boolean enable) {
        super.setDrawerIndicatorEnabled(true);
    }
}
