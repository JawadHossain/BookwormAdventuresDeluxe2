package com.example.bookwormadventuresdeluxe2.Utilities;

import android.view.View;

/**
 * Defines a click listener and long click listener for a RecyclerView
 */
public interface RecyclerViewClickListener
{
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}