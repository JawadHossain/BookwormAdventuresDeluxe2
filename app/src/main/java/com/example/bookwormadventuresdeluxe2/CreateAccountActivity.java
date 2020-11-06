/**
 * CreateAccountActivity.java
 *
 * Activity for creating account. Requires all fields filled in,
 * matching passwords and password length greater than 6
 * characters to successfully create an account. Cannot overwrite
 * an account username or email that already exists. After account
 * creation, it automatically opens MyBooksActivity.
 */

package com.example.bookwormadventuresdeluxe2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private ImageButton backButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseUser currentUser;

    /* FireStore Connection */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        TextView appHeaderTitle = findViewById(R.id.app_header_title);
        appHeaderTitle.setText(R.string.create_account);

        collectionReference = db.collection(getString(R.string.users_collection));
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
        backButton = findViewById(R.id.app_header_back_button);
        editTextUsername = findViewById(R.id.create_username);
        editTextEmail = findViewById(R.id.create_email);
        editTextPhoneNumber = findViewById(R.id.create_phone_number);
        editTextPassword = findViewById(R.id.create_password);
        confirmPassword = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.create_account_progressBar);

        /* Back Button Click Listener */
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this::onBackClick);

        createAccount();
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

    /**
     * CreateAccountButton functionality
     */
    private void createAccount()
    {
        /* Set click listener to create account*/
        createAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /* Check if there are no empty fields*/
                if (!EditTextValidator.createAccountEmpties(editTextUsername, editTextEmail, editTextPhoneNumber,
                        editTextPassword, confirmPassword))
                {
                    /* Check is passwords match and long enough, display error if not*/
                    if (EditTextValidator.passwordsMatch(editTextPassword, confirmPassword)
                            && !EditTextValidator.weakPass(editTextPassword, confirmPassword))
                    {
                        /* Show progress bar */
                        progressBar.setVisibility(View.VISIBLE);

                        /* Checks is username and email is valid and passes it to createUser for errors*/
                        checkUsernameEmailAvailability(editTextUsername.getText().toString().trim(),
                                editTextEmail.getText().toString().trim(),
                                editTextPassword.getText().toString().trim());
                    }
                }
            }
        });
    }

    /**
     * Checker for valid account creation parameters. Goes into CreateUser
     *
     * @param username Input username to be checked
     * @param email Input email to be checked
     * @param password Password of account to be created
     */
    private void checkUsernameEmailAvailability(String username, String email, String password)
    {
        /* Show progress bar */
        progressBar.setVisibility(View.VISIBLE);

        /* Query to find username match*/
        Query userNameQuery = collectionReference.whereEqualTo("username", username);
        Query emailQuery = collectionReference.whereEqualTo("email", email);

        final boolean[] isUsernameInUse = {false};  // Declared final as it is accessed in inner class
        final boolean[] isEmailInUse = {false};

        /* Create User if email and username not in use*/
        emailQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if (task.isSuccessful())
                {
                    /* Search for documents containing email */
                    for (DocumentSnapshot document : task.getResult())
                    {
                        if (document.exists())
                        {
                            /* Set variable to true if email exists */
                            isEmailInUse[0] = true;
                            break;
                        }
                    }
                    /*  Create user if username is available */
                    userNameQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if (task.isSuccessful())
                            {
                                /* Search for documents containing username */
                                for (DocumentSnapshot document : task.getResult())
                                {
                                    if (document.exists())
                                    {
                                        /* Set variable to true if username exists */
                                        isUsernameInUse[0] = true;
                                        break;
                                    }
                                }
                                /* All valid input, create account */
                                if (!isEmailInUse[0] && !isUsernameInUse[0] && isEmailValid(email))
                                {
                                    createUser(username, email, password);
                                }
                                else
                                {
                                    /* Set Edit Text errors*/
                                    if (isEmailInUse[0])
                                    {
                                        EditTextValidator.emailTaken(editTextEmail);
                                    }
                                    if (!isEmailValid(email))
                                    {
                                        EditTextValidator.invalidEmail(editTextEmail);
                                    }
                                    if (isUsernameInUse[0])
                                    {
                                        EditTextValidator.usernameTaken(editTextUsername);
                                    }
                                    /* Hide progressBar*/
                                    progressBar.setVisibility(View.INVISIBLE);
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
     * @param username Username to be created
     * @param email Email to be created
     * @param password Password to be created
     */
    private void createUser(String username, String email, String password)
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
                            /* Successful create account*/
                            currentUser = firebaseAuth.getCurrentUser();
                            assert currentUser != null;

                            /* Store User credentials in the global API*/
                            UserCredentialAPI userCredentialAPI = UserCredentialAPI.getInstance();
                            userCredentialAPI.setUserId(currentUser.getUid());
                            userCredentialAPI.setUsername(username);

                            /* Create firebase database profile */
                            createFirebaseAccount(currentUser.getUid());

                            /* Take user to My Books Activity */
                            Intent intent = new Intent(CreateAccountActivity.this,
                                    MyBooksActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            try
                            {
                                /* Extract Firebase Error Code */
                                String errorCode = "";
                                errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                editTextUsername.setError(errorCode);
                                editTextUsername.requestFocus();
                            } catch (Exception e)
                            {
                                /* Different type from errorCode, cannot be cast to the same object.
                                 * Sets EditText error to new type.
                                 *
                                 * Log message to debug
                                 */
                                editTextUsername.setError(task.getException().getMessage());
                                editTextUsername.requestFocus();
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    }
                });

        /* Hide progress bar*/
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Creates Firebase database account for user with their information
     *
     * @param userId taken from FirebaseAuth instance
     */
    private void createFirebaseAccount(String userId)
    {
        /* Create new User object with credentials */
        Map<String, String> newUser = new HashMap<>();
        newUser.put("userId", userId);
        newUser.put("email", editTextEmail.getText().toString().trim());
        newUser.put("username", editTextUsername.getText().toString().trim());
        newUser.put("phoneNumber", editTextPhoneNumber.getText().toString());

        /* Save new user to Firestore */
        collectionReference.add(newUser);
    }

    /**
     * Method for checking valid email format.
     *
     * @param email Email string to be checked
     * @return boolean true for valid false for invalid
     */
    private boolean isEmailValid(String email)
    {
        /* Source: https://stackoverflow.com/questions/6119722/how-to-check-edittexts-text-is-email-address-or-not*/
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Take user to login screen on back click
     *
     * @param view View
     */
    private void onBackClick(View view)
    {
        super.onBackPressed();
    }
}