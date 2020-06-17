package phone.vishnu.todoapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import phone.vishnu.todoapp.dao.ShelveDao;
import phone.vishnu.todoapp.database.ShelveDatabase;
import phone.vishnu.todoapp.model.Shelve;

public class ShelveRepository {

    private ShelveDao shelveDao;
    private LiveData<List<Shelve>> shelvesList;

    public ShelveRepository(Application application) {
        ShelveDatabase database = ShelveDatabase.getInstance(application);
        shelveDao = database.shelveDao();
        shelvesList = shelveDao.getAllShelves();
    }

    public void insertShelve(Shelve shelve) {
        new InsertShelveAsyncTask(shelveDao).execute(shelve);
    }

    public void updateShelve(Shelve shelve) {
        new UpdateShelveAsyncTask(shelveDao).execute(shelve);
    }

    public void deleteShelve(Shelve shelve) {
        new DeleteShelveAsyncTask(shelveDao).execute(shelve);
    }

    public LiveData<List<Shelve>> getAllShelves() {
        return shelvesList;
    }

    private static class InsertShelveAsyncTask extends AsyncTask<Shelve, Void, Void> {
        private ShelveDao shelveDao;

        public InsertShelveAsyncTask(ShelveDao shelveDao) {
            this.shelveDao = shelveDao;
        }

        @Override
        protected Void doInBackground(Shelve... shelves) {
            shelveDao.insert(shelves[0]);
            return null;
        }
    }

    private static class UpdateShelveAsyncTask extends AsyncTask<Shelve, Void, Void> {
        private ShelveDao shelveDao;

        public UpdateShelveAsyncTask(ShelveDao shelveDao) {
            this.shelveDao = shelveDao;
        }

        @Override
        protected Void doInBackground(Shelve... shelves) {
            shelveDao.update(shelves[0]);
            return null;
        }
    }

    private static class DeleteShelveAsyncTask extends AsyncTask<Shelve, Void, Void> {
        private ShelveDao shelveDao;

        public DeleteShelveAsyncTask(ShelveDao shelveDao) {
            this.shelveDao = shelveDao;
        }

        @Override
        protected Void doInBackground(Shelve... shelves) {
            shelveDao.delete(shelves[0]);
            return null;
        }
    }
}
