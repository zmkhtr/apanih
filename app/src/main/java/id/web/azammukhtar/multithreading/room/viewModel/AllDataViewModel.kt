package id.web.azammukhtar.multithreading.room.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import id.web.azammukhtar.multithreading.room.BaseViewModel
import id.web.azammukhtar.multithreading.room.DataDao
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.NonNullMediatorLiveData
import kotlinx.coroutines.launch

class AllDataViewModel constructor(private val dataDao: DataDao) : BaseViewModel() {

    private val _mData = MediatorLiveData<List<DataModel>>()
    val datas: LiveData<List<DataModel>> = _mData

    fun allData() = launch(coroutineContext) {
        _mData.postValue(dataDao.getAllData())
    }

    fun getDataProcess() = launch(coroutineContext) {
        _mData.postValue(dataDao.getProcessData())
    }

    fun getDataSuccess() = launch(coroutineContext) {
        _mData.postValue(dataDao.getSuccessData())
    }

    fun getDataDefect() = launch(coroutineContext) {
        _mData.postValue(dataDao.getDefectData())
    }
    fun deleteAllData() = launch(coroutineContext){
        dataDao.deleteAllData()
//        allData()
    }

    fun deleteData(dataModel: DataModel) = launch(coroutineContext) {
        dataDao.deleteData(dataModel)
    }

    fun updateData(dataModel: DataModel) = launch(coroutineContext){
        dataDao.updateData(dataModel)
//        getDataProcess()
    }

    fun addData(dataModel: DataModel) = launch(coroutineContext) {
//        dataDao.deleteData(dataModel)
        dataDao.insertData(dataModel)
//        getDataProcess()
    }
}