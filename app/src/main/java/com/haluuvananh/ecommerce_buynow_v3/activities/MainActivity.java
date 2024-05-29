package com.haluuvananh.ecommerce_buynow_v3.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.fragments.CartFragment;
import com.haluuvananh.ecommerce_buynow_v3.fragments.HomeFragment;
import com.haluuvananh.ecommerce_buynow_v3.fragments.ProductFragment;
import com.haluuvananh.ecommerce_buynow_v3.fragments.ProfileFragment;
import com.haluuvananh.ecommerce_buynow_v3.fragments.SearchFragment;
import com.haluuvananh.ecommerce_buynow_v3.fragments.WishlistFragment;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.SimpleOnSearchActionListener;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment;
    CartFragment cartFragment;
    SearchFragment searchFragment;
    WishlistFragment wishlistFragment;
    ProfileFragment profileFragment;
    LinearLayout searchLinearLayout;
    MaterialSearchBar searchBar;
    FragmentManager fm;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchLinearLayout = findViewById(R.id.linearLayout);
        searchBar = findViewById(R.id.searchBar);

        homeFragment = new HomeFragment();
        cartFragment = new CartFragment();
        wishlistFragment = new WishlistFragment();
        profileFragment = new ProfileFragment();
        searchFragment = new SearchFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            fm = getSupportFragmentManager();
            transaction = fm.beginTransaction();

            if (item.getItemId() == R.id.home) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                transaction.replace(R.id.main_frame_layout, homeFragment, "home");
            } else if (item.getItemId() == R.id.cart) {
                if (!cartFragment.isAdded()) {
                    transaction.replace(R.id.main_frame_layout, cartFragment, "cart");
                    transaction.addToBackStack(null);
                }
            } else if (item.getItemId() == R.id.wishlist) {
                if (!wishlistFragment.isAdded()) {
                    transaction.replace(R.id.main_frame_layout, wishlistFragment, "wishlist");
                    transaction.addToBackStack(null);
                }
            } else if (item.getItemId() == R.id.profile) {
                if (!profileFragment.isAdded()) {
                    transaction.replace(R.id.main_frame_layout, profileFragment, "profile");
                    transaction.addToBackStack(null);
                }
            }
            transaction.commit();
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
        addOrRemoveBadge();

        getSupportFragmentManager().addOnBackStackChangedListener(this::updateBottomNavigationSelectedItem);

        searchBar.setOnSearchActionListener(new SimpleOnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                super.onSearchStateChanged(enabled);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (!searchFragment.isAdded())
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, searchFragment, "search").addToBackStack(null).commit();
                super.onSearchConfirmed(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                super.onButtonClicked(buttonCode);
            }
        });

        handleDeepLink();

        if (getIntent().getBooleanExtra("orderPlaced", false)){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment, "profile").addToBackStack(null).commit();
            bottomNavigationView.setSelectedItemId(R.id.profile);
        }
    }

   public void showSearchBar(){
        searchLinearLayout.setVisibility(View.VISIBLE);
    }

    public void hideSearchBar(){
        searchLinearLayout.setVisibility(View.GONE);
    }
    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() > 0)
            fm.popBackStack();
        else
            super.onBackPressed();
    }

    private void updateBottomNavigationSelectedItem() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_frame_layout);

        if (currentFragment instanceof HomeFragment)
            bottomNavigationView.setSelectedItemId(R.id.home);
        else if (currentFragment instanceof CartFragment)
            bottomNavigationView.setSelectedItemId(R.id.cart);
        else if (currentFragment instanceof WishlistFragment)
            bottomNavigationView.setSelectedItemId(R.id.wishlist);
        else if (currentFragment instanceof ProfileFragment)
            bottomNavigationView.setSelectedItemId(R.id.profile);
    }

    public void addOrRemoveBadge() {
        FirebaseUtil.getCartItems().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        int n = task.getResult().size();
                        BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.cart);
                        badge.setBackgroundColor(Color.parseColor("#FFF44336"));
                        if (n > 0) {
                            badge.setVisible(true);
                            badge.setNumber(n);
                        } else {
                            badge.setVisible(false);
                            badge.clearNumber();
                        }
                    }
                });
    }

    private void handleDeepLink(){
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {
                    Uri deepLink = null;
                    if (pendingDynamicLinkData != null)
                        deepLink = pendingDynamicLinkData.getLink();

                    if (deepLink != null){
                        Log.i("DeepLink", deepLink.toString());
                        String productId = deepLink.getQueryParameter("product_id");
                        Fragment fragment = ProductFragment.newInstance(Integer.parseInt(productId));
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, fragment).addToBackStack(null).commit();
                    }
                })
                .addOnFailureListener(e -> Log.i("Error123", e.toString()));
    }
}