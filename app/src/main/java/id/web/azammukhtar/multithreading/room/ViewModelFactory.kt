package id.web.azammukhtar.multithreading.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.web.azammukhtar.multithreading.room.viewModel.AllDataViewModel
import id.web.azammukhtar.multithreading.room.viewModel.CreateNewDataViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val dataDao: DataDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllDataViewModel::class.java))
            return AllDataViewModel(dataDao) as T
        else if(modelClass.isAssignableFrom(CreateNewDataViewModel::class.java))
            return CreateNewDataViewModel(dataDao) as T
        throw IllegalArgumentException("Unknown View Model class")
    }
}