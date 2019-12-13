package id.web.azammukhtar.multithreading.room.viewModel

import id.web.azammukhtar.multithreading.room.BaseViewModel
import id.web.azammukhtar.multithreading.room.DataDao
import id.web.azammukhtar.multithreading.room.DataModel
import kotlinx.coroutines.launch
import timber.log.Timber

class CreateNewDataViewModel constructor(private val dataDao: DataDao) : BaseViewModel() {

    fun addData(dataModel: DataModel) = launch(coroutineContext) {
        dataDao.insertData(dataModel)
        Timber.d("VIN %s", dataModel.vin)
    }
}