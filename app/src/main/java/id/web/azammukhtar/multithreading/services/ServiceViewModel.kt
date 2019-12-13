//package id.web.azammukhtar.multithreading.services
//
//import android.content.ComponentName
//import android.content.ServiceConnection
//import android.os.IBinder
//import android.util.Log
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//
//class ServiceViewModel : ViewModel(){
//
//
//    private val mStatus = MutableLiveData<String>()
//    private val mBinder = MutableLiveData<DataService.MyBinder>()
//
//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
//            // We've bound to MyService, cast the IBinder and get MyBinder instance
//            val binder = iBinder as DataService.MyBinder
//            mBinder.postValue(binder)
//        }
//        override fun onServiceDisconnected(arg0: ComponentName) {
//            mBinder.postValue(null)
//        }
//    }
//
//    fun getServiceConnection(): ServiceConnection {
//        return serviceConnection
//    }
//
//    fun getBinder(): LiveData<DataService.MyBinder> {
//        return mBinder
//    }
//
//    fun getStatus(): LiveData<String> {
//        return mStatus
//    }
//}