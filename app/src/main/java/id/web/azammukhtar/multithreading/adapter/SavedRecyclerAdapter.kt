package id.web.azammukhtar.multithreading.adapter


/**
 * Created by Alhudaghifari on 22:17 16/11/19
 *
 * package id.web.azammukhtar.multithreading.adapter

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.LocalDatabase
import id.web.azammukhtar.multithreading.utils.Constant
import id.web.azammukhtar.multithreading.utils.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RecyclerAdapter(val context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

private var data: List<DataModel> = ArrayList()
private lateinit var listener: OnItemClick
private var operations: HashMap<Int, Boolean> = HashMap()
private var time: HashMap<Int, Long> = HashMap()
private var endTime: HashMap<Int, Long> = HashMap()


override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
Timber.d("CHRONO ONCREATE")

return ViewHolder(
LayoutInflater.from(parent.context).inflate(
id.web.azammukhtar.multithreading.R.layout.item_list,
parent,
false
)
)
}

override fun getItemCount(): Int {
return data.size
}

override fun onBindViewHolder(holder: ViewHolder, position: Int) {
val data = data[position]
val title = data.vin + " - " + data.deviceSerial

holder.setIsRecyclable(false)
holder.textProgressNumber.visibility = View.GONE
Timber.d("CHRONO CANCEL ${data.vin} asdas ${data.status} position ${data.position}" )

holder.cardView.setOnClickListener {
listener.onItemClick(data, holder)
}


if (data.position > -1) {
Timber.d("Do nothing nothing to do")

holder.text.text = title
holder.progress.progress = data.progress
holder.progress.isIndeterminate = data.loading
holder.textProgressNumber.visibility = View.GONE

val myTime = DataManager.getLongData(data.id.toString())
endTime.put(data.id, System.currentTimeMillis() + myTime)
//            data.endTimeee = System.currentTimeMillis() + data.timerrr
//            updatePosition(data, position)
//            updateEndTime(data, data.endTimeee)
//            data.timerrr = data.endTimeee - System.currentTimeMillis()
time.put(data.id, endTime.getValue(data.id) - System.currentTimeMillis())
if(holder.countDownTimer != null){
holder.countDownTimer!!.cancel()
Timber.d("CHRONO CANCEL ${holder.countDownTimer}")
}
holder.countDownTimer = object : CountDownTimer(time.getValue(data.id) * 1000, 1000) {
override fun onFinish() {
listener.onItemFail()
updateRunning(data)
Timber.d("1. CHRONO done! Time's up!")
cancel()
}

override fun onTick(millisUntilFinished: Long) {
//                    Timber.d("CHRONO elapsed: %s", (millisUntilFinished) / 1000)

val minutes = (millisUntilFinished / 1000).toInt() / 60
val seconds = (millisUntilFinished / 1000).toInt() % 60


val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

val period = reverseTime("00:$timeLeftFormatted")
val timeReverse = String.format(Locale.getDefault(), "%02d:%02d", period.minutes, period.seconds)
if (data.timeRunning){
holder.textTimer.text = timeReverse
time.put(data.id, (millisUntilFinished) / 1000)
DataManager.setLongData(data.id.toString(), (millisUntilFinished) / 1000)
val modelku = DataManager.getData(data.id)
if (modelku.status != Constant.STATUS_PROCESS) {
cancel()
}
//                        updateTime(data, (millisUntilFinished) / 1000)
//                        Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning + " DATA " + data.vin)
} else {
cancel()
//                        Log.d("CHRONO ", "TIME STOP " + data.timeRunning + " DATA " + data.vin)
}
}
}.start()
} else {
listener.onItemAdded(data, holder)
//            operations[position] = true

//            updatePosition(data, position)
Timber.d("Do first in")
holder.text.text = title
holder.progress.progress = data.progress
holder.progress.isIndeterminate = data.loading
holder.textProgressNumber.visibility = View.GONE

val myTime = DataManager.getLongData(data.id.toString())
endTime.put(data.id, System.currentTimeMillis() + myTime)
//            data.endTimeee = System.currentTimeMillis() + data.timerrr
//            updateEndTime(data, data.endTimeee)
//            data.timerrr = data.endTimeee - System.currentTimeMillis()
time.put(data.id, endTime.getValue(data.id) - System.currentTimeMillis())

holder.countDownTimer = object : CountDownTimer(time.getValue(data.id) * 1000, 1000) {
override fun onFinish() {
listener.onItemFail()
updateRunning(data)
Timber.d("2. CHRONO done! Time's up!")
cancel()
}

override fun onTick(millisUntilFinished: Long) {
//                    Timber.d("CHRONO elapsed: %s", (millisUntilFinished) / 1000)

val minutes = (millisUntilFinished / 1000).toInt() / 60
val seconds = (millisUntilFinished / 1000).toInt() % 60


val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

val period = reverseTime("00:$timeLeftFormatted")
val timeReverse = String.format(Locale.getDefault(), "%02d:%02d", period.minutes, period.seconds)
if (data.timeRunning){
holder.textTimer.text = timeReverse
time.put(data.id, (millisUntilFinished) / 1000)
DataManager.setLongData(data.id.toString(), (millisUntilFinished) / 1000)
val modelku = DataManager.getData(data.id)
if (modelku.status != Constant.STATUS_PROCESS) {
cancel()
}
//                        updateTime(data, (millisUntilFinished) / 1000)
//                        Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning + " DATA " + data.vin)
} else {
cancel()
//                        Log.d("CHRONO ", "TIME STOP " + data.timeRunning + " DATA " + data.vin)
}
}
}.start()
}
}

//    private fun updatePosition(data: DataModel, position: Int) {
//        data.position = position
//        CoroutineScope(IO).launch {
//            LocalDatabase.getInstance(context).taskDao().updateData(data)
//        }
//    }
//
private fun updateTime(data: DataModel, time: Long) {
data.time = time
CoroutineScope(IO).launch {
LocalDatabase.getInstance(context).taskDao().updateData(data)
}
}
//
//    private fun updateEndTime(data: DataModel, time: Long) {
//        data.endTimeee = time
//        CoroutineScope(IO).launch {
//            LocalDatabase.getInstance(context).taskDao().updateData(data)
//        }
//    }

private fun updateRunning(data: DataModel) {

val date = getCurrentDateTime()
val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

data.timeRunning = false
data.loading = false
data.progress = 0
data.timestamp = dateInString
data.status = DataManager.getData(data.id).status

Timber.d("DB Log Fail $data")

CoroutineScope(IO).launch {
LocalDatabase.getInstance(context).taskDao().updateData(data)
}
}

private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
val formatter = SimpleDateFormat(format, locale)
return formatter.format(this)
}

private fun getCurrentDateTime(): Date {
return Calendar.getInstance().time
}

private fun reverseTime(timeGoing : String) : Period {

val formatter = DateTimeFormat.forPattern("HH:mm:ss")

val startTime: DateTime
val endTime: DateTime
startTime = formatter.parseDateTime(timeGoing)
//        endTime = formatter.parseDateTime("00:45:00")
endTime = formatter.parseDateTime("00:05:00")
return Period(startTime, endTime)
}

class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
val text = itemView.textStatus!!
val progress = itemView.ProgressBar!!
val textProgressNumber = itemView.textItemProgressNumber!!
val cardView = itemView.cardView!!
val textTimer = itemView.textTimer!!

var countDownTimer: CountDownTimer? = null
}

fun setData(dataList: List<DataModel>) {
val oldList = data
val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
DataItemDiffCallback(oldList, dataList)
)
data = dataList
for (item in data) {
//            operations.put(item.id, false)
Timber.d("id time masuk : ${item.id}")
if (time.get(item.id) == null) {
Timber.d("id time saya cinta : ${item.id}")
time.put(item.id, 300)
}
if (endTime.get(item.id) == null) {
Timber.d("id endTime saya : ${item.id}")
endTime.put(item.id, 0)
}
}
notifyDataSetChanged()
diffResult.dispatchUpdatesTo(this)
}

class DataItemDiffCallback(private var oldDataList: List<DataModel>, private var newDataList: List<DataModel>) :
DiffUtil.Callback() {
override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
return (oldDataList[oldItemPosition].id == newDataList[newItemPosition].id)
}

override fun getOldListSize(): Int {
return oldDataList.size
}

override fun getNewListSize(): Int {
return newDataList.size
}

override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
return oldDataList[oldItemPosition] == newDataList[newItemPosition]
}
}


interface OnItemClick {
fun onItemClick(data: DataModel, holder: ViewHolder)
fun onItemAdded(data: DataModel, holder: ViewHolder)
fun onItemFail()
}

fun setOnItemClickListener(listener: OnItemClick) {
this.listener = listener
}
}
 */