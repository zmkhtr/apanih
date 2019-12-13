package id.web.azammukhtar.multithreading.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp
import java.util.*

@Parcelize
@Entity(tableName = "data")
data class DataModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var vin: String,
    var deviceSerial: String,
    var testerCompany: String,
    var timestamp: String,
    var loading: Boolean,
    var status: Int,
    var time: Long,
    var endTime: Long,
    var timeRunning: Boolean,
    var position: Int,
    var imei: String,
    var progress: Int): Parcelable {

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {
            return false
        }

        other as DataModel

        if (id != other.id) {
            return false
        }

        if (timestamp != other.timestamp) {
            return false
        }

        if (testerCompany != other.testerCompany) {
            return false
        }

        if (vin != other.vin) {
            return false
        }

        if (deviceSerial != other.deviceSerial) {
            return false
        }

        if (loading != other.loading) {
            return false
        }

        if (progress != other.progress) {
            return false
        }

        if (status != other.status) {
            return false
        }

        if (time != other.time) {
            return false
        }

        if (timeRunning != other.timeRunning) {
            return false
        }

        if (position != other.position) {
            return false
        }

        if (imei != other.imei) {
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + vin.hashCode()
        result = 31 * result + deviceSerial.hashCode()
        result = 31 * result + testerCompany.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + loading.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + timeRunning.hashCode()
        result = 31 * result + position.hashCode()
        result = 31 * result + imei.hashCode()
        result = 31 * result + progress
        return result
    }
}