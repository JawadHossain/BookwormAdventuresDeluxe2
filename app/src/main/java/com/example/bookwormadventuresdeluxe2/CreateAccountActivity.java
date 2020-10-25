package com.example.bookwormadventuresdeluxe2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

// Todo: Add conditional to check confirmPasswordEditText
public class CreateAccountActivity extends AppCompatActivity
{
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

        createAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                editTextUsername.setError(null);
                editTextEmail.setError(null);
                editTextPassword.setError(null);
                confirmPassword.setError(null);
                // Check fields
                if (!TextUtils.isEmpty(editTextEmail.getText().toString())
                        && !TextUtils.isEmpty(editTextPassword.getText().toString())
                        && !TextUtils.isEmpty(editTextUsername.getText().toString())
                        && !TextUtils.isEmpty(confirmPassword.getText().toString()))
                {
                    if (EditTextErrors.passwordsMatch(editTextPassword, confirmPassword))
                    {
                        String email = editTextEmail.getText().toString().trim();
                        String password = editTextPassword.getText().toString().trim();
                        String username = editTextUsername.getText().toString().trim();
                        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

                        createUser(email, password, username, phoneNumber);
                    }
                }
                else
                {
                    if (TextUtils.isEmpty(confirmPassword.getText().toString()))
                    {
                        EditTextErrors.isEmpty(confirmPassword);
                    }
                    if (TextUtils.isEmpty(editTextPassword.getText().toString()))
                    {
                        EditTextErrors.isEmpty(editTextPassword);
                    }
                    if (TextUtils.isEmpty(editTextEmail.getText().toString()))
                    {
                        EditTextErrors.isEmpty(editTextEmail);
                    }
                    if (TextUtils.isEmpty(editTextUsername.getText().toString()))
                    {
                        EditTextErrors.isEmpty(editTextUsername);
                    }





//                    Toast.makeText(CreateAccountActivity.this, "Please Enter All Fields",
//                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void createUser(String email, String password, final String username, String phoneNumber)
    {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username))
        {

            progressBar.setVisibility(View.VISIBLE);

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

                                    // Add user to users collection in FireStore
                                    Map<String, String> newUser = new HashMap<>();
                                    newUser.put("userId", currentUserId);
                                    newUser.put("email", email);
                                    newUser.put("username", username);
                                    newUser.put("phoneNumber", phoneNumber);

                                    // Save user to db
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

                                                                        progressBar.setVisibility(View.INVISIBLE);
                                                                        String name = task.getResult()
                                                                                .getString("username");

                                                                        /* Store User credentials in the global API*/
                                                                        UserCredentialAPI userCredentialAPI = UserCredentialAPI.getInstance();
                                                                        userCredentialAPI.setUserId(currentUserId);
                                                                        userCredentialAPI.setUsername(name);

                                                                        Intent intent = new Intent(CreateAccountActivity.this,
                                                                                MyBooksActivity.class);
                                                                        intent.putExtra("username", name);
                                                                        intent.putExtra("userId", currentUserId);
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
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                Toast.makeText(CreateAccountActivity.this, errorCode, Toast.LENGTH_LONG).show();

                                switch (errorCode)
                                {
                                    case "ERROR_INVALID_EMAIL":
                                        EditTextErrors.invalidEmail(editTextEmail);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        EditTextErrors.emailTaken(editTextEmail);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                        EditTextErrors.weakPass(editTextPassword, confirmPassword);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        break;





                                    default:
                                        break;
                                }
                            }
                        }
                    });
        }
    }
//                            else
//                            {
//                                // Show Error Message
//                                Toast.makeText(CreateAccountActivity.this, "Error Creating User", Toast.LENGTH_LONG).show();
//
//                            }
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener()
//                    {
//
//                        @Override
//                        public void onFailure(@NonNull Exception e)
//                        {
//                            Toast.makeText(CreateAccountActivity.this, "Failed to save user", Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//
//
//        }
//        else
//        {
//            // Empty fields not allowed
//            Toast.makeText(CreateAccountActivity.this, "Missing Credentials", Toast.LENGTH_LONG)
//                    .show();
//        }


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

}