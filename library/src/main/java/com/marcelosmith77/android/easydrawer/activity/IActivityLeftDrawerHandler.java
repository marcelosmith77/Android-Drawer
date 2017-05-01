package com.marcelosmith77.android.easydrawer.activity;

import android.view.MenuItem;
import android.view.View;

public interface IActivityLeftDrawerHandler {

    /**
     * Left drawer item selected event listener (START SIDE)
     * @param item - The selected item
     */
    boolean onLeftNavigationItemSelected(MenuItem item);

    /**
     * Left drawer open event listener (START SIDE)
     *
     * @param drawerView - The drawer that has been opened
     */
    void onLeftDrawerOpened(View drawerView);

    /**
     * Left drawer open event listener (START SIDE)
     *
     * @param drawerView - The drawer that has been closed
     */
    void onLeftDrawerClosed(View drawerView);
}
