package phone.vishnu.todoapp.application

import android.app.Application
import phone.vishnu.todoapp.database.ShelveDatabase
import phone.vishnu.todoapp.repository.ShelveRepository

class Application : Application() {
    private val database by lazy { ShelveDatabase.getDatabase(this) }
    val repository by lazy { ShelveRepository(database.shelveDao()) }
}