package com.example.bookwormadventuresdeluxe2;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class FirebaseUserGetSet
{
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
                                UserObject userObject = new UserObject(document.getData().get("username").toString(),
                                                    document.getData().get("email").toString(),
                                                    document.getData().get("userId").toString(),
                                                    document.getId());
                                if (document.getData().get("phoneNumber").toString() != null)
                                {
                                    userObject.setPhoneNumber(document.getData().get("phoneNumber").toString());
                                }
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

    public static void editInfo(String docId, String newEmail, String newPhoneNumber)
    {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("email", newEmail);
        data.put("phoneNumber", newPhoneNumber);

        firebase.collection("Users")
                .document(docId)
                .update(data);
    }

    /* https://stackoverflow.com/questions/49514859/how-to-get-data-object-from-another-event-android-studio */
    public interface UserCallback
    {
        void onCallback(UserObject userObject);
    }
}
