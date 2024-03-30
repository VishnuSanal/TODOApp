package phone.vishnu.todoapp.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import phone.vishnu.todoapp.model.Shelve
import phone.vishnu.todoapp.receiver.NotificationReceiver

class AlarmHelper {

    companion object {

        fun setAlarm(context: Context, shelve: Shelve) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            if (alarmManager != null) try {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, shelve.dateDue, getPendingIntent(context, shelve)
                )
            } catch (e: SecurityException) {
                Log.e("vishnu", "setAlarm: setting alarm failed $shelve", e)
            }
        }

        fun cancelAlarm(context: Context, shelve: Shelve) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

            if (alarmManager != null) alarmManager.cancel(
                getPendingIntent(context, shelve)
            )
        }

        private fun getPendingIntent(context: Context, shelve: Shelve): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                shelve.dateDue.toInt(),
                Intent(context, NotificationReceiver::class.java).putExtra(
                    Constants.SHELVE_INTENT_EXTRA, shelve
                ),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}