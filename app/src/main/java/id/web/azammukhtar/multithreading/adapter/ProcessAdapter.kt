package id.web.azammukhtar.multithreading.adapter

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.LocalDatabase
import kotlinx.android.synthetic.main.item_list.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import timber.log.Timber
import java.util.*

class ProcessAdapter(private val interaction: Interaction? = null, private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var operations: HashMap<Int, Boolean> = HashMap()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataModel>() {

        override fun areItemsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
            return (oldItem.id == newItem.id)
        }

        override fun areContentsTheSame(oldItem: DataModel, newItem: DataModel): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }

        val data = differ.currentList[position]
        if (operations.contains(position)) {
            Timber.d("Nothing to do")
        } else {
            operations[position] = true
            interaction?.onItemAdded(position, data)
            data.endTime = System.currentTimeMillis() + data.time
            updateEndTime(data, data.endTime)
            data.time = data.endTime - System.currentTimeMillis()

            val countDownTimer = object : CountDownTimer(data.time * 1000, 1000) {
                override fun onFinish() {
                    updateRunning(data, false)
                    Log.d(" CHRONO done!", "Time's up!")
                    cancel()
                }

                override fun onTick(millisUntilFinished: Long) {
                    Log.d("CHRONO elapsed: ", " " + (millisUntilFinished) / 1000)

                    val minutes = (millisUntilFinished / 1000).toInt() / 60
                    val seconds = (millisUntilFinished / 1000).toInt() % 60


                    val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                    val period = reverseTime("00:$timeLeftFormatted")
                    val timeReverse = String.format(Locale.getDefault(), "%02d:%02d", period.minutes, period.seconds)
                    if (data.timeRunning){
//                        holder.text = timeReverse
                        updateTime(data, (millisUntilFinished) / 1000)
//                        Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning + " DATA " + data.vin)
                    } else {
                        cancel()
//                        Log.d("CHRONO ", "TIME STOP " + data.timeRunning + " DATA " + data.vin)
                    }
                }
            }
            countDownTimer.start()
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<DataModel>) {
        differ.submitList(list)
    }

    class ViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val textTimer = itemView.textTimer
        fun bind(item: DataModel) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            textItemProgressNumber.visibility = View.GONE


            val title = item.vin + " - " + item.deviceSerial
            textStatus.text = title
            ProgressBar.progress = item.progress
            ProgressBar.isIndeterminate = item.loading



        }

    }

    interface Interaction {
        fun onItemSelected(position: Int, item: DataModel)
        fun onItemAdded(position: Int, item: DataModel)
    }

    private fun updateTime(data: DataModel, time: Long) {
        data.time = time
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabase.getInstance(context).taskDao().updateData(data)
        }
    }

    private fun updateEndTime(data: DataModel, time: Long) {
        data.endTime = time
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabase.getInstance(context).taskDao().updateData(data)
        }
    }

    private fun updateRunning(data: DataModel, runing: Boolean) {
        data.timeRunning = runing
        CoroutineScope(Dispatchers.IO).launch {
            LocalDatabase.getInstance(context).taskDao().updateData(data)
        }
    }

    private fun reverseTime(timeGoing : String) : Period {

        val formatter = DateTimeFormat.forPattern("HH:mm:ss")

        val startTime: DateTime
        val endTime: DateTime
        startTime = formatter.parseDateTime(timeGoing)
        endTime = formatter.parseDateTime("00:45:00")
        return Period(startTime, endTime)
    }

}
