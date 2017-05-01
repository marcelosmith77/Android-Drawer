package com.marcelosmith77.android.easydrawer.fragment;

import android.view.View;

public interface IFragmentRightDrawerHandler {

    /**
     * Right drawer open event listener (START SIDE)
     *
     * @param drawerView - The drawer that has been opened
     */
    void onRightDrawerOpened(View drawerView);

    /**
     * Right drawer close event listener (START SIDE)
     *
     * @param drawerView - The drawer that has been closed
     */
    void onRightDrawerClosed(View drawerView);
}
