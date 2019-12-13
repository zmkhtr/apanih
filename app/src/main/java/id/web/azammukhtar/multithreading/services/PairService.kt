package id.web.azammukhtar.multithreading.services

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.network.ApiNetwork
import id.web.azammukhtar.multithreading.network.model.fail.FailResponse
import id.web.azammukhtar.multithreading.network.model.pass.PassResponse
import id.web.azammukhtar.multithreading.network.model.position.PositionResponse
import id.web.azammukhtar.multithreading.network.model.start.StartResponse
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.LocalDatabase
import id.web.azammukhtar.multithreading.utils.Constant
import id.web.azammukhtar.multithreading.utils.Constant.CHANNEL_ID
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.utils.Utils
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PairService : Service(){

//    lateinit var listener: OnDataChange

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val dataModel = intent!!.getParcelableExtra<DataModel>("DATA_MODEL_KEY")
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Pair Service")
            .setContentText(dataModel?.vin)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(dataModel!!.position, notification)

//        if (DataManager.isTestOn()){
//            startPairingWithTest(dataModel)
//        } else {
//            startPairingWithoutTest(dataModel)
//        }

        return START_NOT_STICKY
    }


//    private fun startPairingWithoutTest(dataModel: DataModel) {
//        Utils.logSuccess("startPairingWithoutTest ","try " + dataModel.vin)
//        CoroutineScope(Dispatchers.IO).launch {
//            Utils.logSuccess("startPairingWithoutTest ","inside " + dataModel.vin)
//            ApiNetwork.services
//                .startInspectionNormal(
//                    dataModel.vin,
//                    dataModel.deviceSerial,
//                    dataModel.testerCompany
//                )
//                .enqueue(object : Callback<StartResponse> {
//                    override fun onFailure(call: Call<StartResponse>, t: Throwable) {
//                        Utils.logError("startPairingWithoutTest, startInspection","onFailure", t)
//                        if (!Utils.isOnline(applicationContext)){
//                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                        } else {
//                            updateData(dataModel, false, Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER, 0)
//                        }
////                        listener.onDataChange()
//                        stopSelf()
//                    }
//
//                    override fun onResponse(
//                        call: Call<StartResponse>,
//                        response: Response<StartResponse>
//                    ) {
//                        Utils.logSuccess("startPairingWithoutTest, startInspection", "onResponse, code : " + response.code())
//                        if (response.isSuccessful) {
//                            ApiNetwork.services
//                                .startPairingNormal(dataModel.deviceSerial, dataModel.vin)
//                                .enqueue(object : Callback<PassResponse> {
//                                    override fun onFailure(call: Call<PassResponse>, t: Throwable) {
//                                        Utils.logError("startPairingWithoutTest, startPair","onFailure", t)
//                                        if (!Utils.isOnline(applicationContext)){
//                                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                                        } else {
//                                            updateData(dataModel, false, Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER, 0)
//                                        }
////                                        listener.onDataChange()
//                                        stopSelf()
//                                    }
//
//                                    override fun onResponse(
//                                        call: Call<PassResponse>,
//                                        response: Response<PassResponse>
//                                    ) {
//                                        Utils.logSuccess("startPairingWithoutTest, startPair", "onResponse, code : " + response.code())
//                                        if (response.isSuccessful){
//                                            updateData(dataModel, false, Constant.STATUS_SUCCESS, 100)
////                                            listener.onDataChange()
//                                            stopSelf()
//                                        }
//                                    }
//
//                                })
//                        }
//                    }
//                })
//        }
//    }
//
//    private fun startPairingWithTest(dataModel: DataModel) {
//        Utils.logSuccess("startPairingWithTest", "called")
//        CoroutineScope(Dispatchers.IO).launch {
//            delay(10000)
//            Utils.logSuccess("startPairingWithTest", "inside called")
//            ApiNetwork.services
//                .startInspectionNormal(
//                    dataModel.vin,
//                    dataModel.deviceSerial,
//                    dataModel.testerCompany
//                )
//                .enqueue(object : Callback<StartResponse> {
//                    override fun onResponse(
//                        call: Call<StartResponse>,
//                        response: Response<StartResponse>
//                    ) {
//                        Utils.logSuccess("startPairingWithTest, startInspection", "onResponse, code : " + response.code())
//                        if (response.isSuccessful) {
//                            startTest(dataModel)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<StartResponse>, t: Throwable) {
//                        Utils.logError("startPairingWithTest, startInspection","onFailure", t)
//                        if (!Utils.isOnline(applicationContext)){
//                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                            Utils.logError("startTest, checkPosition","no network", t)
//                        } else {
//                            updateData(dataModel, false, Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER, 0)
//                            Utils.logError("startTest, checkPosition","server error", t)
//                        }
////                        listener.onDataChange()
//                        stopSelf()
//                    }
//                })
//        }
//    }
//
//
//    fun startTest(dataModel: DataModel) {
//        CoroutineScope(Dispatchers.IO).launch {
//            var status = 0
//            var done = false
//            repeat(40) {
//                delay(50000)
//                ApiNetwork.services
//                    .checkPositionNormal(dataModel.deviceSerial)
//                    .enqueue(object : Callback<PositionResponse> {
//                        override fun onResponse(
//                            call: Call<PositionResponse>,
//                            response: Response<PositionResponse>
//                        ) {
//                            Utils.logSuccess("startTest, checkPosition","onSuccess : " + response.code())
//
//                            if (response.isSuccessful) {
//                                val deviceTimestamp = response.body()!!.data.deviceCoordinate.positionTimestamp
//                                val serverTimestamp = response.body()!!.data.timestamp.date
//                                val deviceLatitude = response.body()!!.data.deviceCoordinate.latitude
//                                val deviceLongitude = response.body()!!.data.deviceCoordinate.longitude
//                                if (Utils.getTimeBetween(deviceTimestamp, serverTimestamp)) {
//                                    if (getDistance(deviceLatitude, deviceLongitude)) {
//                                        ApiNetwork.services
//                                            .startPairingNormal(
//                                                dataModel.deviceSerial,
//                                                dataModel.vin
//                                            )
//                                            .enqueue(object : Callback<PassResponse> {
//                                                override fun onResponse(
//                                                    call: Call<PassResponse>,
//                                                    response: Response<PassResponse>
//                                                ) {
//                                                    Utils.logSuccess("startTest, startPairing","onSuccess : " + response.code())
//                                                    if (response.isSuccessful){
//                                                        updateData(dataModel, false, Constant.STATUS_SUCCESS, 100)
//                                                        done = true
////                                                        listener.onDataChange()
//                                                        cancel()
//                                                    }
//                                                }
//
//                                                override fun onFailure(
//                                                    call: Call<PassResponse>,
//                                                    t: Throwable
//                                                ) {
//                                                    if (!Utils.isOnline(applicationContext)){
//                                                        Utils.logError("startTest, startPairing","no network", t)
//                                                        updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                                                    } else {
//                                                        Utils.logError("startTest, startPairing","server error", t)
//                                                        updateData(dataModel, false, Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER, 0)
//                                                    }
////                                                    listener.onDataChange()
//                                                    stopSelf()
//                                                }
//
//                                            })
//                                    } else {
//                                        status = Constant.STATUS_DEFECT_NOT_IN_DISTANCE
//                                    }
//                                } else {
//                                    status = Constant.STATUS_DEFECT_TIMESTAMP
//                                }
//                            }
//                        }
//                        override fun onFailure(call: Call<PositionResponse>, t: Throwable) {
//                            if (!Utils.isOnline(applicationContext)){
//                                Utils.logError("startTest, checkPosition","no network", t)
//                                updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                            } else {
//                                Utils.logError("startTest, checkPosition","server error", t)
//                                updateData(dataModel, false, Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER, 0)
//                            }
////                            listener.onDataChange()
//                            stopSelf()
//                        }
//
//                    })
//            }
//            if(!done){
//                ApiNetwork.services
//                    .testFailNormal(dataModel.deviceSerial,dataModel.vin,dataModel.testerCompany)
//                    .enqueue(object : Callback<FailResponse> {
//                        override fun onFailure(call: Call<FailResponse>, t: Throwable) {
//                            if (!Utils.isOnline(applicationContext)){
//                                Utils.logError("startTest, testFail","no network", t)
//                                updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                            } else {
//                                Utils.logError("startTest, testFail","server error", t)
//                                updateData(dataModel, false, Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER, 0)
//                            }
////                            listener.onDataChange()
//                            stopSelf()
//                        }
//                        override fun onResponse(
//                            call: Call<FailResponse>,
//                            response: Response<FailResponse>
//                        ) {
//                            Utils.logSuccess("startTest, testFail","onSuccess : " + response.code())
//                            if (response.isSuccessful){
//                                updateData(dataModel, false, status, 0)
////                                listener.onDataChange()
//                                stopSelf()
//                            }
//                        }
//                    })
//            } else {
////                listener.onDataChange()
//                stopSelf()
//            }
//        }
//    }

    private fun updateData(dataModel: DataModel, loading: Boolean, status: Int, progress: Int) {

        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

        dataModel.status = status
        dataModel.loading = loading
        dataModel.progress = progress
        dataModel.timeRunning = loading
        dataModel.timestamp = dateInString

        CoroutineScope(Dispatchers.IO).launch {
//            LocalDatabase.getInstance(applicationContext).taskDao().deleteData(dataModel)
            LocalDatabase.getInstance(applicationContext).taskDao().updateData(dataModel)
        }
        Utils.logSuccess("updateData", "updated " + dataModel.status)
    }
    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getDistance(latitude: Double, longitude: Double): Boolean {
        (ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val loc1 = Location("")
        loc1.latitude = location!!.latitude
        loc1.longitude = location.longitude

        Utils.logSuccess(
            "getDistance",
            "Device loc " + " Lat  " + loc1.latitude + " Long " + loc1.longitude
        )

        val loc2 = Location("")
        loc2.latitude = latitude
        loc2.longitude = longitude

        Utils.logSuccess(
            "getDistance",
            "Server loc " + " Lat  " + loc2.latitude + " Long " + loc2.longitude
        )
        Utils.logSuccess("getDistance", "Distance loc" + " " + loc1.distanceTo(loc2) + " Meter ")

        return loc1.distanceTo(loc2) < 1001
    }

//    interface OnDataChange {
//        fun onDataChange()
//    }

//    fun setOnDataChangeListener(listener: OnDataChange) {
//        this.listener = listener
//    }
}