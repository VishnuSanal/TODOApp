package phone.vishnu.todoapp.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.activity.MainActivity;

public class NotificationHelper {

    private static final String NOTIFICATION_CHANNEL_ID = "phone.vishnu.todoapp";
    private static final String NOTIFICATION_CHANNEL_NAME = "ReminderNotificationChannel";
    private static final int NOTIFICATION_REQUEST_CODE = 2222;
    private final Context mContext;
    private String mTodo;


    NotificationHelper(Context context, String todo) {
        mContext = context;
        mTodo = todo;
    }

    void createNotification() {

        Intent intent = new Intent(mContext, MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext, NOTIFICATION_REQUEST_CODE /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setSmallIcon(R.drawable.ic_drawing)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("TODO")
                .setContentText(mTodo);

        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(NOTIFICATION_REQUEST_CODE /* Request Code */, mBuilder.build());

    }
}