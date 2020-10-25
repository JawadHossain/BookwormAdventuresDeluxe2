package com.example.bookwormadventuresdeluxe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class EditBooksActivity extends AppCompatActivity
{
    TextView takePhoto;
    ImageView bookPicture;
    EditText titleView, authorView, descriptionView, isbnView;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    // random numbers to identify what the activity will do
    public static int ADD_BOOK = 50853;
    public static int EDIT_BOOK = 34880;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_books);
        // TODO: implement the camera later
        // https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity?
        takePhoto = (TextView) findViewById(R.id.take_photo);
        titleView = findViewById(R.id.title_edit_text);
        authorView = findViewById(R.id.author_edit_text);
        descriptionView = findViewById(R.id.description_edit_text);
        isbnView = findViewById(R.id.isbn_edit_text);
    }

    public void takePhoto(View view)
    {
        // TODO: implement the camera
        return;
    }

    /**
     * Returns the new book to the activity that called EditBooksActivity
     *
     * @param view
     */
    public void saveBook(View view)
    {
        String title, author, description, isbn;

        title = titleView.getText().toString();
        author = authorView.getText().toString();
        description = descriptionView.getText().toString();
        isbn = isbnView.getText().toString();

        if (Book.fieldsValid(title, author, description, isbn))
        {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            // status when adding book is available
            intent.putExtra("NewBook", new Book(title, author, description, isbn, Status.Available));
            finish();
        }
    }

    /**
     * This method was added so that
     * Tapping the back button ends this activity
     * Not start a new MyBooksActivity
     *
     * @param item
     * @return
     */
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
     * Opens the barcode scanning functionality
     *
     * @param v
     */
    public void scanIsbn(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(
                EditBooksActivity.this);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    /**
     * This where the activity handles the barcode scanned
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        if (scanResult != null)
        {
//            Toast.makeText(getApplicationContext(), "scan   " + scanResult.getContents(), Toast.LENGTH_LONG).show();
            isbnView.setText(scanResult.getContents());
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }


}