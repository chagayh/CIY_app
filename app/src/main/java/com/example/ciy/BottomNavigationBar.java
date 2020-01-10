package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavigationBar extends AppCompatActivity {

    private HomeFragment homeFragment;
    private FavoritesFragment favoritesFragment;
    private SearchFragment searchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            homeFragment = new HomeFragment();
            favoritesFragment = new FavoritesFragment();
            searchFragment = new SearchFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                homeFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    displayHomeFragment();
                    break;
                case R.id.nav_favorites:
                    displayFavoritesFragment();
                    break;
                case R.id.nav_search:
                    displaySearchFragment();
                    break;
            }
//            // Replace the content of the container
//            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
//            // Append this transaction to the backstack
//            fragmentTransaction.addToBackStack("optional tag");
//            // Commit the changes
//            fragmentTransaction.commit();
            return true;
        }
    };

    protected void displayHomeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (homeFragment.isAdded()) { // if the fragment is already in container
            fragmentTransaction.show(homeFragment);
        } else { // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, homeFragment, "Home Fragment");
        }
        // Hide Favorites fragment
        if (favoritesFragment.isAdded()) {
            fragmentTransaction.hide(favoritesFragment);
        }
        // Hide Search fragment
        if (searchFragment.isAdded()) {
            fragmentTransaction.hide(searchFragment);
        }
        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displayFavoritesFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (favoritesFragment.isAdded()) { // if the fragment is already in container
            fragmentTransaction.show(favoritesFragment);
        } else { // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, favoritesFragment, "Favorites Fragment");
        }
        // Hide Favorites fragment
        if (homeFragment.isAdded()) {
            fragmentTransaction.hide(homeFragment);
        }
        // Hide Search fragment
        if (searchFragment.isAdded()) {
            fragmentTransaction.hide(searchFragment);
        }
        // Commit changes
        fragmentTransaction.commit();
    }

    protected void displaySearchFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (searchFragment.isAdded()) { // if the fragment is already in container
            fragmentTransaction.show(searchFragment);
        } else { // fragment needs to be added to frame container
            fragmentTransaction.add(R.id.fragment_container, searchFragment, "Search Fragment");
        }
        // Hide Favorites fragment
        if (homeFragment.isAdded()) {
            fragmentTransaction.hide(homeFragment);
        }
        // Hide Search fragment
        if (favoritesFragment.isAdded()) {
            fragmentTransaction.hide(favoritesFragment);
        }
        // Commit changes
        fragmentTransaction.commit();
    }
}
