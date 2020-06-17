package phone.vishnu.todoapp.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.activity.MainActivity;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;

public class NotificationHelper {

    private static final String NOTIFICATION_CHANNEL_ID = "phone.vishnu.shelvestodo";
    private static final String NOTIFICATION_CHANNEL_NAME = "TODONotificationChannel";
    private final Context context;
    private String title, description;

    public NotificationHelper(Context context, String title, String description) {
        this.context = context;
        this.title = title;
        this.description = description;
    }

    public void createNotification() {

        Calendar calendar = Calendar.getInstance();
        String timeString =
                String.valueOf(calendar.get(Calendar.MINUTE)) +
                        calendar.get(Calendar.HOUR_OF_DAY) +
                        calendar.get(Calendar.DAY_OF_MONTH) +
                        calendar.get(Calendar.MONTH);

        int NOTIFICATION_REQUEST_CODE = Integer.parseInt(timeString);

        Intent intent = new Intent(context, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent resultPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, intent, 0);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.bigText(description);


        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_drawing)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setStyle(bigTextStyle)
                .setContentTitle(title)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setPriority(Notification.PRIORITY_MAX);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.setDescription("This is the notification channel for TODO Reminders");
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_REQUEST_CODE, mBuilder.build());

    }
}