package phone.vishnu.todoapp.helpers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.activity.MainActivity;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;

class NotificationHelper {

    private static final String NOTIFICATION_CHANNEL_ID = "phone.vishnu.todoapp";
    private static final String NOTIFICATION_CHANNEL_NAME = "ReminderNotificationChannel";
    private final Context mContext;
    private String mTodo;

    NotificationHelper(Context context, String todo) {
        mContext = context;
        mTodo = todo;
    }

    void createNotification() {

        Calendar calendar = Calendar.getInstance();
        String timeString =
                String.valueOf(calendar.get(Calendar.MINUTE)) +
                        calendar.get(Calendar.HOUR_OF_DAY) +
                        calendar.get(Calendar.DAY_OF_MONTH) +
                        calendar.get(Calendar.MONTH);

        int NOTIFICATION_REQUEST_CODE = Integer.parseInt(timeString);

        Intent intent = new Intent(mContext, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_CODE, intent, 0);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.ic_drawing)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("TODO")
                .setContentText(mTodo)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX); //Important for heads-up notification
        ;

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            notificationChannel.setDescription("This is the notification channel for TODO Alarms");
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_REQUEST_CODE, mBuilder.build());

    }
}