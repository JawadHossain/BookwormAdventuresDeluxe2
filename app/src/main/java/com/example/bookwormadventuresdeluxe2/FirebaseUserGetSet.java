package com.example.bookwormadventuresdeluxe2;

import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
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
    /**
     * Performs query to extract UserProfileObject from database
     *
     * @param username username of object to be called
     * @param myCallback interface for returning object after query success
     */
    public static void getUser(String username, UserCallback myCallback)
    {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        firebase.collection("Users")
                .whereEqualTo("username", username)
                .get()
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
                                                    document.getData().get("username").toString(),
                                                    document.getData().get("email").toString(),
                                                    document.getData().get("userId").toString(),
                                                    document.getId()
                                                    );

                                if (document.getData().get("phoneNumber").toString() != null)
                                {
                                    /* Adding phone number to userObject if it exists */
                                    userObject.setPhoneNumber(document.getData().get("phoneNumber").toString());
                                }

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
     * Edits firebase email of target user with new email
     *
     * @param docId Firebase document ID to user to be targeted
     * @param newEmail New email written
     */
    public static void editEmail(String docId, String newEmail)
    {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("email", newEmail);

        firebase.collection("Users")
                .document(docId)
                .update(data);
    }

    /**
     * Edits firebase phoneNumber of target user with a new email
     *
     * @param docId Firebase document ID of user to be targeted
     * @param newPhone New phone number written
     */
    public static void editPhone(String docId, String newPhone)
    {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("phoneNumber", newPhone);

        firebase.collection("Users")
                .document(docId)
                .update(data);
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

    /**
     * Edits FirebaseAuth email and Firebase database email/phone number of user
     *
     * @param inputEmail New email to be written
     * @param inputPhone New phone number to be written
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
                            FirebaseUserGetSet.editEmail(documentID,
                                    inputEmail.getText().toString());
                            FirebaseUserGetSet.editPhone(documentID,
                                    inputPhone.getText().toString());
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
                                        throw new Exception("Unexpected Firebase Error Code"
                                                + "inside click listener.");
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
}
