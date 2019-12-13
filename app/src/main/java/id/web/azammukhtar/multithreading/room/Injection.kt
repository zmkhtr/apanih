package id.web.azammukhtar.multithreading.room

import android.content.Context

object Injection {

    private fun provideTaskDataSource(context: Context): DataDao {
        val database = LocalDatabase.getInstance(context)
        return database.taskDao()
    }

    fun provideViewModelFactory(context: Context): ViewModelFactory {
        val dataDao = provideTaskDataSource(context)
        return ViewModelFactory(dataDao)
    }
}