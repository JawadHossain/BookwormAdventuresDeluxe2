package com.example.bookwormadventuresdeluxe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class AddOrEditBooksActivity extends AppCompatActivity
{
    TextView takePhoto;
    ImageView bookPicture;
    EditText titleView, authorView, descriptionView, isbnView;
    boolean editingBook = false;
    Button deleteButton;
    Book bookToEdit;

    FirebaseAuth firebaseAuth;

    public static int ADD_BOOK = 0;
    public static int EDIT_BOOK = 1;
    public static int DELETE_BOOK = 2;

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
        deleteButton = findViewById(R.id.delete_button);

        // If editing a book, prepopulate text fields with their old values
        int requestCode = -1;
        if (getIntent().getSerializableExtra("requestCode") != null)
        {
            requestCode = getIntent().getIntExtra("requestCode", 0);
        }
        if (requestCode == AddOrEditBooksActivity.EDIT_BOOK)
        {
            this.editingBook = true;
            this.bookToEdit = (Book) getIntent().getSerializableExtra("bookToEdit");

            if (bookToEdit != null)
            {
                titleView.setText(bookToEdit.getTitle());
                authorView.setText(bookToEdit.getAuthor());
                descriptionView.setText(bookToEdit.getDescription());
                isbnView.setText(bookToEdit.getIsbn());
            }
        }

        /* Hide the delete button if we are adding a book */
        if (!this.editingBook)
        {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    public void takePhoto(View view)
    {
        // TODO: implement the camera
        return;
    }

    /**
     * Returns the new or edited book to the activity that called EditBooksActivity
     *
     * @param view
     */
    public void saveBook(View view)
    {
        firebaseAuth = FirebaseAuth.getInstance();
        String title, author, description, isbn;

        title = titleView.getText().toString();
        author = authorView.getText().toString();
        description = descriptionView.getText().toString();
        isbn = isbnView.getText().toString();

        if (fieldsValid())
        {
            if (editingBook)
            {
                // Update the book and send it back to the calling fragment, MyBooksDetailViewFragment
                this.bookToEdit.setTitle(titleView.getText().toString());
                this.bookToEdit.setAuthor(authorView.getText().toString());
                this.bookToEdit.setDescription(descriptionView.getText().toString());
                this.bookToEdit.setIsbn(isbnView.getText().toString());

                Intent intent = new Intent();
                setResult(this.EDIT_BOOK, intent);
                intent.putExtra("EditedBook", this.bookToEdit);
            }
            else
            {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                // status when adding book is available
                intent.putExtra("NewBook", new Book(UserCredentialAPI.getInstance().getUsername(),
                                                        title, author, description, isbn, Status.Available));
            }
            finish();
        }
    }

    /**
     * Validate the fields entered in this activity
     * Title and author cannot be empty
     * ISBN can be empty, or has digits or length 10 or 13
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean fieldsValid()
    {
        boolean valid = true;
        if (TextUtils.isEmpty(titleView.getText().toString()))
        {
            EditTextValidator.isEmpty(titleView);
            valid = false;
        }
        if (TextUtils.isEmpty(authorView.getText().toString()))
        {
            EditTextValidator.isEmpty(authorView);
            valid = false;
        }
        // ISBN cannot be empty
        String isbn_input = isbnView.getText().toString();
        if (TextUtils.isEmpty(isbn_input))
        {
            EditTextValidator.isEmpty(isbnView);
            valid = false;
        } // Only display one error message
        else if (!(isbn_input.matches("\\d{10}") ||
                isbn_input.matches("\\d{13}")))
        {
            // ISBN only has digits of length 10 or 13
            // https://en.wikipedia.org/wiki/International_Standard_Book_Number
            EditTextValidator.invalidIsbn(isbnView);
            valid = false;
        }

        return valid;
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
                AddOrEditBooksActivity.this);
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
            String isbn_scan_result = scanResult.getContents();
            // Older versions had 9 digits but can be converted to 10 "by prefixing it with a zero"
            // https://en.wikipedia.org/wiki/International_Standard_Book_Number
            if (isbn_scan_result.length() == 9)
            {
                isbn_scan_result = "0" + isbn_scan_result;
            }
            isbnView.setText(isbn_scan_result);
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    /**
     * Listener for the delete button
     * When the delete button is pressed, remove the current book from the db
     *
     * @param view
     */
    public void onDeleteButtonClick(View view)
    {
        String documentId;
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        if (getIntent().getSerializableExtra("documentId") != null)
        {
            documentId = getIntent().getStringExtra("documentId");
        }
        else
        {
            /* This should never be possible, documentId is passed into this activity. No document
               id means a problem for the query.
             */
            throw new IllegalStateException("No documentId passed to Edit Book Activity.");
        }

        if (this.bookToEdit != null)
        {
            rootRef.collection(getString(R.string.books_collection)).document(documentId).delete();

            Intent intent = new Intent();
            /* Set result to deleted so when we return to the previous fragment we know delete was pressed */
            setResult(this.DELETE_BOOK, intent);
            /* Return one activity up */
            finish();
        }
        else
        {
            /* We should never be able to get into a state where we can see this button but we
               aren't editing a book.
             */
            throw new IllegalStateException("Pressed the Delete Button but wasn't editing a book!");
        }
    }
}
