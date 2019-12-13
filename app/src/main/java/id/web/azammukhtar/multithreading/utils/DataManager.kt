package id.web.azammukhtar.multithreading.utils

import com.orhanobut.hawk.Hawk
import com.orhanobut.hawk.Hawk.get
import com.orhanobut.hawk.Hawk.put
import id.web.azammukhtar.multithreading.room.DataModel
import java.sql.Timestamp

object DataManager {

    private const val TEST_KEY ="TEST_KEY"
    private const val LAT_KEY ="LAT_KEY"
    private const val LONG_KEY ="LONG_KEY"
    private const val SESSION_KEY ="SESSION_KEY"
    private const val TOKEN_KEY ="TOKEN_KEY"
    const val PAIR_ACTIVITY_KEY = "p4irActvt1kEy"

    fun setTest(test : Boolean) {
        put(TEST_KEY, test)
    }
    fun isTestOn() : Boolean {
        return get(TEST_KEY, true)
    }

    fun setLat(latitude: Double){
        put(LAT_KEY, latitude)
    }

    fun setLong(longitude: Double){
        put(LONG_KEY, longitude)
    }

    fun setLongData(key: String, longitude: Long) {
        put(key, longitude)
    }

    fun getLat() : Double{
        return get(LAT_KEY, 0.0)
    }

    fun getLong() : Double{
        return get(LONG_KEY, 0.0)
    }

    fun getLongData(key: String) : Long {
        return get(key, 0)
    }

    fun setBoolean(key: String, status: Boolean) {
        put(key, status)
    }

    fun getBoolean(key: String) : Boolean {
        return get(key, false)
    }

    fun setStatus(id: Int, status: Int){
        put("status$id", status)
    }

    fun getStatus(id: Int) : Int{
        return get("status$id", 7)
    }

    fun setLogin(test : Boolean, token: String) {
        put(SESSION_KEY, test)
        put(TOKEN_KEY, token)
    }
    fun isLoggedIn() : Boolean {
        return get(SESSION_KEY, false)
    }

    fun getToken() : String{
        return get(TOKEN_KEY, "#")
    }

    fun setData(id: Int, dataModel: DataModel){
        put(id.toString(), dataModel)
    }

    fun getData(id: Int) : DataModel{
        return get(id.toString(), DataModel(
            vin = "#",
            deviceSerial = "#",
            loading = false,
            testerCompany = "#",
            timestamp = "#",
            status = Constant.STATUS_PROCESS,
//                time = 2700,
            time = 0,
            timeRunning = false,
            endTime = 0,
            progress = 0,
            position = -2,
            imei = ""
        ))
    }

    fun deleteDataByKey(key: Int) {
        Hawk.delete(key.toString())
    }
}