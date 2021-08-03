package com.vn.iambulance.testingapp;

import static com.vn.iambulance.testingapp.Constant.*;
import android.app.*;
import android.content.*;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.*;
import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Integer userId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("userId")));
            Integer changesCount = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("changesCount")));
            Intent intent = new Intent();
            intent.putExtra(USER_ID_MESSAGE, userId);
            intent.putExtra(CHANGES_COUNT_MESSAGE, changesCount);
            intent.setAction(ACTION_NAME);
            sendBroadcast(intent);
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String title, String content) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "GITHUB";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "GITHUB Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("GITHUB Channel");
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo("Info");
        notificationManager.notify(new Random().nextInt(), builder.build());
    }
}