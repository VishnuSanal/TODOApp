package phone.vishnu.todoapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import phone.vishnu.todoapp.helper.NotificationHelper;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context, intent.getStringExtra("title"), intent.getStringExtra("description"));
        notificationHelper.createNotification();
    }
}