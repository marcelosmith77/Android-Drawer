package com.marcelosmith77.android.easydrawer.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

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

        ((AbstractBaseDrawerActivity) getActivity()).showFrament(fragment, name, false, false);
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
     * Shows fragment, without clearing back stack
     *
     * @param fragment - The fragment
     * @param name - The name for this back stack state
     * @param addToStack - Add to stack?
     */
    protected void showFrament(Fragment fragment, String name, boolean addToStack) {
        ((AbstractBaseDrawerActivity) getActivity()).showFrament(fragment, name, addToStack, false);
    }

    /**
     * Simules back button
'    */
    protected void back() {

        if (getActivity() != null)
            getActivity().onBackPressed(); // see AbstractBaseDrawerActivity

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.hideKeyboard(getActivity());
            }
        });
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



