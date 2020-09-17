package phone.vishnu.todoapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import phone.vishnu.todoapp.helper.StartupHelper;

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction() != null) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

                try {
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            StartupHelper startupHelper = new StartupHelper(context);
                            startupHelper.renewAlarms();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
