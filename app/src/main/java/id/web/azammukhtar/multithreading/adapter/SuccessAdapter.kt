package id.web.azammukhtar.multithreading.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_list.view.*
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.utils.Utils
import kotlin.collections.ArrayList
import id.web.azammukhtar.multithreading.R


class SuccessAdapter(val context: Context) : RecyclerView.Adapter<SuccessAdapter.ViewHolder>() {

    private var data: List<DataModel> = ArrayList()
    private lateinit var listener: OnItemClick

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

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
        val statusPlusTime = Utils.changeStatusToString(data.status) + " - " + data.timestamp
        holder.textProgressNumber.text = statusPlusTime
        holder.progress.progress = 100
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