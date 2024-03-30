package phone.vishnu.todoapp.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import phone.vishnu.todoapp.dao.ShelveDao
import phone.vishnu.todoapp.model.Shelve

class ShelveRepository(private val shelveDao: ShelveDao) {

    private val shelvesList: LiveData<List<Shelve>> = shelveDao.getAllShelves()

    @WorkerThread
    suspend fun insert(shelve: Shelve) {
        shelveDao.insert(shelve)
    }

    @WorkerThread
    suspend fun delete(shelve: Shelve) {
        shelveDao.delete(shelve)
    }

    @WorkerThread
    suspend fun update(shelve: Shelve) {
        shelveDao.update(shelve)
    }

    fun getAllShelves(): LiveData<List<Shelve>> {
        return shelvesList
    }
}