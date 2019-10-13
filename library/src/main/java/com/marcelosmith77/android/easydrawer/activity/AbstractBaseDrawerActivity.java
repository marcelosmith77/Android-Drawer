package com.marcelosmith77.android.easydrawer.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PersistableBundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.marcelosmith77.android.easydrawer.fragment.IFragmentBackHandler;
import com.marcelosmith77.android.easydrawer.fragment.IFragmentRightDrawerHandler;
import com.marcelosmith77.android.easydrawer.fragment.IHomeFragment;

/**
 * The class controls the drawer behavior.
 */
public abstract class AbstractBaseDrawerActivity extends AppCompatActivity implements IActivityBackStackHandler, IActivityLeftDrawerHandler, IActivityRightDrawerHandler, IActivityBottomNavigationHandler {

    // Controls when user did touch once and ask him to touch again to exit from the app
    private boolean doubleBackToExitPressedOnce = false;

    private boolean drawerInitialized = false;

    // Left drawer that is in use on your main activity
    private DrawerLayout drawerLayout;

    // left navigation view
    private NavigationView leftNavigationView;

    // right navigation view
    private NavigationView rightNavigationView;

    // Bottom navigation view
    private BottomNavigationView bottomNavigationView;

    // Listener the toggles drawer visible
    private ActionBarDrawerToggle drawerToggle;

    // Container id, where fragments are replaced
    private int fragmentContainerId;

    @Override
    protected void onResume() {
        super.onResume();

        if (!drawerInitialized) {
            try {
                setupUI();
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

        // There is bottom navigation home menu, fires that
        if (getBottomHomeMenuId() != -1) {
            fireBottomNavigation(getBottomHomeMenuId());
        } else {
            Fragment f = getHomeFragment();

            if (f != null) {
                showFrament(f);
                maybeCheckHomeLeftNavigation();
            }
        }
    }

    /**
     * Get Drawer parts to setup an events and listeners related to the drawer
     * @return BaseDrawerParts
     */
    protected abstract BaseDrawerParts getBaseDrawerParts();

    /**
     * Setup drawer events
     */
    private void setupUI() {

        final BaseDrawerParts parts = getBaseDrawerParts();

        setSupportActionBar(parts.toolbar);

        this.drawerLayout = parts.drawerLayout;
        this.leftNavigationView = parts.leftNavigationView;
        this.bottomNavigationView = parts.bottomNavigationView;
        this.fragmentContainerId = parts.fragmentContainerId;

        // Setup Navigation item listener (START SIDE)
        if (parts.leftNavigationView != null) {
            parts.leftNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
        if (parts.rightNavigationViews != null) {
            for (NavigationView rightView : parts.rightNavigationViews) {
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

            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, parts.toolbar, parts.openDrawerContentDescRes, parts.closeDrawerContentDescRes) {
                @Override
                public void onDrawerOpened(View drawerView) {

                    if (drawerView.getId() == parts.leftNavigationView.getId()) {
                        onLeftDrawerOpened(drawerView);
                    } else {
                        onRightDrawerOpened(drawerView);
                    }
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (drawerView.getId() == parts.leftNavigationView.getId()) {
                        onLeftDrawerClosed(drawerView);
                    } else {
                        onRightDrawerClosed(drawerView);
                    }
                }
            };

            drawerLayout.addDrawerListener(drawerToggle);

            // Locks END side, that will be enabled by fragments that uses right drawers
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.END);

            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

                    if (backStackCount > 0) {
                        onBackPressed();
                    }
                }
            });

            drawerToggle.syncState();
        }

        parts.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int backStackCount = getSupportFragmentManager().getBackStackEntryCount();

                    if (backStackCount > 0) {
                        onBackPressed();
                    } else {
                        if (drawerToggle.isDrawerIndicatorEnabled()) {
                            int drawerLockMode = drawerLayout.getDrawerLockMode(GravityCompat.START);

                            if (drawerLayout.isDrawerVisible(GravityCompat.START) && (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN)) {
                                drawerLayout.closeDrawer(GravityCompat.START);
                            } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
                                drawerLayout.openDrawer(GravityCompat.START);
                            }
                        }
                    }
                }
        });

        if (parts.bottomNavigationView != null) {
            parts.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    return onBottomNavigationItemSelected(item);
                }
            });

            parts.bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem item) {
                    onBottomNavigationItemReselected(item);
                }
            });
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

                if (getSupportFragmentManager().isDestroyed()) {
                    return;
                }

                getSupportFragmentManager().executePendingTransactions();

                backStackCount = getSupportFragmentManager().getBackStackEntryCount();

                // there is fragments on the stack?
                if (backStackCount == 0) {
                    showHomeFragment(); // Stack is empty, show home fragment
                    resetDrawerIndicators();
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
     * Bottom navigation item selected
     *
     * @param menuItem - The selected item
     */
    @Override
    public boolean onBottomNavigationItemSelected(MenuItem menuItem) {
        return onBottomNavigationItemSelected(menuItem, null);
    }

    /**
     * Bottom navigation item re-selected
     *
     * @param menuItem - The RE-selected item
     */
    @Override
    public void onBottomNavigationItemReselected(MenuItem menuItem) {
        onBottomNavigationItemReselected(menuItem, null);
    }

    /**
     * Bottom navigation item selected
     *
     * @param menuItem - The selected item
     */
    @Override
    public boolean onBottomNavigationItemSelected(MenuItem menuItem, Bundle args) {

        if (menuItem.getItemId() == getBottomHomeMenuId()) {
            // home fired, reset drawer indicators
            resetDrawerIndicators();
            maybeCheckHomeLeftNavigation();
        }

        return true;
    }

    /**
     * Bottom navigation item re-selected
     *
     * @param item - The RE-selected item
     */
    @Override
    public void onBottomNavigationItemReselected(MenuItem item, Bundle args) {
    }

    /**
     * Aciona programaticamente um menu de navegação inferior
     * @param menuItemId
     */
    public void fireBottomNavigation(@IdRes int menuItemId) {

        fireBottomNavigation(menuItemId, null);
    }

    /**
     * Aciona programaticamente um menu de navegação inferior
     * @param menuItemId
     */
    public void fireBottomNavigation(@IdRes int menuItemId, Bundle args) {

        if (bottomNavigationView != null) {
            MenuItem menuItem = bottomNavigationView.getMenu().findItem(menuItemId);

            if (menuItem != null) {

                if (onBottomNavigationItemSelected(menuItem, args)) {
                    menuItem.setChecked(true);

                    if (menuItem.getItemId() == getBottomHomeMenuId()) {
                        // home fired, reset drawer indicators
                        resetDrawerIndicators();
                        maybeCheckHomeLeftNavigation();
                    }
                }
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
            if (!isHomeFragment && addToStack) {
                ft.addToBackStack(name);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if (getCurrentFragment() != null) {
            getCurrentFragment().onSaveInstanceState(outState);
        }
    }

    /**
     * Show hamburguer icon
     */
    private void resetDrawerIndicators() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        if (drawerToggle != null) {
            drawerToggle.setDrawerIndicatorEnabled(true);
            drawerToggle.syncState();
        }
    }

    private void maybeCheckHomeLeftNavigation() {

        if (getHomeMenuId() != -1) {

            // set home menu item selected inside drawer
            if (leftNavigationView != null) {
                leftNavigationView.setCheckedItem(getHomeMenuId());
            }
        }
    }

    @Override
    public int getBottomHomeMenuId() {
        return -1;
    }

    @Override
    public int getHomeMenuId() {
        return -1;
    }

    /**
     * Closes left navigation view
     */
    public void closeLeftDrawer() {
        if (this.drawerLayout != null)
            this.drawerLayout.closeDrawer(GravityCompat.START);
    }

    /**
     * Closes right navigation view
     */
    public void closeRightDrawer() {
        if (this.drawerLayout != null)
            this.drawerLayout.closeDrawer(GravityCompat.END);
    }

    /**
     * Closes left navigation view
     * @para defaultCheckedItem - navigation view item to be checked
     */
    public void closeLeftDrawer(@IdRes int defaultCheckedItem) {
        closeLeftDrawer();

        if (this.leftNavigationView != null)
            this.leftNavigationView.setCheckedItem(defaultCheckedItem);
    }
}
