package com.example.notespro;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        Intent service = new Intent(context, NotificationService.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(service);
//        } else {
//            context.startService(service);
//        }


            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            Toast.makeText(context, "Alarm Receiveed", Toast.LENGTH_SHORT).show();
            Log.e("Alarm Received","Received");

            // Create an intent for the MainActivity to open when the notification is clicked
            Intent nextIntent = new Intent(context, openDialogInterface.class);
            nextIntent.putExtra("title",title);
            nextIntent.putExtra("content",content);
            nextIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            int uniqueId=generateUniqueID();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, uniqueId, nextIntent,  PendingIntent.FLAG_MUTABLE);


            // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = context.getString(R.string.default_notification_channel_id);
            CharSequence channelName = context.getString(R.string.default_notification_channel_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription("This is Used to Remember the Tasks Developed By Praveen");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.BigTextStyle bigTextStyle=new NotificationCompat.BigTextStyle()
                .bigText(content);

            // Build the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.drawable.todologo)
                    .setContentTitle(title+" - "+"ToDoWise")
                    .setContentText(content)
                    .setStyle(bigTextStyle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                    .setVibrate(new long[]{0, 100, 200, 300})
                    .setContentIntent(pendingIntent); // Set the pending intent

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            // Check for notification permission (if needed)
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Handle the missing permission case here
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }

            // Show the notification
            try {
                notificationManagerCompat.notify(0, builder.build());
            } catch (Exception e) {
                Toast.makeText(context, "Notification error: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
    }


    private int generateUniqueID() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}
