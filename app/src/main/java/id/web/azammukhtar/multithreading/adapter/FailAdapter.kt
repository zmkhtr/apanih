package id.web.azammukhtar.multithreading.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.LocalDatabase
import id.web.azammukhtar.multithreading.utils.Constant.STATUS_PROCESS
import id.web.azammukhtar.multithreading.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import id.web.azammukhtar.multithreading.R


class FailAdapter(val context: Context) : RecyclerView.Adapter<FailAdapter.ViewHolder>() {

    private var data: List<DataModel> = ArrayList()
    private lateinit var listener: OnItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("CHRONO ", "ONCREATE")

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_list,
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
        val title = "VIN : " + data.vin + " - " + "SN : " + data.deviceSerial

        holder.text.text = title
        holder.textProgressNumber.text = Utils.changeStatusToString(data.status)
        holder.progress.progress = 100
        holder.progress.progressTintList = ColorStateList.valueOf(Color.RED)
        holder.progress.progressBackgroundTintList = ColorStateList.valueOf(Color.RED)
        holder.textTimer.visibility = View.GONE
        holder.cardView.setOnClickListener {
            listener.onItemClick(data, holder)
        }
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text = itemView.textStatus!!
        val progress = itemView.ProgressBar!!
        val textProgressNumber = itemView.textItemProgressNumber!!
        val cardView = itemView.cardView!!
        val textTimer = itemView.textTimer!!

    }

    fun setData(dataList: List<DataModel>) {
        val oldList = data
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            DataItemDiffCallback(oldList, dataList)
        )
        data = dataList
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
    }

    fun setOnItemClickListener(listener: OnItemClick) {
        this.listener = listener
    }
}