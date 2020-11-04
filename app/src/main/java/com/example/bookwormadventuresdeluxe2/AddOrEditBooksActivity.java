package com.example.bookwormadventuresdeluxe2;

/**
 * This activity houses the functionality for adding or editing a new book. When in
 * editing mode, the fields are populated with the values of the selected book
 * and the user may update these fields as they wish. In adding mode, the fields are
 * blank and the user must enter the information from scratch.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.UUID;

public class AddOrEditBooksActivity extends AppCompatActivity
{
    TextView takePhoto;
    ImageView bookPicture;
    EditText titleView, authorView, descriptionView, isbnView;
    boolean editingBook = false;
    Button deleteButton;
    Book bookToEdit;
    String bookPhotoDowloadUrl = "";

    FirebaseAuth firebaseAuth;

    public static int ADD_BOOK = 0;
    public static int EDIT_BOOK = 1;
    public static int DELETE_BOOK = 2;
    public static int REQUEST_IMAGE_UPLOAD = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_books);
        // https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity?
        takePhoto = (TextView) findViewById(R.id.take_photo);
        titleView = findViewById(R.id.title_edit_text);
        authorView = findViewById(R.id.author_edit_text);
        descriptionView = findViewById(R.id.description_edit_text);
        isbnView = findViewById(R.id.isbn_edit_text);
        deleteButton = findViewById(R.id.delete_button);
        bookPicture = findViewById(R.id.book_photo);

        /* If editing a book, prepopulate text fields with their old values */
        int requestCode = -1;
        if (getIntent().getSerializableExtra("requestCode") != null)
        {
            requestCode = getIntent().getIntExtra("requestCode", 0);
        }
        if (requestCode == AddOrEditBooksActivity.EDIT_BOOK)
        {
            this.editingBook = true;
            this.bookToEdit = (Book) getIntent().getSerializableExtra("bookToEdit");
            bookPhotoDowloadUrl = bookToEdit.getImageUrl();

            if (bookToEdit != null)
            {
                titleView.setText(bookToEdit.getTitle());
                authorView.setText(bookToEdit.getAuthor());
                descriptionView.setText(bookToEdit.getDescription());
                isbnView.setText(bookToEdit.getIsbn());
                bookToEdit.setPhoto(this.bookToEdit, bookPicture);
            }
        }

        /* Hide the delete button if we are adding a book */
        if (!this.editingBook)
        {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Allows the user to upload a photo for the book from their gallery
     * of images on their phone
     * https://developer.android.com/training/camera/photobasics
     *
     * @param view
     */
    public void uploadPhoto(View view)
    {
        Intent uploadPhotoIntent = new Intent();
        uploadPhotoIntent.setType("image/*");
        uploadPhotoIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(uploadPhotoIntent, getResources().getString(R.string.select_photo)), REQUEST_IMAGE_UPLOAD);
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
                this.bookToEdit.setImageUrl(bookPhotoDowloadUrl);

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
                        title, author, description, isbn, Status.Available, bookPhotoDowloadUrl));

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
        /* I added this so the back button in the app bar would just end this activity
         * Not start a new MyBooksActivity
         * https://stackoverflow.com/questions/14437745/how-to-override-action-bar-back-button-in-android */
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
     * This where the activity handles the barcode scanned and uploads photos
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_IMAGE_UPLOAD && resultCode == RESULT_OK)
        {
            uploadPhoto(intent.getData());
        }
        else
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

    /**
     * Uploads the selected image to FireStorage and updates the view with the
     * chosen image on success
     *
     * @param selectedImage the Uri of the selected image
     */
    public void uploadPhoto(Uri selectedImage)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* If the book already has a photo, we want to delete it before adding a new one */
        if (this.bookToEdit != null && this.bookToEdit.getImageUrl().compareTo("") != 0)
        {
            StorageReference currentBookPhoto = storage.getReferenceFromUrl(this.bookToEdit.getImageUrl());
            // https://stackoverflow.com/questions/45103085/deleting-file-from-firebase-storage-using-url
            currentBookPhoto.delete().addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception exception)
                {
                    /* Should delete manually from firebase */
                    Log.v("uploadPhoto", "Old photo was not deleted");
                }
            });
        }

        // https://code.tutsplus.com/tutorials/image-upload-to-firebase-in-android-application--cms-29934
        if (selectedImage != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            /* Create a unique ID for the uploaded photo and store it in Fire Storage */
            StorageReference bookPhotoReference = storageReference.child("images/" + UUID.randomUUID().toString());
            bookPhotoReference.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            progressDialog.dismiss();
                            /* Update the image view in current screen with uploaded image */
                            Bitmap bitmap = null;
                            try
                            {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                                bookPicture.setImageBitmap(bitmap);
                                bookPicture.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }

                            /* Get the download URL for the newly added image so it can be mapped to the
                             * corresponding book and stored in FireStore */
                            StorageReference dataRef = FirebaseStorage.getInstance().getReference().child(bookPhotoReference.getPath());
                            dataRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                            {
                                @Override
                                public void onSuccess(Uri downloadUrl)
                                {
                                    bookPhotoDowloadUrl = downloadUrl.toString();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(AddOrEditBooksActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }
}
