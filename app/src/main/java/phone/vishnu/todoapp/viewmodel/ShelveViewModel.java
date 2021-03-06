package phone.vishnu.todoapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import phone.vishnu.todoapp.model.Shelve;
import phone.vishnu.todoapp.repository.ShelveRepository;

public class ShelveViewModel extends AndroidViewModel {

    ShelveRepository repository;
    LiveData<List<Shelve>> allShelves;

    public ShelveViewModel(@NonNull Application application) {
        super(application);
        repository = new ShelveRepository(application);
        allShelves = repository.getAllShelves();
    }

    public void insert(Shelve shelve) {
        repository.insertShelve(shelve);
    }

    public void delete(Shelve shelve) {
        repository.deleteShelve(shelve);
    }

    public void update(Shelve shelve) {
        repository.updateShelve(shelve);
    }

    public LiveData<List<Shelve>> getAllShelves() {
        return allShelves;
    }

    public ArrayList<String> getAllTitles() {
        ArrayList<String> allTitles = new ArrayList<>();

        for (Shelve shelve : Objects.requireNonNull(allShelves.getValue())) {
            allTitles.add(shelve.getTitle());
        }

        return allTitles;
    }
}
