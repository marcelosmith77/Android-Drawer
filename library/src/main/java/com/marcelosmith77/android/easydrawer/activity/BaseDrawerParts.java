package com.marcelosmith77.android.easydrawer.activity;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

public class BaseDrawerParts {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView leftNavigationView;
    NavigationView[] rightNavigationViews;
    int fragmentContainerId;
    int openDrawerContentDescRes;
    int closeDrawerContentDescRes;
    int touchAgaingDescRes;
    BottomNavigationView bottomNavigationView;

    public BaseDrawerParts(Toolbar toolbar, DrawerLayout drawerLayout, NavigationView leftNavigationView, NavigationView[] rightNavigationViews, @IdRes int fragmentContainerId, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes, @StringRes int touchAgaingDescRes) {
        this(toolbar, drawerLayout, leftNavigationView, rightNavigationViews, fragmentContainerId, openDrawerContentDescRes, closeDrawerContentDescRes, touchAgaingDescRes, null);
    }
    public BaseDrawerParts(Toolbar toolbar, DrawerLayout drawerLayout, NavigationView leftNavigationView, NavigationView[] rightNavigationViews, @IdRes int fragmentContainerId, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes, @StringRes int touchAgaingDescRes, BottomNavigationView bottomNavigationView) {
        this.toolbar = toolbar;
        this.drawerLayout = drawerLayout;
        this.leftNavigationView = leftNavigationView;
        this.rightNavigationViews = rightNavigationViews;
        this.fragmentContainerId = fragmentContainerId;
        this.openDrawerContentDescRes = openDrawerContentDescRes;
        this.closeDrawerContentDescRes = closeDrawerContentDescRes;
        this.touchAgaingDescRes = touchAgaingDescRes;
        this.bottomNavigationView = bottomNavigationView;
    }
}
