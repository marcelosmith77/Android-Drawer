package com.marcelosmith77.android.easydrawer.activity;

import android.os.Bundle;
import android.view.MenuItem;

public interface IActivityBottomNavigationHandler {

    /**
     * Bottom navigation item selected event listener
     *
     * @param item - The selected item
     * @return True - if item were selected, otherwise returns false.
     */
    boolean onBottomNavigationItemSelected(MenuItem item);

    /**
     * Bottom navigation item RE-selected event listener
     *
     * @param item - The selected item
     */
    void onBottomNavigationItemReselected(MenuItem item);

    /**
     * Bottom navigation item selected event listener
     *
     * @param item - The selected item
     * @return True - if item were selected, otherwise returns false.
     */
    boolean onBottomNavigationItemSelected(MenuItem item, Bundle args);


    /**
     * Bottom navigation item RE-selected event listener
     *
     * @param item - The selected item
     */
    void onBottomNavigationItemReselected(MenuItem item, Bundle args);

}
