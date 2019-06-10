package phone.vishnu.todoapp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {


            NotificationManagerCompat myManager = NotificationManagerCompat.from(ctx);

            NotificationCompat.Builder myNoti = new NotificationCompat.Builder(ctx)
                    .setContentTitle("")
                    .setContentText("")
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_drawing);

            Intent i1 = new Intent(ctx, MainActivity.class);
            PendingIntent pd = PendingIntent.getActivity(ctx, 1, i1, 0);
            myNoti.setContentIntent(pd);

            myManager.notify(1, myNoti.build());

        }
    }
}