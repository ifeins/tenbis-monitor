package com.ifeins.tenbis.activities;

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
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.ifeins.tenbis.R;
import com.ifeins.tenbis.fragments.OverviewFragment;
import com.ifeins.tenbis.fragments.StatsFragment;
import com.ifeins.tenbis.fragments.TransactionsFragment;
import com.ifeins.tenbis.models.User;
import com.ifeins.tenbis.services.TenbisMonitorService;
import com.ifeins.tenbis.services.UsersService;
import com.ifeins.tenbis.utils.MenuUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int ITEM_STATS = 0;
    private static final int ITEM_OVERVIEW = 1;
    private static final int ITEM_TRANSACTIONS = 2;

    private BottomNavigationView mNavigationView;
    private ViewPager mViewPager;
    private HomePageAdapter mAdapter;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mAdapter = new HomePageAdapter(getSupportFragmentManager());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_actionbar, menu);
        MenuUtils.tintMenuItemDrawable(this, menu, R.id.action_sync, android.R.color.white);
        MenuUtils.tintMenuItemDrawable(this, menu, R.id.action_sign_out, android.R.color.white);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                syncUserData();
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void syncUserData() {
        UsersService usersService = TenbisMonitorService.getInstance().getUsersService();
        usersService.refresh(User.getCurrentUser()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // don't need to do anything as firestore will automatically take care of refreshing data
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener((task) -> {
                    User.setCurrentUser(null);
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
