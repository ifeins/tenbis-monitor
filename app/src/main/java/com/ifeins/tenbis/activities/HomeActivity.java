package com.ifeins.tenbis.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.ifeins.tenbis.R;
import com.ifeins.tenbis.fragments.OverviewFragment;
import com.ifeins.tenbis.fragments.StatsFragment;
import com.ifeins.tenbis.fragments.TransactionsFragment;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ITEM_STATS = 0;
    private static final int ITEM_OVERVIEW = 1;
    private static final int ITEM_TRANSACTIONS = 2;

    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private HomePageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mViewPager = findViewById(R.id.home_view_pager);
        mAdapter = new HomePageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(ITEM_OVERVIEW);

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

    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener((task) -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "signOut: Failed to sign out", task.getException());
                    }
                });
    }

    public static class HomePageAdapter extends FragmentPagerAdapter {

        public HomePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StatsFragment();
                case 1:
                    return new OverviewFragment();
                case 2:
                    return new TransactionsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }


}
