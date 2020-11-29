package com.example.bookwormadventuresdeluxe2.Utilities;

/**
 * Class to track which fragment is active in the MyBooksActivity. This is used for determining
 * which fragment to show when returning from a different fragment or activity to the MyBooksActivity
 */

import androidx.fragment.app.Fragment;

public class ActiveFragmentTracker
{
    public static Fragment activeFragment = null;
}
