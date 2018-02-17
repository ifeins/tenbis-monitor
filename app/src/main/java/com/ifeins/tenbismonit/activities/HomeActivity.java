package com.ifeins.tenbismonit.activities;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ifeins.tenbismonit.R;
import com.ifeins.tenbismonit.fragments.HomeAdapterFragment;
import com.ifeins.tenbismonit.fragments.OverviewFragment;
import com.ifeins.tenbismonit.fragments.StatsFragment;
import com.ifeins.tenbismonit.fragments.TransactionsFragment;
import com.ifeins.tenbismonit.models.User;
import com.ifeins.tenbismonit.services.TenbisMonitorService;
import com.ifeins.tenbismonit.services.UsersService;
import com.ifeins.tenbismonit.utils.FirebaseUtils;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ITEM_STATS = 0;
    private static final int ITEM_OVERVIEW = 1;
    private static final int ITEM_TRANSACTIONS = 2;
    private static final int RC_SIGN_IN = 1;
    private static final String STATE_DURING_SYNC = "state_during_sync";

    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private HomePageAdapter mAdapter;
    private Toolbar mToolbar;
    private Menu mMenu;
    private boolean mDuringSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mDuringSync = savedInstanceState.getBoolean(STATE_DURING_SYNC);
        }

        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        List<Fragment> fragments = Arrays.asList(new StatsFragment(), new OverviewFragment(), new TransactionsFragment());
        mAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
        mViewPager = findViewById(R.id.home_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(ITEM_OVERVIEW);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case ITEM_STATS:
                        mNavigationView.setSelectedItemId(R.id.action_stats);
                        break;
                    case ITEM_OVERVIEW:
                        mNavigationView.setSelectedItemId(R.id.action_overview);
                        break;
                    case ITEM_TRANSACTIONS:
                        mNavigationView.setSelectedItemId(R.id.action_transactions);
                        break;
                }
            }
        });

        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setSelectedItemId(R.id.action_overview);
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_stats:
                    mViewPager.setCurrentItem(ITEM_STATS);
                    break;
                case R.id.action_overview:
                    mViewPager.setCurrentItem(ITEM_OVERVIEW);
                    break;
                case R.id.action_transactions:
                    mViewPager.setCurrentItem(ITEM_TRANSACTIONS);
                    break;
            }

            return true;
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_DURING_SYNC, mDuringSync);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_actionbar, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                syncUserData();
                return true;
            case R.id.action_sign_out:
                Toast.makeText(this, "Sign out is currently unsupported", Toast.LENGTH_SHORT).show();
//                signOut();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            User.setCurrentUser(new User(firebaseUser.getUid(), null, null));

            for (int i = 0; i < mAdapter.getCount(); i++) {
                HomeAdapterFragment fragment = (HomeAdapterFragment) mAdapter.getItem(i);
                fragment.subscribeForUpdates();
            }
        }
    }

    public void syncUserData() {
        if (mDuringSync) return;

        mDuringSync = true;
        Drawable drawable = mMenu.findItem(R.id.action_sync).getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }

        UsersService usersService = TenbisMonitorService.getInstance().getUsersService();
        usersService.refresh(User.getCurrentUser()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // don't need to do anything as firestore will automatically take care of refreshing data
                Drawable drawable = mMenu.findItem(R.id.action_sync).getIcon();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).stop();
                }
                mDuringSync = false;
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Drawable drawable = mMenu.findItem(R.id.action_sync).getIcon();
                if (drawable instanceof Animatable) {
                    ((Animatable) drawable).stop();
                }

                mDuringSync = false;
            }
        });
    }

    private void signOut() {
        for (int i = 0; i < mAdapter.getCount(); i++) {
            HomeAdapterFragment fragment = (HomeAdapterFragment) mAdapter.getItem(i);
            fragment.unsubscribeForUpdates();
        }

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        User.setCurrentUser(null);
                        FirebaseUtils.signIn(this, RC_SIGN_IN, false);
                    } else {
                        Log.e(TAG, "signOut: Failed to sign out", task.getException());
                    }
                });
    }

    public static class HomePageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;

        public HomePageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }


}
