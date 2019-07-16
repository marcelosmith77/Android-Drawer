package com.marcelosmith77.android.easydrawer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.marcelosmith77.android.easydrawer.activity.AbstractBaseDrawerActivity;
import com.marcelosmith77.android.easydrawer.utils.Utils;

public abstract class AbstractBaseDrawerFragment extends Fragment {

    /**
     * Shows fragment, adds it to the stack without clearing back stack
     *
     * @param fragment - The fragment
     */
    protected void showFrament(Fragment fragment) {

        showFrament(fragment, fragment.getClass().getSimpleName());
    }

    /**
     * Shows fragment, adds it to the stack without clearing back stack
     *
     * @param fragment - The fragment
     * @param name - The name for this back stack state
     */
    protected void showFrament(Fragment fragment, String name) {

        FragmentActivity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            ((AbstractBaseDrawerActivity) activity).showFrament(fragment, name, false, false);
        }
    }

    /**
     * Shows fragment, without clearing back stack
     *
     * @param fragment - The fragment
     * @param addToStack - Add to stack?
     */
    protected void showFrament(Fragment fragment, boolean addToStack) {
       showFrament(fragment, fragment.getClass().getSimpleName(), addToStack);
    }

    /**
     * Shows fragment
     *
     * @param fragment - The fragment
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     * @param key - parameter key
     * @param parcelable - fragment parameter
     */
    public void showFrament(Fragment fragment, boolean addToStack, boolean clearStack, String key, Parcelable parcelable) {

        FragmentActivity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            Bundle args = new Bundle();
            args.putParcelable(key, parcelable);

            ((AbstractBaseDrawerActivity) activity).showFrament(fragment, fragment.getClass().getSimpleName(), addToStack, clearStack, args);
        }
    }

    /**
     * Shows fragment
     *
     * @param fragment - The fragment
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     * @param args - bundle of arguments
     */
    public void showFrament(Fragment fragment, boolean addToStack, boolean clearStack, Bundle args) {

        FragmentActivity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            ((AbstractBaseDrawerActivity) activity).showFrament(fragment, fragment.getClass().getSimpleName(), addToStack, clearStack, args);
        }
    }

    /**
     * Shows fragment, without clearing back stack
     *
     * @param fragment - The fragment
     * @param name - The name for this back stack state
     * @param addToStack - Add to stack?
     */
    protected void showFrament(Fragment fragment, String name, boolean addToStack) {
        FragmentActivity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            ((AbstractBaseDrawerActivity) activity).showFrament(fragment, name, addToStack, false);
        }
    }

    protected void fireBottomNavigation(@IdRes int menuItemId) {
        fireBottomNavigation(menuItemId, null);
    }

    protected void fireBottomNavigation(@IdRes int menuItemId, Bundle args) {
        FragmentActivity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            ((AbstractBaseDrawerActivity) activity).fireBottomNavigation(menuItemId, null);
        }
    }

    /**
     * Simules back button
'    */
    protected void back() {

        FragmentActivity activity = getActivity();

        if (activity != null && !activity.isFinishing()) {
            activity.onBackPressed(); // see AbstractBaseDrawerActivity

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.hideKeyboard(getActivity());
                }
            });
        }
    }

    /**
     * Checks activity signature
     * @param context - The activity context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof AbstractBaseDrawerActivity)) {
            throw new RuntimeException(context.toString() + " must extends AbstractBaseDrawerActivity");
        }
    }
}



