package phone.vishnu.todoapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import phone.vishnu.todoapp.model.Shelve;

@Dao
public interface ShelveDao {

    @Insert
    void insert(Shelve shelve);

    @Update
    void update(Shelve shelve);

    @Delete
    void delete(Shelve shelve);

    @Query("SELECT * FROM Shelve order by id desc")
    LiveData<List<Shelve>> getAllShelves();
}
