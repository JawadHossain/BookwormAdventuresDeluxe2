package com.example.bookwormadventuresdeluxe2.NotificationUtility;

/**
 * Monitors FCM token updates
 * Builds notification from received message
 * sources: https://firebase.google.com/docs/cloud-messaging/android/client
 * https://www.youtube.com/watch?v=D9EqQyDSFrI&t=6s&ab_channel=CodingCafe
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.bookwormadventuresdeluxe2.GlobalApplication;
import com.example.bookwormadventuresdeluxe2.LoginActivity;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import static com.example.bookwormadventuresdeluxe2.GlobalApplication.CHANNEL_ID;
import static com.example.bookwormadventuresdeluxe2.GlobalApplication.getAppContext;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    public static final String TAG = "FirebaseMesagingService";

    /**
     * Listen for FCM app registration toke change and update firestore
     *
     * @param token The new token
     */
    @Override
    public void onNewToken(@NonNull String token)
    {
        super.onNewToken(token);
        String currentUserId = getCurrentUserId();
        if (currentUserId != null)
        {
            sendRegistrationToServer(token, currentUserId);
        }
    }

    /**
     * Listen for notification messages
     *
     * @param remoteMessage The notification message received
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        /* Check if notification is intended for logged in user */
        String currentUserId = getCurrentUserId();
        String receiverUserId = remoteMessage.getData().get("ReceiverUserId");
        if (currentUserId != null && currentUserId.equals(receiverUserId))
        {
            showNotification(remoteMessage.getData().get("Title"), remoteMessage.getData().get("Message"));
        }
        else
        {
            Log.d(TAG, "Notification not intended for this device.");
        }
    }

    /**TAG
     * Build and show notification
     *
     * @param title The title of the notification
     * @param body  The title of the body
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showNotification(String title, String body)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // for lower than api 26
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // removes notification on click
                .build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(new Random().nextInt(), notification);
    }

    /**
     * Update Firestore with new token for user
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token, String userId)
    {
        Context context = getAppContext();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.document(context.getString(R.string.users_collection) + "/" + userId)
                .update(context.getString(R.string.firestore_user_token_field), token);
    }

    /**
     * Retrieves current user's Id
     *
     * @return id of the user or null if logged out
     */
    private String getCurrentUserId()
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
        {
            return currentUser.getUid();
        }
        return null;
    }
}
