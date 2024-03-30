package phone.vishnu.todoapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import phone.vishnu.todoapp.model.Shelve
import phone.vishnu.todoapp.repository.ShelveRepository

class MainViewModel(private val repository: ShelveRepository) : ViewModel() {

    private val shelvesList: LiveData<List<Shelve>> = repository.getAllShelves()

    fun insert(shelve: Shelve) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(shelve)
    }

    fun delete(shelve: Shelve) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(shelve)
    }

    fun update(shelve: Shelve) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(shelve)
    }

    fun getAllShelves(): LiveData<List<Shelve>> {
        return shelvesList
    }
}

class MainViewModelFactory(private val repository: ShelveRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}