package com.marcelosmith77.android.easydrawer.activity;

import com.marcelosmith77.android.easydrawer.fragment.IHomeFragment;

/**
 * Handles back stack
 *
 * Does give you an ability to tell what fragment will be shown where there is no fragments on the stack (Home)
 */
public interface IActivityBackStackHandler {


    /**
     * Home fragment class type
     * Which one you wanna use for the first shown fragment
     * @return The home fragment class type
     */
    Class<? extends IHomeFragment>  getHomeFragmentClass();

    /**
     * Allows you to define the behavior when the app is going to close / finish
     */
    void exitApp();
}
