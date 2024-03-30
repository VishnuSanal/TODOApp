package phone.vishnu.todoapp.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import phone.vishnu.todoapp.MainActivity
import phone.vishnu.todoapp.R
import phone.vishnu.todoapp.model.Shelve

class NotificationHelper {

    companion object {
        fun createNotification(context: Context, shelve: Shelve?) {

            val requestCode = shelve?.dateDue!!.toInt()

            val intent =
                Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val pendingIntent =
                PendingIntent.getActivity(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

            val builder =
                NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_drawing)
                    .setContentTitle(shelve.title)
                    .setContentText(shelve.description)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                    .setPriority(NotificationCompat.PRIORITY_MAX)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = "The notifications for TODO App Tasks"
            notificationChannel.setShowBadge(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            builder.setChannelId(Constants.NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)

            notificationManager.notify(requestCode, builder.build())
        }
    }
}
