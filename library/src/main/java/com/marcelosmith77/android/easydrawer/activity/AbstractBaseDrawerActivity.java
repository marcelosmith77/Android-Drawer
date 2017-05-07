package com.marcelosmith77.android.easydrawer.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.marcelosmith77.android.easydrawer.fragment.IFragmentBackHandler;
import com.marcelosmith77.android.easydrawer.fragment.IFragmentRightDrawerHandler;
import com.marcelosmith77.android.easydrawer.fragment.IHomeFragment;

/**
 * The class controls the drawer behavior.
 */
public abstract class AbstractBaseDrawerActivity extends AppCompatActivity implements IActivityBackStackHandler, IActivityLeftDrawerHandler, IActivityRightDrawerHandler {

    // Controls when user did touch once and ask him to touch again to exit from the app
    private boolean doubleBackToExitPressedOnce = false;

    private boolean drawerInitialized = false;

    // Left drawer that is in use on your main activity
    private DrawerLayout drawerLayout;

    // Container id, where fragments are replaced
    private int fragmentContainerId;

    @Override
    protected void onResume() {
        super.onResume();

        if (!drawerInitialized) {
            try {

                BaseDrawerParts parts = getBaseDrawerParts();
                setupUI(parts.toolbar, parts.drawerLayout, parts.leftNavigationView, parts.rightNavigationViews, parts.fragmentContainerId, parts.openDrawerContentDescRes, parts.closeDrawerContentDescRes);

            } finally {
                drawerInitialized = true;

                showHomeFragment();
            }
        }
    }

    /**
     * Show home fragment
     */
    private void showHomeFragment() {

        Fragment f;

        try {
            f = (Fragment) getHomeFragmentClass().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Fragment HOME cannot be instantiated. Make sure that extends android.support.v4.app.Fragment");
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Fragment HOME cannot be instantiated. Make sure that have an empty constructor.");
        }

        if (f != null)
            showFrament(f);

    }

    /**
     * Get Drawer parts to setup an events and listeners related to the drawer
     * @return BaseDrawerParts
     */
    protected abstract BaseDrawerParts getBaseDrawerParts();

    /**
     * Setup drawer events, like listeners.
     * @param toolbar - App toolbar
     * @param drawerLayout - Left drawer
     * @param leftNavigationView - Left navigation view (like main activity)
     * @param rightNavigationViews - Right navigation views (Used by fragment or activities)
     * @param fragmentContainerId - main container id.
     * @param openDrawerContentDescRes  A String resource to describe the "open drawer" action
     *                                  for accessibility
     * @param closeDrawerContentDescRes A String resource to describe the "close drawer" action
     *                                  for accessibility

     */
    private void setupUI(@NonNull Toolbar toolbar, @NonNull final DrawerLayout drawerLayout, final NavigationView leftNavigationView, NavigationView[] rightNavigationViews, @IdRes int fragmentContainerId, @StringRes int openDrawerContentDescRes, @StringRes int closeDrawerContentDescRes) {

        setSupportActionBar(toolbar);

        this.drawerLayout = drawerLayout;
        this.fragmentContainerId = fragmentContainerId;

        // Setup Navigation item listener (START SIDE)
        if (leftNavigationView != null) {
            leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    boolean result = onLeftNavigationItemSelected(item);

                    // item selected, close drawer
                    if (result) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }

                    return result; // item selected?
                }
            });
        }

        // Setup Navigation item listener (END SIDE)
        if (rightNavigationViews != null) {
            for (NavigationView rightView : rightNavigationViews) {
                rightView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        //Tells that right navigation item was selected
                        return onRightNavigationItemSelected(item);
                    }
                });
            }
        }


        if (drawerLayout != null) {

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, openDrawerContentDescRes, closeDrawerContentDescRes) {
                @Override
                public void onDrawerOpened(View drawerView) {

                    if (drawerView.getId() == leftNavigationView.getId()) {
                        onLeftDrawerOpened(drawerView);
                    } else {
                        onRightDrawerOpened(drawerView);
                    }
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (drawerView.getId() == leftNavigationView.getId()) {
                        onLeftDrawerClosed(drawerView);
                    } else {
                        onRightDrawerClosed(drawerView);
                    }
                }
            };

            drawerLayout.addDrawerListener(toggle);

            // Locks END side, that will be enabled by fragments that uses right drawers
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

            toggle.syncState();
        }
    }

    /**
     * Right drawer open event listener (END SIDE)
     *
     * if current fragment is an instance of <b>IFragmentRightDrawerHandler</b> tells it that the draw has been opened
     * @param drawerView - The drawer that has been opened
     */
    @Override
    public void onRightDrawerOpened(View drawerView) {
        Fragment f = getCurrentFragment();

        if (f instanceof IFragmentRightDrawerHandler) {
            ((IFragmentRightDrawerHandler) f).onRightDrawerOpened(drawerView);
        }
    }

    /**
     * Right drawer close event listener (END SIDE)
     *
     * if current fragment is an instance of <b>IFragmentRightDrawerHandler</b> tells it that the draw has been closed
     * @param drawerView - The drawer that has been closed
     */
    @Override
    public void onRightDrawerClosed(View drawerView) {
        Fragment f = getCurrentFragment();

        if (f instanceof IFragmentRightDrawerHandler) {
            ((IFragmentRightDrawerHandler) f).onRightDrawerClosed(drawerView);
        }
    }


    /**
     * Left drawer open event listener (START SIDE)
     *
     * @param drawerView - The drawer that has been opened
     */
    @Override
    public void onLeftDrawerOpened(View drawerView) {

    }

    /**
     * Left drawer close event listener (START SIDE)
     *
     * @param drawerView - The drawer that has been closed
     */
    @Override
    public void onLeftDrawerClosed(View drawerView) {

    }

    /**
     *  Handles back button
     */
    @Override
    public void onBackPressed() {

        // drawer opened ? close it!
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

            // drawer opened ? close it!
        } else if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {

            // Current fragment has custom back handler ?
            FragmentManager fm = getSupportFragmentManager();
            Fragment currentFragment = getCurrentFragment();

            // Let current fragment decide itself to continue or not
            if (currentFragment instanceof IFragmentBackHandler) {
                if (!((IFragmentBackHandler) currentFragment).onBackButtonPressed()) {
                    return;
                }
            }

            // there is fragments on the stack?
            int backStackCount = fm.getBackStackEntryCount();
            if (backStackCount > 0) {

                // Go ahead and call android native back action, that will pop the stack
                try {
                    super.onBackPressed();
                } catch (IllegalStateException e) {
                    supportFinishAfterTransition();
                    return;
                }

                getSupportFragmentManager().executePendingTransactions();

                backStackCount = getSupportFragmentManager().getBackStackEntryCount();

                // there is fragments on the stack?
                if (backStackCount == 0) {
                    showHomeFragment(); // Stack is empty, show home fragment
                }

                // Current fragment does have drawer? Unlock It, allowing END SIDE
                if (getCurrentFragment() instanceof IFragmentRightDrawerHandler) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                } else {
                    // Lock It, blocking END SIDE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);
                }

            } else {

                // User did touch again (twice)
                if (doubleBackToExitPressedOnce) {

                    // Performs exit
                    exitApp();
                    return;
                }

                // We are in home fragment, the user touched back button once, we show an message to tell him to touch again if he wants to exit.
                // Give him 2 seconds to touch again, otherwise resets the flag.

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getBaseDrawerParts().touchAgaingDescRes, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    /**
     * Left drawer item selected event listener (START SIDE)
     *
     * @param item - The selected item
     */
    @Override
    public boolean onLeftNavigationItemSelected(MenuItem item) {
        return true;
    }

    /**
     * Right drawer item selected event listener (END SIDE)
     * if current fragment is an instance of <b>IFragmentRightDrawerHandler</b> tells it that the item has been selected
     *
     * @param item - The selected item
     */
    @Override
    public boolean onRightNavigationItemSelected(MenuItem item) {

        Fragment f = getCurrentFragment();

        if (f instanceof IFragmentRightDrawerHandler) {
            return f.onOptionsItemSelected(item);
        }

        return true;
    }


    /**
     * Shows fragment, clearing all stack before adding it to the stack.
     *
     * @param f - The fragment
     */
    public void showFrament(Fragment f) {
        showFrament(f, f.getClass().getSimpleName());
    }

    /**
     * Shows fragment, clearing all stack before adding it to the stack.
     *
     * @param f - The fragment
     * @param name - The name for this back stack state
     */
    public void showFrament(Fragment f, String name) {
        showFrament(f, name, true, true);
    }

    /**
     * Shows fragment
     *
     * @param f - The fragment
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     */
    public void showFrament(Fragment f, boolean addToStack, boolean clearStack) {
        showFrament(f, f.getClass().getSimpleName(), addToStack, clearStack);
    }

    /**
     * Shows fragment
     *
     * @param f - The fragment
     * @param name - The name for this back stack state
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     */
    public void showFrament(Fragment f, String name, boolean addToStack, boolean clearStack) {
        showFrament(f, f.getClass().getSimpleName(), addToStack, clearStack, null);
    }

    /**
     * Shows fragment
     *
     * @param f - The fragment
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     * @param args - fragment parameters
     */
    public void showFrament(Fragment f, boolean addToStack, boolean clearStack, Bundle args) {
        showFrament(f, f.getClass().getSimpleName(), addToStack, clearStack, args);
    }

    /**
     * Shows fragment
     *
     * @param f - The fragment
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     * @param key - parameter key
     * @param parcelable - fragment parameter
     */
    public void showFrament(Fragment f, boolean addToStack, boolean clearStack, String key, Parcelable parcelable) {
        Bundle args = new Bundle();
        args.putParcelable(key, parcelable);

        showFrament(f, f.getClass().getSimpleName(), addToStack, clearStack, args);
    }

    /**
     * Shows fragment
     *
     * @param f - The fragment
     * @param name - The name for this back stack state
     * @param addToStack - Add to stack?
     * @param clearStack - Clear stack?
     * @param args - fragment parameters
     */
    public void showFrament(Fragment f, String name, boolean addToStack, boolean clearStack, Bundle args) {

        if (f == null)
            throw new RuntimeException("Fragment cannot be null!");

        try {

            boolean isHomeFragment =  f instanceof IHomeFragment;

            // Home fragment, must clear back stack
            if (isHomeFragment || clearStack) {
                clearBackStackFragments();
            }

            if (clearStack) {
                clearBackStackFragments();
            }

            if (addToStack && clearStack) {
                removeCurrentFragment();
            }

            if(args != null) { f.setArguments(args);}

            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(fragmentContainerId, f);

            // Home fragment must not pushed to stack
            if (!isHomeFragment && addToStack)
                ft.addToBackStack(name);

            ft.commit();

            fragmentManager.executePendingTransactions();
        } catch (IllegalStateException e) {
            supportFinishAfterTransition();
            return;
        }
    }

    /**
     * Clear back stack
     */
    private void clearBackStackFragments() {

        try {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) {
            supportFinishAfterTransition();
            return;
        }
    }

    /**
     * Get current fragment
     * @return Fragment - Current fragment found
     */
    protected Fragment getCurrentFragment() {
        Fragment f = getSupportFragmentManager().findFragmentById(fragmentContainerId);
        return f;
    }

    /**
     * Remove current fragment
     */
    private void removeCurrentFragment() {
        Fragment f = getCurrentFragment();

        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commit();
        }
    }


}
