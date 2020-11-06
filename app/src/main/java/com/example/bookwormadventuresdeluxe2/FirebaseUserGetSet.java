package com.example.bookwormadventuresdeluxe2;

/**
 * Class for calling user profile object details from database.
 * Also able to edit FirebaseAuth email and profile contact information
 */

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Firebase writer and reader for UserProfileObject
 */
public class FirebaseUserGetSet
{
    private static Context context = GlobalApplication.getAppContext();
    private static FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    private static CollectionReference usersRef = firebase.collection(context.getString(R.string.users_collection));

    /**
     * Performs query to extract UserProfileObject from database
     *
     * @param username Username of object to be called
     * @param myCallback Interface for returning object after query success
     */
    public static void getUser(String username, UserCallback myCallback)
    {
        usersRef.whereEqualTo("username", username).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                /* Extracting userObject from document */
                                UserProfileObject userObject = new UserProfileObject(
                                                    document.getData().get(context.getString(R.string.firestore_username)).toString(),
                                                    document.getData().get(context.getString(R.string.firestore_email)).toString(),
                                                    document.getData().get(context.getString(R.string.firestore_phoneNumber)).toString(),
                                                    document.getData().get(context.getString(R.string.firestore_userId)).toString(),
                                                    document.getId()
                                                    );

                                /* Returns object after query is complete, avoids null returns while waiting*/
                                myCallback.onCallback(userObject);
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Edits Firebase email of target user with new email
     *
     * @param docId Firebase document ID to user to be targeted
     * @param newEmail New email written
     */
    public static void editEmail(String docId, String newEmail)
    {
        Map<String, Object> data = new HashMap<>();

        data.put(context.getString(R.string.firestore_email), newEmail);

        usersRef.document(docId).update(data);
    }

    /**
     * Edits Firebase phoneNumber of target user with a new phone number
     *
     * @param docId Firebase document ID of user to be targeted
     * @param newPhone New phone number written
     */
    public static void editPhone(String docId, String newPhone)
    {
        Map<String, Object> data = new HashMap<>();

        data.put(context.getString(R.string.firestore_phoneNumber), newPhone);

        usersRef.document(docId).update(data);
    }

    /**
     * Edits FirebaseAuth email and Firebase database email/phone number of user
     *
     * @param inputEmail New email to be written
     * @param inputPhone New phone number to be written
     * @param documentID Document id of target user
     */
    public static void changeAuthInfo(EditText inputEmail, EditText inputPhone, String documentID)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(inputEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            /* Successful profile edit*/
                            editEmail(documentID,
                                    inputEmail.getText().toString().trim());
                            editPhone(documentID,
                                    inputPhone.getText().toString().trim());
                            Log.d(TAG, "User info updated.");
                        }
                        else
                        {
                            try
                            {
                                /* Tries to match errorCode to EditText error */
                                String errorCode = "";
                                errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();

                                switch (errorCode)
                                {
                                    case "ERROR_INVALID_EMAIL":
                                        /* Set Email EditText error code to check validity */
                                        EditTextValidator.invalidEmail(inputEmail);
                                        break;

                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        /* Set Email EditText error code to email taken */
                                        EditTextValidator.emailTaken(inputEmail);
                                        break;

                                    default:
                                        /* Unexpected Error code*/
                                        inputEmail.setError(task.getException().getMessage());
                                }
                            }
                            catch (Exception e)
                            {
                                /* Different type from errorCode, cannot be cast to the same object.
                                 * Sets EditText error to new type.
                                 *
                                 * Log message to debug
                                 */
                                inputEmail.setError(task.getException().getMessage());
                                Log.d(TAG, e.getMessage());
                            }
                        }
                    }
                });
    }

    /**
     * Callback for UserProfileObject
     */
    public interface UserCallback
            /*
             * Source: https://stackoverflow.com/questions/49514859/how-to-get-data-object-from-another-event-android-studio
             * */
    {
        void onCallback(UserProfileObject userObject);
    }
}
