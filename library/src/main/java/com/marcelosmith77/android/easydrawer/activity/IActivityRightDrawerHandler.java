package com.marcelosmith77.android.easydrawer.activity;

import android.view.MenuItem;
import android.view.View;

public interface IActivityRightDrawerHandler {

    /**
     * Right drawer item selected event listener (END SIDE)
     *
     * @param item - The selected item
     */
    boolean onRightNavigationItemSelected(MenuItem item);

    /**
     * Right drawer open event listener (END SIDE)
     *
     * @param drawerView - The drawer that has been opened
     */
    void onRightDrawerOpened(View drawerView);

    /**
     * Right drawer close event listener (END SIDE)
     *
     * @param drawerView - The drawer that has been closed
     */
    void onRightDrawerClosed(View drawerView);
}
