package com.marcelosmith77.android.easydrawer.activity;

import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

public class BaseDrawerParts {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView leftNavigationView;
    NavigationView[] rightNavigationViews;
    int fragmentContainerId;

    public BaseDrawerParts(Toolbar toolbar, DrawerLayout drawerLayout, NavigationView leftNavigationView, NavigationView[] rightNavigationViews, @IdRes int fragmentContainerId) {
        this.toolbar = toolbar;
        this.drawerLayout = drawerLayout;
        this.leftNavigationView = leftNavigationView;
        this.rightNavigationViews = rightNavigationViews;
        this.fragmentContainerId = fragmentContainerId;
    }
}
