package com.example.bookwormadventuresdeluxe2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

// https://stackoverflow.com/questions/15249409/getting-error-in-barcode-scanner-in-android
// Better way for later: https://www.youtube.com/watch?v=drH63NpSWyk
public class IsbnScanActivity extends AppCompatActivity implements View.OnClickListener
{
    public static int LAUNCH_SCAN_ISBN = 42069;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isbn_scan);
    }

    /**
     * Clicked the "Scan" button
     *
     * @param v
     */
    @Override
    public void onClick(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(
                IsbnScanActivity.this);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // I added this so the back button in the app bar would just end this activity
        // Not start a new MyBooksActivity
        // https://stackoverflow.com/questions/14437745/how-to-override-action-bar-back-button-in-android
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                break;
            default:
                /* We would not expect any other id */
                throw new IllegalArgumentException();
        }
        return true;
    }

    /**
     * This method gets called when returning from the barcode scanner function
     * Process the data here then return it to the calling activity with finis()
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        if (scanResult != null)
        {
            Toast.makeText(getApplicationContext(), "scan   " + scanResult.getContents(), Toast.LENGTH_LONG).show();
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}