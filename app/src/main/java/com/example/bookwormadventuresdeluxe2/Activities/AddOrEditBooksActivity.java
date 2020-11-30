package com.example.bookwormadventuresdeluxe2.Activities;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bookwormadventuresdeluxe2.Models.Book;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.DownloadImageTask;
import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

public class AddOrEditBooksActivity extends AppCompatActivity
{
    private TextView takePhoto;
    private ImageView bookPicture;
    private ImageView scanIsbnButton;
    private EditText titleView, authorView, descriptionView, isbnView;
    private boolean editingBook = false;
    private boolean deleteBookPictureWhenSaving = false;
    private Button deleteButton;
    private FloatingActionButton saveButton;
    private FloatingActionButton deletePictureButton;
    private Book bookToEdit;
    private String bookPhotoDownloadUrl = "";

    private FirebaseAuth firebaseAuth;

    public static int ADD_BOOK = 0;
    public static int EDIT_BOOK = 1;
    public static int DELETE_BOOK = 2;
    public static int REQUEST_IMAGE_UPLOAD = 3;

    /* Request queue for REST requests */
    RequestQueue requestQueue;

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
        scanIsbnButton = findViewById(R.id.scan_isbn_button);
        deleteButton = findViewById(R.id.delete_button);
        deletePictureButton = findViewById(R.id.delete_picture);
        saveButton = findViewById(R.id.my_books_save_button);
        bookPicture = findViewById(R.id.book_photo);
        requestQueue = Volley.newRequestQueue(this);

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
            bookPhotoDownloadUrl = bookToEdit.getImageUrl();

            if (bookToEdit != null)
            {
                titleView.setText(bookToEdit.getTitle());
                authorView.setText(bookToEdit.getAuthor());
                descriptionView.setText(bookToEdit.getDescription());
                isbnView.setText(bookToEdit.getIsbn());
                bookToEdit.setPhoto(this.bookToEdit, bookPicture);
            }
        }

        /* When the user clicks on the delete button
         * Remove picture from screen and hide the delete button
         * This is necessary for when the user cancels editing the book
         */
        deletePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // will be checked when the user saves
                deleteBookPictureWhenSaving = true;
                // remove the picture from the screen
                bookPicture.setImageResource(R.drawable.ic_camera);
                // Hide the delete picture button
                deletePictureButton.hide();
            }
        });

        if (this.editingBook)
        {
            /* Disable editing if book is not available */
            if (!bookToEdit.getStatus().equals(Status.Available))
            {
                titleView.setFocusable(false);
                authorView.setFocusable(false);
                descriptionView.setFocusable(false);
                isbnView.setFocusable(false);
                deletePictureButton.hide();
                saveButton.hide();
                takePhoto.setVisibility(View.GONE);
                scanIsbnButton.setVisibility(View.GONE);
                /* Show Editing not available message */
                Toast toast = Toast.makeText(this, getString(R.string.unavailable_for_edit), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0,0);
                ViewGroup group = (ViewGroup) toast.getView();
                TextView messageTextView = (TextView) group.getChildAt(0);
                messageTextView.setTextSize(18);
                messageTextView.setTextColor(getResources().getColor(R.color.delete));
                toast.show();
            }
            else
            {
                /* Hide delete button if the book has no image url*/
                if (bookToEdit.getImageUrl().equals(""))
                {
                    deletePictureButton.hide();
                }
                else
                {
                    /* Only show the delete button if we are editing a book and it has an image*/
                    deletePictureButton.show();
                }
            }
        }
        else
        {
            /* Hide the delete button if we are adding a book */
            deleteButton.setVisibility(View.INVISIBLE);
            deletePictureButton.hide();
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
     * Deletes the picture associated with the book being edited
     */
    private void deletePhoto()
    {
        String imageUrl;
        if (editingBook)
        {
            imageUrl = this.bookToEdit.getImageUrl();
            // remove association of book to the image url
            this.bookToEdit.setImageUrl("");
        }
        else
        {
            /* This will occur when a user uploads a picture
             * when adding a new book then deletes it before saving the new book*/
            imageUrl = this.bookPhotoDownloadUrl;
        }

        /* This should never be called when the book does not have an image*/
        if (imageUrl.equals(""))
        {
            throw new IllegalStateException("deletePhoto should not have been called!");
        }

        // Remove the association to the book object
        this.bookPhotoDownloadUrl = "";

        this.deletePhotoFromFirebase(imageUrl);
    }

    /**
     * Returns the new or edited book to the activity that called EditBooksActivity
     *
     * @param view
     */
    public void saveBook(View view)
    {
        /* Save changes */
        firebaseAuth = FirebaseAuth.getInstance();
        String title, author, description, isbn;

        title = titleView.getText().toString();
        author = authorView.getText().toString();
        description = descriptionView.getText().toString();
        isbn = isbnView.getText().toString();

        if (fieldsValid())
        {
            if (this.deleteBookPictureWhenSaving)
            {
                // commit the changes to Firebase and the book object
                this.deletePhoto();
            }

            if (editingBook)
            {
                // Update the book and send it back to the calling fragment, MyBooksDetailViewFragment
                this.bookToEdit.setTitle(titleView.getText().toString());
                this.bookToEdit.setAuthor(authorView.getText().toString());
                this.bookToEdit.setDescription(descriptionView.getText().toString());
                this.bookToEdit.setIsbn(isbnView.getText().toString());
                this.bookToEdit.setImageUrl(bookPhotoDownloadUrl);

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
                        title, author, description, isbn, Status.Available, bookPhotoDownloadUrl));

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
     * This method modifies the back button functionality to end this activity
     * instead of starting a new MyBooksActivity.
     *
     * @param item The MenuItem that was clicked
     * @return true
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
     * @param v The view that this is called from
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
            deletePictureButton.show();
            // if the user uploads a picture, reassign this to false
            this.deleteBookPictureWhenSaving = false;
        }
        else
        {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, intent);
            if (scanResult != null)
            {
                String isbn_scan_result = scanResult.getContents();
                if (isbn_scan_result != null) // proceed if result present
                {
                    /* The scan picked up some invalid ISBN. */
                    if (isbn_scan_result.length() < 9)
                    {
                        Toast.makeText(AddOrEditBooksActivity.this, "Unable to detect the ISBN. Please Retry.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Older versions had 9 digits but can be converted to 10 "by prefixing it with a zero"
                    // https://en.wikipedia.org/wiki/International_Standard_Book_Number
                    if (isbn_scan_result.length() == 9)
                    {
                        isbn_scan_result = "0" + isbn_scan_result;
                    }
                    isbnView.setText(isbn_scan_result);

                    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn_scan_result;

                    /* Request the book details from Google's API */
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                            {

                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    parseBookResult(response);
                                }
                            }, new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                /*
                                   In this case, we simply won't add the additional book details
                                */
                                    Log.d("Volley Error: ", error.getMessage());
                                }
                            });
                    requestQueue.add(jsonObjectRequest);
                }
                else
                {
                    super.onActivityResult(requestCode, resultCode, intent);
                }
            }
        }

    }

    /**
     * Parse the result received from Google's book API after scanning an ISBN
     */
    private void parseBookResult(JSONObject result)
    {
        String title = "";
        String subtitle = "";
        String author = "";
        String description = "";
        String imageUrlString = "";

        JSONObject volumeInfo;

        /* Try to get the book's volume info (if there is even a match) */
        try
        {
            /* If there are no matches, return */
            if (result.getInt("totalItems") < 1)
            {
                return;
            }
            /* Multiple books may be returned by the books API, select the first one */
            volumeInfo = result.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");
        } catch (JSONException e)
        {
            return;
        }

        /* Try to get the author from the endpoint */
        try
        {
            JSONArray authors = volumeInfo.getJSONArray("authors");

            /* Since there may be multiple authors, append them together separated by a comma */
            for (int i = 0; i < authors.length(); i++)
            {
                if (i == 0)
                {
                    author += authors.getString(i);
                }
                else
                {
                    author += ", " + authors.getString(i);
                }
            }

            authorView.setText(author);
        } catch (JSONException e)
        {
            /* Do nothing, this is expected to happen sometimes */
        }

        /* Try to get the title from the endpoint */
        try
        {
            /*
               The subtitle may not exist. Catch it in here so that the exception doesn't bubble up
               and cause no data to be set for the title.
            */
            try
            {
                subtitle = volumeInfo.getString("subtitle");
            } catch (JSONException e)
            {
                /* Do nothing, this is expected to happen sometimes */
            }
            title = (subtitle == "") ? (volumeInfo.getString("title")) : (volumeInfo.getString("title") + ": " + subtitle);
            titleView.setText(title);
        } catch (JSONException e)
        {
            /* Do nothing, this is expected to happen sometimes */
        }

        /* Try to get the description from the endpoint */
        try
        {
            description = volumeInfo.getString("description");
            descriptionView.setText(description);
        } catch (JSONException e)
        {
            /* Do nothing, this is expected to happen sometimes */
        }

        /* Try to get a thumbnail of the book from the endpoint */
        try
        {
            imageUrlString = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
            /* For some reason this API is stupid and returns an http URL not https. Convert it. */
            if (imageUrlString.substring(0, 5) != "https")
            {
                imageUrlString = "https" + imageUrlString.substring(4);
            }
            /* Set the book photo to the photo stored at the url */
            // https://stackoverflow.com/questions/11831188/how-to-get-bitmap-from-a-url-in-android
            new DownloadImageTask(bookPicture).execute(imageUrlString);
            bookPhotoDownloadUrl = imageUrlString;
        } catch (JSONException e)
        {
            /* Do nothing, this is expected to happen sometimes */
        }
    }

    /**
     * Listener for the delete button
     * When the delete button is pressed, remove the current book from the db
     *
     * @param view The view that this is called from
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
     * Deletes a linked photo from Firebase
     *
     * @param imageUrl A string containing the URL to the image to delete
     */
    private void deletePhotoFromFirebase(String imageUrl)
    {
        /* Only delete the book image from firebase if it was originally stored there. */
        if (!imageUrl.startsWith("https://firebasestorage"))
        {
            return;
        }
        // https://stackoverflow.com/questions/45103085/deleting-file-from-firebase-storage-using-url
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference currentBookPhoto = storage.getReferenceFromUrl(imageUrl);
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
            this.deletePhotoFromFirebase(this.bookToEdit.getImageUrl());
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
                                    bookPhotoDownloadUrl = downloadUrl.toString();
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
