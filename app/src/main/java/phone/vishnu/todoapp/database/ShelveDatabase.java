package phone.vishnu.todoapp.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import phone.vishnu.todoapp.dao.ShelveDao;
import phone.vishnu.todoapp.model.Shelve;


@Database(entities = {Shelve.class}, version = 1)
public abstract class ShelveDatabase extends RoomDatabase {

    private static ShelveDatabase instance;
    private static RoomDatabase.Callback callback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    public static synchronized ShelveDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(context.getApplicationContext(), ShelveDatabase.class, "shelve_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build();
        }

        return instance;
    }

    public abstract ShelveDao shelveDao();

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void, Void> {
        private ShelveDao shelveDao;

        public PopulateDBAsyncTask(ShelveDatabase database) {
            this.shelveDao = database.shelveDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
