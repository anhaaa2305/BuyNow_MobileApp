package com.haluuvananh.ecommerce_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.OnSelectionChangedListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.haluuvananh.ecommerce_app_v1.Fragments.ChangePasswordFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.FavouriteFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.HistoryFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.HomeFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.IndexFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.MyCartFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.MyProfileFragment;
import com.haluuvananh.ecommerce_app_v1.Fragments.NotificationFragment;
import com.haluuvananh.ecommerce_app_v1.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth auth;

    private int mCurrentFragment = IndexFragment.FRAGMENT_HOME;

    private NavigationView mNavigationView;

    private DrawerLayout mDrawerLayout;

    private FrameLayout contentFrame;
    private Toolbar toolbar;
    private ImageView imgAvatar;
    private TextView tvName, tvEmail;

    private final MyProfileFragment mMyProfileFragment = new MyProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        initUi();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        loadFragment(new HomeFragment());
        mNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);


    }

    private void initUi() {
        toolbar = findViewById(R.id.home_toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        contentFrame = findViewById(R.id.content_frame);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_myCart) {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
            finish();
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {

            if (mCurrentFragment != IndexFragment.FRAGMENT_HOME) {
                loadFragment(new HomeFragment());
                mCurrentFragment = IndexFragment.FRAGMENT_HOME;
            }

        } else if (id == R.id.nav_my_cart) {

            if (mCurrentFragment != IndexFragment.FRAGMENT_MY_CART) {
                loadFragment(new MyCartFragment());
                mCurrentFragment = IndexFragment.FRAGMENT_MY_CART;
            }

        } else if (id == R.id.nav_history) {
            if (mCurrentFragment != IndexFragment.FRAGMENT_HISTORY) {
                loadFragment(new HistoryFragment());
                mCurrentFragment = IndexFragment.FRAGMENT_HISTORY;
            }

        } else if (id == R.id.nav_my_profile) {
            if (mCurrentFragment != IndexFragment.FRAGMENT_MY_PROFILE) {
                loadFragment(mMyProfileFragment);
                mCurrentFragment = IndexFragment.FRAGMENT_MY_PROFILE;
            }
        } else if (id == R.id.nav_change_password) {
            if (mCurrentFragment != IndexFragment.FRAGMENT_CHANGE_PASSWORD) {
                loadFragment(new ChangePasswordFragment());
                mCurrentFragment = IndexFragment.FRAGMENT_CHANGE_PASSWORD;
            }
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_notification) {
            if (mCurrentFragment != IndexFragment.FRAGMENT_NOTIFICATION) {
                loadFragment(new NotificationFragment());
                mCurrentFragment = IndexFragment.FRAGMENT_NOTIFICATION;
            }
        } else if (id == R.id.nav_favourite) {
            if (mCurrentFragment != IndexFragment.FRAGMENT_FAVOURITE) {
                loadFragment(new FavouriteFragment());
                mCurrentFragment = IndexFragment.FRAGMENT_NOTIFICATION;
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            /*get out app*/
            super.onBackPressed();

        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }
}