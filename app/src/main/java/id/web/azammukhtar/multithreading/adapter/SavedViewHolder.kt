//package id.web.azammukhtar.multithreading.adapter
//
//import android.os.CountDownTimer
//import android.util.Log
//import android.view.View
//import id.web.azammukhtar.multithreading.utils.Utils
//import java.util.*
//
//override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
//    val data = data[position]
//    val title = data.vin + " - " + data.deviceSerial
//
//    Log.d("CHRONO ", "ONBIND " + data.time + " holder " + data.endTime)
//
//    Log.d("data ", "$data holder $holder")
//
//    Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning
//            + " data " + data.vin + " status " + data.status + " loading " + data.loading)
//
//    val status = Utils.changeStatusToString(data.status)
//
//    holder.cardView.setOnClickListener {
//        listener.onItemClick(data, holder)
//    }
//
//    if (data.timeRunning) {
//
//        if(holder.countDownTimer != null){
//            holder.countDownTimer!!.cancel()
//            Log.d("CHRONO ", "CANCEL " + holder.countDownTimer)
//        }
//        Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning + " data " + data.vin)
//        data.endTime = System.currentTimeMillis() + data.time
//        updateEndTime(data, data.endTime)
//        data.time = data.endTime - System.currentTimeMillis()
//
//        holder.countDownTimer = object : CountDownTimer(data.time * 1000, 1000) {
//            override fun onFinish() {
//                updateRunning(data, false)
//                Log.d(" CHRONO done!", "Time's up!")
//                holder.textProgressNumber.text = status
//                cancel()
//            }
//
//            override fun onTick(millisUntilFinished: Long) {
//                Log.d("CHRONO elapsed: ", " " + (millisUntilFinished) / 1000)
//
//                val minutes = (millisUntilFinished / 1000).toInt() / 60
//                val seconds = (millisUntilFinished / 1000).toInt() % 60
//
//
//                val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
//
//                val period = reverseTime("00:$timeLeftFormatted")
//                val timeReverse = String.format(Locale.getDefault(), "%02d:%02d", period.minutes, period.seconds)
//                if (data.timeRunning){
//                    holder.textTimer.text = timeReverse
//                    updateTime(data, (millisUntilFinished) / 1000)
////                        Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning + " DATA " + data.vin)
//                } else {
//                    cancel()
//                    holder.textProgressNumber.text = status
////                        Log.d("CHRONO ", "TIME STOP " + data.timeRunning + " DATA " + data.vin)
//                }
//            }
//        }.start()
//    } else {
//
//        Log.d("CHRONO ", "TIME RUNNING " + data.timeRunning )
//        holder.countDownTimer = object : CountDownTimer(0, 1000) {
//            override fun onFinish() {
//                updateRunning(data, false)
//                Log.d(" CHRONO done!", "Time's up! false init ")
//                cancel()
//                holder.textProgressNumber.text = status
//            }
//            override fun onTick(millisUntilFinished: Long) {
//                Log.d(" CHRONO seconds : ", " " + (millisUntilFinished) / 1000)
//                cancel()
//                holder.textProgressNumber.text = status
//            }
//        }
//    }
//
//
//
//    if (operations.contains(position)) {
//        Log.d("Do nothing", " nothing to do")
//        if (data.loading) {
//
//            Log.d("CHRONO Do nothing", " nothing to do")
//
//            holder.textProgressNumber.visibility = View.GONE
//            holder.textTimer.visibility = View.VISIBLE
//
//
//            holder.text.text = title
//            holder.progress.progress = data.progress
//            holder.progress.isIndeterminate = data.loading
//            holder.textProgressNumber.text = status
//
//        } else {
//
//            Log.d("CHRONO else Do nothing", " nothing to do")
//            holder.countDownTimer!!.cancel()
//            holder.textProgressNumber.visibility = View.VISIBLE
//            holder.textTimer.visibility = View.GONE
//            holder.text.text = title
//            holder.progress.progress = data.progress
//            holder.progress.isIndeterminate = data.loading
//            holder.textProgressNumber.text = status
//        }
//    } else {
//        listener.onItemAdded(data, holder)
//        operations[position] = true
//
//        if (data.loading) {
//
//            Log.d("CHRONO Do ", " nothing to do")
//            holder.textProgressNumber.visibility = View.GONE
//            holder.textTimer.visibility = View.VISIBLE
//
//            holder.text.text = title
//            holder.progress.progress = data.progress
//            holder.progress.isIndeterminate = data.loading
//            holder.textProgressNumber.text = status
//
//        } else {
//
//            Log.d("CHRONO Do else ", " nothing to do")
//            holder.textProgressNumber.visibility = View.VISIBLE
//            holder.textTimer.visibility = View.GONE
//            holder.countDownTimer!!.cancel()
//            holder.text.text = title
//            holder.progress.progress = data.progress
//            holder.progress.isIndeterminate = data.loading
//            holder.textProgressNumber.text = status
//        }
//    }
//}
