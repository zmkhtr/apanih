package id.web.azammukhtar.multithreading.utils

import android.content.Context
import android.location.Location
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import android.net.ConnectivityManager
import java.text.SimpleDateFormat
import java.util.*


object Utils {

    fun logSuccess(methodName: String, message: String){
        Timber.d("LOG SUCCESS, Method : $methodName message : $message")
    }
    fun logError( methodName: String, message: String, throwable: Throwable){
        Timber.e(throwable, "LOG ERROR, Method : $methodName message error : $message")
    }

    fun getTimeBetween(deviceTime: String, serverTime: String) : Boolean{
//        2019-10-17 10:15:59 <- format waktu yang tepat dan bisa di bandingkan

        val device = deviceTime.substring(11, 19)
        val server = serverTime.substring(11, 19)

        val formatter = DateTimeFormat.forPattern("HH:mm:ss")

        val startTime: DateTime
        val endTime: DateTime
        startTime = formatter.parseDateTime(device)
        endTime = formatter.parseDateTime(server)
        val period = Period(endTime, startTime)
        logSuccess("getTimeBetween", " device $device server $server")
        logSuccess("getTimeBetween"," between " + period.hours + " " + period.minutes + " " + period.seconds )

        return period.hours >= 0 || period.minutes >= 0 || period.seconds >= 0
         /*Rubah bagian ini untuk jarak perbandingan pada waktu
         period.hours >= 0 (lebih besar atau sama dengann 0 JAM)
         period.minutes >= 0 (lebih besar atau sama dengann 0 MENIT)
         period.seconds >= 0  (lebih besar atau sama dengann 0 DETIK)*/
    }

    fun changeStatusToString(statusId : Int): String {
        lateinit var status :String
        when (statusId) {
            Constant.STATUS_PROCESS -> status = "PROCESS"
            Constant.STATUS_SUCCESS -> status = "SUCCESS"
            Constant.STATUS_DEFECT_TIMESTAMP -> status = "TIMESTAMP NOT RENEWABLE"
            Constant.STATUS_DEFECT_NOT_IN_DISTANCE -> status = "NOT IN DISTANCE"
            Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER -> status = "NOT REPLY FROM SERVER"
            Constant.STATUS_DEFECT_OPERATOR_SIGNAL -> status = "OPERATOR SIGNAL"
            Constant.STATUS_DEFECT_NO_GPS -> status = "NO GPS"
            Constant.STATUS_FAIL -> status = "FAIL BY SYSTEM"
            Constant.STATUS_DEFECT_SERVER_BUSY -> status = "SERVER IS BUSY AT A MOMENT, PLEASE RETRY"
            Constant.STATUS_SUCCESS_BUT_FAIL -> status = "FUCTIONAL TEST SUCCESS, BUT VEHICLE ALREADY EXIST"
        }
        return status
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }

    fun getDistance(latitude: Double, longitude: Double): Boolean {
        val loc1 = Location("")
        loc1.latitude = DataManager.getLat()
        loc1.longitude = DataManager.getLong()

        logSuccess(
            "getDistance",
            "Device loc " + " Lat  " + loc1.latitude + " Long " + loc1.longitude
        )

        val loc2 = Location("")
        loc2.latitude = latitude
        loc2.longitude = longitude

       logSuccess(
            "getDistance",
            "Server loc " + " Lat  " + loc2.latitude + " Long " + loc2.longitude
        )
        logSuccess("getDistance", "Distance loc" + " " + loc1.distanceTo(loc2) + " Meter ")

        return loc1.distanceTo(loc2) < 10001
       /* Rubah bagian ini untuk jarak perbandingan dalam meter
        loc1.distanceTo(loc2) < 1001
        loc1 = lokasi handphone
        loc2 = lokasi dari server
        dibandingankan dengan jarak 1001 = 1KM
        rubah 1001 untuk merubah jarak perbandingan nilai jarak di tuliskan dalam meter  */
    }

//    const val STATUS_PROCESS = 0
//    const val STATUS_SUCCESS = 1
//    const val STATUS_DEFECT_TIMESTAMP = 2
//    const val STATUS_DEFECT_NOT_IN_DISTANCE = 3
//    const val STATUS_DEFECT_NOT_REPLY_FROM_SERVER = 4
//    const val STATUS_DEFECT_OPERATOR_SIGNAL = 5
//    const val STATUS_DEFECT_NO_GPS = 6
//    const val STATUS_FAIL = 7
}