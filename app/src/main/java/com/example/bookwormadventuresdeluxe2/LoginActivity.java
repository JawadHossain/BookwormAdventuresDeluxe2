package com.example.bookwormadventuresdeluxe2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    EditText editTextEmail;
    EditText editTextPassword;
    Button loginButton;
    Button createAccountButton;
    ImageButton visibilityButton;
    ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTheme(R.style.AppTheme);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                currentUser = firebaseAuth.getCurrentUser();

                /* if User exists redirect directly to MyBooks */
                if (currentUser != null)
                {
                    String currentUserId = currentUser.getUid();

                    collectionReference
                            .whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>()
                            {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error)
                                {
                                    if (error != null)
                                    {
                                        return;
                                    }

                                    /* Fill User Credentials */
                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                    {
                                        UserCredentialAPI userCredentialAPI = UserCredentialAPI.getInstance();
                                        userCredentialAPI.setUserId(snapshot.getString("userId"));
                                        userCredentialAPI.setUsername(snapshot.getString("username"));
                                        Intent myBooksIntent = new Intent(LoginActivity.this, MyBooksActivity.class);
                                        startActivity(myBooksIntent);
                                        finish(); // Removes activity from stack so user not brought back here with back button
                                    }
                                }
                            });
                }


            }
        };

        progressBar = findViewById(R.id.login_progressBar);
        editTextEmail = (EditText) findViewById(R.id.login_email);
        editTextPassword = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        createAccountButton = (Button) findViewById(R.id.create_account_button);
        visibilityButton = (ImageButton) findViewById(R.id.visibility_button);

        /* Set the password to hidden by default */
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());

        loginButton.setOnClickListener(this);

        createAccountButton.setOnClickListener(this);

        visibilityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /* Toggle back and forth between visible and hidden password */

                /* Get the cursor start and end index so we can restore the cursor position later */
                int cursorStart = editTextPassword.getSelectionStart();
                int cursorEnd = editTextPassword.getSelectionEnd();

                /* https://stackoverflow.com/questions/24106904/get-drawable-of-image-button */
                if (visibilityButton.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_visibility_24px).getConstantState()))
                {
                    /* Image is currently set to password visible, so set it to hidden */
                    visibilityButton.setImageResource(R.drawable.ic_visibility_off_24px);
                    /* https://stackoverflow.com/questions/3685790/how-to-switch-between-hide-and-view-password */
                    /* Hide the text as well */
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                else
                {
                    /* Image is currently set to password hidden, so set it to visible */
                    visibilityButton.setImageResource(R.drawable.ic_visibility_24px);
                    /* https://stackoverflow.com/questions/3685790/how-to-switch-between-hide-and-view-password */
                    /* Show the text again */
                    editTextPassword.setTransformationMethod(null);
                }

                /* Restore cursor position after hiding/showing the password */
                editTextPassword.setSelection(cursorStart, cursorEnd);
            }
        });
    }

    @Override
    protected void onStart()
    {
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onStart();
    }

    @Override
    protected void onPause()
    {
        if (firebaseAuth != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        super.onPause();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.login_button:
                loginUser(editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString().trim());
                break;
            case R.id.create_account_button:
                /* Override to open Create Account Activity */
                Intent myBooksIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                LoginActivity.this.startActivity(myBooksIntent);
                break;
        }
    }

    public void loginUser(String email, String password)
    {
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        progressBar.setVisibility(View.VISIBLE);

        /* Check email and password parameters*/
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password))
        {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (task.isSuccessful())
                            {
                                String currentUserId = user.getUid();

                                collectionReference
                                        .whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>()
                                        {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error)
                                            {
                                                if (error != null)
                                                {
                                                    return;
                                                }
                                                if (!queryDocumentSnapshots.isEmpty())
                                                {
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots)
                                                    {
                                                        // add to UserCredentialAPI to be accessible throughout app
                                                        UserCredentialAPI userCredentialAPI = UserCredentialAPI.getInstance();
                                                        userCredentialAPI.setUsername(snapshot.getString("username"));
                                                        userCredentialAPI.setUserId(snapshot.getString("userId"));
                                                    }
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    // Go to ListActivity
                                                    startActivity(new Intent(LoginActivity.this, MyBooksActivity.class));
                                                }
                                            }
                                        });
                            }
                            else
                            {
                                String errorCode = "";
                                try
                                {
                                    errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                }
                                catch(Exception e)
                                {
                                    String toomany = task.getException().getMessage();
                                    Toast.makeText(LoginActivity.this, toomany, Toast.LENGTH_LONG).show();
                                }

                                switch(errorCode)
                                {
                                    case "ERROR_WRONG_PASSWORD":
                                        EditTextErrors.wrongPassword(editTextPassword);
                                        break;

                                    case "ERROR_INVALID_EMAIL":
                                        EditTextErrors.emailNotFound(editTextEmail);
                                        break;

                                    default:
                                        break;
                                }
                            }
                        }
                    });
        }
//                            }
//                            if (user != null)
//                            {

//                            else
//                            {
//                                // user not Found
//                                EditTextErrors.emailNotFound(editTextEmail);
//                                progressBar.setVisibility(View.INVISIBLE);
//                                Toast.makeText(LoginActivity.this, "Invalid Credentials. Try Again", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                        }
//                    });
//                    .addOnFailureListener(new OnFailureListener()
//                    {
//                        @Override
//                        public void onFailure(@NonNull Exception e)
//                        {
//                            progressBar.setVisibility(View.INVISIBLE);
//                            Toast.makeText(LoginActivity.this, "Error Logging In " + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });

        else if (TextUtils.isEmpty(email))
        {
            EditTextErrors.isEmpty(editTextEmail);
            return;
        }
        else if (TextUtils.isEmpty(password))
        {
            EditTextErrors.isEmpty(editTextPassword);
            return;
        }
//        else
//        {
//            Toast.makeText(LoginActivity.this, "Missing Credentials", Toast.LENGTH_LONG).show();
//            return;
//        }
    }
}