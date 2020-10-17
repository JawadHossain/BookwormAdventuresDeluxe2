package com.example.bookwormadventuresdeluxe2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyBooksActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{

    BottomNavigationView navbar;
    MyBooksFragment myBooksFragment = new MyBooksFragment();
    SearchFragment searchFragment = new SearchFragment();
    RequestsFragment requestsFragment = new RequestsFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("My Books");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        navbar = findViewById(R.id.bottom_navbar);
        navbar.setOnNavigationItemSelectedListener(this);
        navbar.setSelectedItemId(R.id.my_books_menu_item); // Set My Books as default

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        /* Override to open a new Fragment*/
        switch (item.getItemId())
        {
            case R.id.my_books_menu_item:
                /* Respond to My books click*/
                replaceFragment(myBooksFragment);
                setTitle("My Books");
                break;
            case R.id.search_menu_item:
                /* Respond to Search click*/
                replaceFragment(searchFragment);
                setTitle("Search");
                break;
            case R.id.requests_menu_item:
                /* Respond to Requests click*/
                replaceFragment(requestsFragment);
                setTitle("Requests");
                break;
            case R.id.profile_menu_item:
                /* Respond to  Profile click*/
                replaceFragment(profileFragment);
                setTitle("Profile");
                break;
        }

        /* return true to select Menu item */
        return true;
    }

    public void replaceFragment(Fragment fragment)
    {
        /* Update fragment Container with new fragment
         * Use fade in and fade out for transition
         * */
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.frame_container, fragment).commit();
    }
}