//package id.web.azammukhtar.multithreading.services
//
//import android.Manifest
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import android.location.LocationManager
//import android.os.Binder
//import android.os.IBinder
//import android.util.Log
//import androidx.core.content.ContextCompat
//import id.web.azammukhtar.multithreading.network.ApiNetwork
//import id.web.azammukhtar.multithreading.room.DataModel
//import id.web.azammukhtar.multithreading.room.LocalDatabase
//import id.web.azammukhtar.multithreading.utils.Constant.STATUS_DEFECT
//import id.web.azammukhtar.multithreading.utils.Constant.STATUS_FAIL
//import id.web.azammukhtar.multithreading.utils.Constant.STATUS_NETWORK_ERROR
//import id.web.azammukhtar.multithreading.utils.Constant.STATUS_PROCESS
//import id.web.azammukhtar.multithreading.utils.Constant.STATUS_SUCCESS
//import kotlinx.coroutines.*
//import kotlinx.coroutines.Dispatchers.IO
//import org.joda.time.DateTime
//import org.joda.time.Period
//import org.joda.time.format.DateTimeFormat
//import java.io.IOException
//
//class DataService : Service() {
//    private val TAG = "DataService "
//
//
//    private lateinit var newIntent: Intent
//    private val mBinder = MyBinder()
//
//    private val context = this
//    private lateinit var status: String
//
//    override fun onBind(p0: Intent?): IBinder? {
//        return mBinder
//    }
//
//    inner class MyBinder : Binder() {
//        fun getService(): DataService {
//            return this@DataService
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        status = STATUS_PROCESS
//        Log.d(TAG, " status $status")
//    }
//
////    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//////        val input = intent!!.getStringExtra("KEY")
//////        val data = intent.getParcelableExtra<DataModel>("DATA")
//////
//////        newIntent = Intent("id.web.azammukhtar.multithreading")
//////        newIntent.putExtra("KEY", input)
//////        newIntent.putExtra("DATA", data)
////
////
////
//////        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//////            .setContentTitle("Pairing Service Started")
//////            .setContentText(input)
//////            .setSmallIcon(R.drawable.ic_add)
//////            .build()
////
//////        startForeground(1, notification)
////
//////        startPairing(data!!, input!!)
////
//////        return START_NOT_STICKY
////       }
//
//
//    fun startPairing(dataModel: DataModel, string: String) {
//        CoroutineScope(IO).launch {
//            delay(10000)
//
//            status = STATUS_PROCESS
//            var done = false
//
//            Log.e("PAIR : ", "start pair await key $done")
//            val startPair = async {
//                ApiNetwork.services.startInspection(
//                    dataModel.vin,
//                    dataModel.deviceSerial,
//                    dataModel.testerCompany
//                )
//            }
//            try {
//                startPair.await()
//                Log.e("PAIR : ", "start pair await ")
//            } catch (ex: IOException) {
//                // do your handling here
//                ex.printStackTrace()
//                Log.e("PAIR : ", "start pair ", ex)
//                status = STATUS_NETWORK_ERROR
//                Log.d("PAIR Status : ", " $status")
//            }
//
//            repeat(5) {
//                delay(60000)
//
//                Log.d("PAIR Progress : ", "$string $it")
//
//                val pairLocation = async {
//                    ApiNetwork.services.checkPosition(dataModel.deviceSerial)
//                }
//
//                try {
//                    pairLocation.await()
//                } catch (ex: IOException) {
//                    // do your handling here
//                    Log.e("PAIR : ", "pair location ", ex)
//                    ex.printStackTrace()
//                    status = STATUS_NETWORK_ERROR
//                }
//                if (getTimeBetween(pairLocation.await().data.deviceTimestamp, pairLocation.await().data.serverTimestamp)){
//                    if (getDistance(
//                            pairLocation.await().data.deviceCoordinates.latitude,
//                            pairLocation.await().data.deviceCoordinates.longitude
//                        ) <= 100) {
//                        val pairPass = async {
//                            ApiNetwork.services.startPairing(
//                                dataModel.deviceSerial,
//                                dataModel.vin
//                            )
//                        }
//                        try {
//                            pairPass.await()
//                            status = STATUS_SUCCESS
//                            done = true
//                            cancel()
//                        } catch (ex: IOException) {
//                            // do your handling here
//                            ex.printStackTrace()
//                            Log.e("PAIR : ", "pair pass ", ex)
//                            status = STATUS_NETWORK_ERROR
//                        }
//                    }
//                }
//            }
//
//            Log.d("TimeOut", " timeout")
//
//            if (!done){
//                val pairFail = async {
//                    ApiNetwork.services.testFail(dataModel.deviceSerial)
//                }
//                try {
//                    pairFail.await()
//                    status = STATUS_DEFECT
//
//                } catch (ex: IOException) {
//                    // do your handling here
//                    ex.printStackTrace()
//                    Log.e("PAIR : ", "pair fail ", ex)
//                    status = STATUS_FAIL
//                }
//            }
//        }
//    }
//
//
//
//    private fun getTimeBetween(deviceTime: String, serverTime: String) : Boolean{
//
//        val device = deviceTime.substring(12, 19)
//        val server = serverTime.substring(12, 19)
//
//        val formatter = DateTimeFormat.forPattern("HH:mm:ss")
//
//        val startTime: DateTime
//        val endTime: DateTime
//        startTime = formatter.parseDateTime(device)
//        endTime = formatter.parseDateTime(server)
//        val period = Period(startTime, endTime)
//        Log.d("TIME BETWEEN ", " between " + period.hours + " " + period.minutes + " " + period.seconds)
//        return period.seconds >= 1
//    }
//
//    private fun updateData(dataModel: DataModel, loading: Boolean, status: String, progress: Int) {
//        Log.d(TAG, "updated")
//        dataModel.status = status
//        dataModel.loading = loading
//        dataModel.progress = progress
////        dataModel.time = time
//        CoroutineScope(IO).launch {
//            LocalDatabase.getInstance(context).taskDao().updateData(dataModel)
//        }
//    }
//
//
//    override fun onTaskRemoved(rootIntent: Intent) {
//        super.onTaskRemoved(rootIntent)
//        Log.d(TAG, "onTaskRemoved: called.")
//        stopSelf()
//    }
//
//    private fun getDistance(latitude: Double, longitude: Double): Float {
//        (ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED)
//
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//
//        val loc1 = Location("")
//        loc1.latitude = location!!.latitude
//        loc1.longitude = location.longitude
//
//        Log.d("Device loc ", " Lat  " + loc1.latitude + " Long " + loc1.longitude)
//
//        val loc2 = Location("")
//        loc2.latitude = latitude
//        loc2.longitude = longitude
//
//
//        Log.d("Server loc ", " Lat  " + loc2.latitude + " Long " + loc2.longitude)
//
//        Log.d("Distance loc", " " + loc1.distanceTo(loc2) + " Meter ")
//        return loc1.distanceTo(loc2)
//    }
//
//    fun getStatus() : String{
//        return status
//    }
//
//}