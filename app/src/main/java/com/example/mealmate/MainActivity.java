package com.example.mealmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.blue));

        // Initialize the DrawerLayout and BottomAppBar
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set listeners
        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onBottomNavigationItemSelected);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null) {
            // Perform any necessary setup
        }

        // Set up the ActionBarDrawerToggle to link toolbar with drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Load the HomeFragment initially when the app is first started
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    // Method to load fragments dynamically
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Fragment selectedFragment = null;

        // Check which item was selected from the drawer
        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_about) {
            selectedFragment = new AboutFragment();
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        // If a fragment was selected, replace the current fragment with the selected one
        if (selectedFragment != null) {
            Log.d("MainActivity", "Fragment Selected: " + selectedFragment.getClass().getSimpleName());
            loadFragment(selectedFragment);
        }

        // Close the drawer after item selection
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // BottomNavigationView item selection listener for switching fragments
    private boolean onBottomNavigationItemSelected(MenuItem item) {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.bottom_favourite) {
            selectedFragment = new FavouriteFragment();
        } else if (item.getItemId() == R.id.bottom_manage) {
            selectedFragment = new ManageFragment();
        } else if (item.getItemId() == R.id.bottom_create) {
            selectedFragment = new CreateFragment();
        } else if (item.getItemId() == R.id.bottom_grocery_list) {
            selectedFragment = new GroceryListFragment();
        } else if (item.getItemId() == R.id.bottom_Map) {
            selectedFragment = new MapFragment();
        }

        // If a fragment was selected, replace the current fragment with the selected one
        if (selectedFragment != null) {
            loadFragment(selectedFragment);
            return true;
        }

        return false;
    }

    public void refreshFavoriteFragment() {
        FavouriteFragment favouriteFragment = (FavouriteFragment) getSupportFragmentManager().findFragmentByTag("FavouriteFragment");
        if (favouriteFragment != null) {
            favouriteFragment.refreshFavoriteMeals();
        }
    }



}