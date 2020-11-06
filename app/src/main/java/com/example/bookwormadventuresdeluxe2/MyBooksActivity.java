package com.example.bookwormadventuresdeluxe2;

/**
 * MyBooksActivity is the "main menu" activity required to launch the majority of the other
 * activities and fragments. Fromt this activity, users and enter the "My Books" fragment, the
 * "Search" fragment, the "Requests/Borrows" fragment, and the "Profile" fragment via the
 * navigation fragment. Additionally, users can enter the notifications fragment, scan book
 * fragment, or the add book fragment.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;

public class MyBooksActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener
{

    private BottomNavigationView navbar;
    private MyBooksFragment myBooksFragment = new MyBooksFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private RequestsFragment requestsFragment = new RequestsFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);

        navbar = findViewById(R.id.bottom_navbar);
        navbar.setOnNavigationItemSelectedListener(this);
        navbar.setSelectedItemId(R.id.my_books_menu_item); // Set My Books as default

    }

    // Todo : Fix Profile Avatar visibility issue
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

                /* Pulling UserProfileObject from database */
                FirebaseUserGetSet.getUser(UserCredentialAPI.getInstance().getUsername(), new FirebaseUserGetSet.UserCallback()
                {
                    @Override
                    public void onCallback(UserProfileObject userObject)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.profile_object), userObject);
                        profileFragment.setArguments(bundle);

                        /* Opening MyProfileFragment */
                        replaceFragment(profileFragment);
                        setTitle("Profile");
                    }
                });
                break;
            default:
                /* We would not expect any other id */
                throw new IllegalArgumentException();
        }

        /* return true to select Menu item */
        return true;
    }

    // https://developer.android.com/guide/topics/ui/dialogs
    @Override
    public void onBackPressed()
    {
        ExitConfirmationDialogFragment exitConfirmation = new ExitConfirmationDialogFragment();
        exitConfirmation.show(getSupportFragmentManager(), "ConfirmExit");
    }

    /**
     * Update fragment Container with new fragment
     * Use fade in and fade out for transition
     *
     * @param fragment The fragment to replace this one with
     */
    public void replaceFragment(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.frame_container, fragment).commit();
    }

    /**
     * Gets called when an activity called with startActivityForResult returns
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == IntentIntegrator.REQUEST_CODE)
        {
            // https://stackoverflow.com/questions/6147884/onactivityresult-is-not-being-called-in-fragment
            for (Fragment fragment : getSupportFragmentManager().getFragments())
            {
                // cascade the onActivityResult call into this activity's fragments
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}