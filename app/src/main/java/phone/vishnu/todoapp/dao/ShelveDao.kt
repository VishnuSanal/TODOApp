package phone.vishnu.todoapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import phone.vishnu.todoapp.model.Shelve

@Dao
interface ShelveDao {
    @Insert
    fun insert(shelve: Shelve)

    @Update
    fun update(shelve: Shelve)

    @Delete
    fun delete(shelve: Shelve)

    @Query("SELECT * FROM Shelve order by dateDue desc")
    fun getAllShelves(): LiveData<List<Shelve>>
}