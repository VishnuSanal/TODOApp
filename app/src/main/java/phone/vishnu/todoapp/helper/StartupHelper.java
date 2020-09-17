package phone.vishnu.todoapp.helper;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import phone.vishnu.todoapp.model.Shelve;
import phone.vishnu.todoapp.receiver.NotificationReceiver;
import phone.vishnu.todoapp.viewmodel.ShelveViewModel;

import static android.content.Context.ALARM_SERVICE;

public class StartupHelper {

    private Context context;

    public StartupHelper(Context context) {
        this.context = context;
    }

    public void renewAlarms() {

        List<Shelve> shelveList = new ShelveViewModel((Application) context).getAllShelves().getValue();

        if (shelveList != null) {
            for (Shelve shelve : shelveList) {
                myAlarm(shelve.getDateDue(), shelve.getTitle(), shelve.getDescription());
            }
        }

    }

    private void myAlarm(String timeInMillis, String title, String description) {

        if (!Objects.equals(timeInMillis, "")) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeInMillis));

            if (calendar.getTime().compareTo(new Date()) < 0)
                calendar.add(Calendar.DAY_OF_MONTH, 1);

            String timeString =
                    String.valueOf(calendar.get(Calendar.MINUTE)) +
                            calendar.get(Calendar.HOUR_OF_DAY) +
                            calendar.get(Calendar.DAY_OF_MONTH) +
                            calendar.get(Calendar.MONTH);

            Intent intent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), Integer.parseInt(timeString), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

}
