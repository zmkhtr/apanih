package id.web.azammukhtar.multithreading.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.Injection
import id.web.azammukhtar.multithreading.room.viewModel.AllDataViewModel
import android.os.Bundle
import android.util.Log


class DataReceiver :BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val data = intent!!.getParcelableExtra<DataModel>("DATA")
        val input = intent!!.getStringExtra("KEY")

        Log.d("Broadcast ", " Broadcast receive 1 $input")
        val newIntent = Intent("id.web.azammukhtar.multithreading")
        val extras = Bundle()
        newIntent.putExtra("KEY", input)
        newIntent.putExtra("DATA", data)
        newIntent.putExtras(extras)
        context!!.sendBroadcast(newIntent)
    }
}