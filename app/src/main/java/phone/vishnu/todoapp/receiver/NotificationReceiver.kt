package phone.vishnu.todoapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import phone.vishnu.todoapp.helper.Constants
import phone.vishnu.todoapp.helper.NotificationHelper
import phone.vishnu.todoapp.model.Shelve

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationHelper.createNotification(
            context,
            intent.getSerializableExtra(Constants.SHELVE_INTENT_EXTRA) as? Shelve
        )
    }
}