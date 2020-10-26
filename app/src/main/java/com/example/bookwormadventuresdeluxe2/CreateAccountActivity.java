package com.example.bookwormadventuresdeluxe2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity
{
    private static final String TAG = "CreateAccountActivity";
    private Button createAccountButton;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPhoneNumber;
    private EditText editTextPassword;
    private EditText confirmPassword;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    /* FireStore Connection */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                currentUser = firebaseAuth.getCurrentUser();
            }
        };

        createAccountButton = (Button) findViewById(R.id.create_acount_button_confirm);
        editTextUsername = findViewById(R.id.create_username);
        editTextEmail = findViewById(R.id.create_email);
        editTextPhoneNumber = findViewById(R.id.create_phone_number);
        editTextPassword = findViewById(R.id.create_password);
        confirmPassword = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.create_account_progressBar);

        /* Set click listener to create account*/
        createAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                editTextUsername.setError(null);
                editTextEmail.setError(null);
                editTextPassword.setError(null);
                confirmPassword.setError(null);
                /* Check for Empty EditTexts */
                if (!TextUtils.isEmpty(editTextEmail.getText().toString())
                        && !TextUtils.isEmpty(editTextPassword.getText().toString())
                        && !TextUtils.isEmpty(editTextUsername.getText().toString())
                        && !TextUtils.isEmpty(confirmPassword.getText().toString()))
                {
                    /* Check if passwords match */
                    if (EditTextValidator.passwordsMatch(editTextPassword, confirmPassword))
                    {
                        String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        String username = editTextUsername.getText().toString().trim();
                        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

                        /* Create user if username is not already taken*/
                        checkUsernameAvailability(email, password, username, phoneNumber);

                    }
                }
                else
                {
                    /* Set Empty EditText Error code */
                    if (TextUtils.isEmpty(confirmPassword.getText().toString()))
                    {
                        EditTextValidator.isEmpty(confirmPassword);
                    }
                    if (TextUtils.isEmpty(editTextPassword.getText().toString()))
                    {
                        EditTextValidator.isEmpty(editTextPassword);
                    }
                    if (TextUtils.isEmpty(editTextEmail.getText().toString()))
                    {
                        EditTextValidator.isEmpty(editTextEmail);
                    }
                    if (TextUtils.isEmpty(editTextUsername.getText().toString()))
                    {
                        EditTextValidator.isEmpty(editTextUsername);
                    }

                }
            }
        });
    }

    /**
     * Check if username is available
     * On Success create user
     * On Failure add error to username editText
     * Source: https://stackoverflow.com/questions/48570270/firestore-query-checking-if-username-already-exists
     *
     * @param username Username requiring availability check
     */
    public void checkUsernameAvailability(String email, String password, final String username, String phoneNumber)
    {
        /* Show progress bar */
        progressBar.setVisibility(View.VISIBLE);
        /* Query to find username match*/
        Query userNameQuery = collectionReference.whereEqualTo("username", username);
        userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {

                    boolean isUsernameInUse = false;

                    /* Search for documents containing username */
                    for (DocumentSnapshot document : task.getResult())
                    {
                        if (document.exists())
                        {
                            /* Set variable to true if username exists */
                            isUsernameInUse = true;
                            break;
                        }
                    }
                    /* Create User if username is available*/
                    if (!isUsernameInUse)
                    {
                        createUser(email, password, username, phoneNumber);
                    }
                    else
                    {
                        /* Hide progressBar and set error in username editText*/
                        progressBar.setVisibility(View.INVISIBLE);
                        EditTextValidator.usernameTaken(editTextUsername);
                    }
                }
                else
                {
                    Log.d("CreateAccountActivity", "Error getting documents"
                            + "in checkUsernameAvailability: ", task.getException());
                }
            }
        });
    }

    /**
     * Attempt to create an user
     * Take to MyBooksActivity on success
     * Show error message on failure
     * ProgressBar visibility set to Invisible inside nested calls due to asynchronous firebase methods
     *
     * @param email
     * @param password
     * @param username
     * @param phoneNumber
     */
    public void createUser(String email, String password, final String username, String phoneNumber)
    {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username))
        {

            /* Create Firebase User */
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                // Take user to My Books
                                currentUser = firebaseAuth.getCurrentUser();
                                assert currentUser != null;

                                if (currentUser != null)
                                {
                                    final String currentUserId = currentUser.getUid();

                                    // Create new User object with credentials
                                    Map<String, String> newUser = new HashMap<>();
                                    newUser.put("userId", currentUserId);
                                    newUser.put("email", email);
                                    newUser.put("username", username);
                                    newUser.put("phoneNumber", phoneNumber);

                                    // Save new user to Firestore
                                    collectionReference.add(newUser)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>()
                                            {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference)
                                                {
                                                    documentReference.get()
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                                            {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                                                {
                                                                    if (task.getResult().exists())
                                                                    {
                                                                        /* Hide progress bar */
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        String name = task.getResult()
                                                                                .getString("username");

                                                                        /* Store User credentials in the global API*/
                                                                        UserCredentialAPI userCredentialAPI = UserCredentialAPI.getInstance();
                                                                        userCredentialAPI.setUserId(currentUserId);
                                                                        userCredentialAPI.setUsername(name);

                                                                        /* Take user to My Books Activity */
                                                                        Intent intent = new Intent(CreateAccountActivity.this,
                                                                                MyBooksActivity.class);
                                                                        startActivity(intent);

                                                                    }
                                                                    else
                                                                    {
                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                    }
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener()
                                            {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    Toast.makeText(CreateAccountActivity.this, "Failed to save user " + e.getMessage(), Toast.LENGTH_LONG)
                                                            .show();
                                                }
                                            });
                                }
                            }
                            else
                            {

                                /* Set EditText Error type from errorCode */
                                try
                                {
                                    /* Extract Firebase Error Code */
                                    String errorCode = "";
                                    errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                    switch (errorCode)
                                    {
                                        case "ERROR_INVALID_EMAIL":
                                            /* Set Email EditText error code and additionally check password eligibility */
                                            EditTextValidator.weakPass(editTextPassword, confirmPassword);
                                            EditTextValidator.invalidEmail(editTextEmail);
                                            break;

                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                        EditTextValidator.weakPass(editTextPassword, confirmPassword);
                                        EditTextValidator.emailTaken(editTextEmail);
                                        break;

                                        case "ERROR_WEAK_PASSWORD":
                                            EditTextValidator.weakPass(editTextPassword, confirmPassword);
                                            break;

                                        default:
                                            /* Unexpected Error code*/
                                            throw new Exception("Unexpected Firebase Error Code"
                                                    + "inside click listener.");
                                    }
                                    /* Hide progress bar*/
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                                catch (Exception e)
                                {
                                    /* Log message to debug*/
                                    Log.d(TAG, "Unexpected Firebase Error code: " + e.getMessage());
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Set Current User and attach Auth state listener on Start of Activity
     */
    @Override
    protected void onStart()
    {
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }

    /**
     * Detach Auth State Listener on idle Activity
     */
    @Override
    protected void onPause()
    {
        if (firebaseAuth != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        super.onPause();
    }

}