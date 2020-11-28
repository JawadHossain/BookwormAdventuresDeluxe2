package com.example.bookwormadventuresdeluxe2.Utilities;

/**
 * Class for calling user profile object details from database.
 * Also able to edit FirebaseAuth email and profile contact information
 */

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.bookwormadventuresdeluxe2.Models.User;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Firebase writer and reader for User
 */
public class FirebaseUserGetSet
{
    private static Context context = GlobalApplication.getAppContext();
    private static FirebaseFirestore firebase = FirebaseFirestore.getInstance();
    private static CollectionReference usersRef = firebase.collection(context.getString(R.string.users_collection));

    /**
     * Performs query to extract User from database
     *
     * @param username   Username of object to be called
     * @param myCallback Interface for returning object after query success
     */
    public static void getUser(String username, UserCallback myCallback)
    {
        usersRef.whereEqualTo(context.getString(R.string.firestore_username), username).get()
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
                                User userObject = new User(
                                        document.getData().get(context.getString(R.string.firestore_username)).toString(),
                                        document.getData().get(context.getString(R.string.firestore_email)).toString(),
                                        document.getData().get(context.getString(R.string.firestore_phoneNumber)).toString(),
                                        document.getData().get(context.getString(R.string.firestore_userId)).toString(),
                                        document.getId(),
                                        document.getString("token")
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
     * @param docId    Firebase document ID to user to be targeted
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
     * @param docId    Firebase document ID of user to be targeted
     * @param newPhone New phone number written
     */
    public static void editPhone(String docId, String newPhone)
    {
        Map<String, Object> data = new HashMap<>();

        data.put(context.getString(R.string.firestore_phoneNumber), newPhone);

        usersRef.document(docId).update(data);
    }

    /**
     * Checks if email is currently in use
     *
     * @param email              Email to be checked
     * @param emailCheckCallback Callback for timing return
     */
    public static void checkEmailExists(EditText email, EmailCheckCallBack emailCheckCallback)
    {
        usersRef.whereEqualTo(context.getString(R.string.firestore_email), email.getText().toString().trim())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
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
                            emailCheckCallback.onCallback(true);
                        }
                    }
                }
                if (task.getResult().size() == 0)
                {
                    /* False if it does not exist */
                    emailCheckCallback.onCallback(false);
                }
            }
        });
    }

    /**
     * Edits FirebaseAuth email and Firebase database email/phone number of user
     *
     * @param inputEmail   New email to be written
     * @param documentID   Document id of target user
     * @param editCallback Callback for waiting for result
     */
    public static void changeEmail(EditText inputEmail, String documentID, EditCallback editCallback)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(inputEmail.getText().toString().trim())
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
                            editCallback.onCallback(true);
                            Log.d(TAG, "User info updated.");
                        }
                        else
                        {
                            try
                            {
                                /* Failed edit */
                                String errorCode = "";
                                errorCode = ((FirebaseAuthException) task.getException()).getMessage();
                                inputEmail.setError(errorCode);
                                inputEmail.requestFocus();
                            } catch (Exception e)
                            {
                                /* Different type of error, cannot be cast to the same object.
                                 * Sets EditText error to new type.
                                 *
                                 * Log message to debug
                                 */
                                inputEmail.setError(task.getException().getMessage());
                                inputEmail.requestFocus();
                                Log.d(TAG, e.getMessage());
                            }
                            editCallback.onCallback(false);
                            Log.d(TAG, "User info update failed.");
                        }
                    }});
    }

    /**
     * Increment User notification count
     *
     * @param userId The user ID of the user
     */
    public static void incrementNotificationCount(String userId)
    {
        FirebaseFirestore.getInstance().collection(context.getString(R.string.users_collection))
                .document(userId)
                .update(context.getString(R.string.firestore_user_notification_count_field), FieldValue.increment(1));
    }

    /**
     * Reset User notification Count and Delete Notifications
     *
     * @param userId The user ID of the user
     */
    public static void resetNotifications(String userId)
    {
        /* Update UserCredentialAPI*/
        UserCredentialAPI.getInstance().setNotificationCount(null);
        /* set notification count field to null */
        firebase.collection(context.getString(R.string.users_collection))
                .document(userId)
                .update(context.getString(R.string.firestore_user_notification_count_field), null);

        /**
         *  delete all documents in collection one at a time as firestore doesn't support
         *  deleting entire collection in android
         *  */
        firebase
                .collection(context.getString(R.string.users_collection) + "/"
                        + userId + "/" + context.getString(R.string.notifications_collection))
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
                                String docID = document.getId();
                                firebase
                                        .document(context.getString(R.string.users_collection)
                                                + "/" + userId + "/" + context.getString(R.string.notifications_collection)
                                                + "/" + docID)
                                        .delete();
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents" + task.getException());
                        }
                    }
                });
    }

    /**
     * Callback for User
     */
    public interface UserCallback
    /*
     * Source: https://stackoverflow.com/questions/49514859/how-to-get-data-object-from-another-event-android-studio
     * */
    {
        void onCallback(User userObject);
    }

    /**
     * Callback for editing profile result
     */
    public interface EditCallback
    /*
     * Source: https://stackoverflow.com/questions/49514859/how-to-get-data-object-from-another-event-android-studio
     * */
    {
        void onCallback(Boolean result);
    }

    /**
     * Callback for checking email existence
     */
    public interface EmailCheckCallBack
    /*
     * Source: https://stackoverflow.com/questions/49514859/how-to-get-data-object-from-another-event-android-studio
     * */
    {
        void onCallback(Boolean result);
    }
}
