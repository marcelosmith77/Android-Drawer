package com.marcelosmith77.android.easydrawer.fragment;

/**
 * Allows fragment to override back button behavior, let itself decide to continue or not.
 */
public interface IFragmentBackHandler {

    boolean onBackButtonPressed();
}
