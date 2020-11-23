package com.example.bookwormadventuresdeluxe2;

import androidx.fragment.app.Fragment;

/**
 * Class to track which fragment is active in the MyBooksActivity. This is used for determining
 * which fragment to show when returning from a different fragment or activity to the MyBooksActivity
 */
public class ActiveFragmentTracker
{
    public static Fragment activeFragment = null;
}
