package id.web.azammukhtar.multithreading.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.adapter.RecyclerAdapter
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.viewModel.AllDataViewModel
import kotlinx.coroutines.*
import id.web.azammukhtar.multithreading.network.ApiNetwork
import id.web.azammukhtar.multithreading.network.liveModel.fail.FailLive
import id.web.azammukhtar.multithreading.network.liveModel.pass.PassLive
import id.web.azammukhtar.multithreading.network.liveModel.position.PositionLive
import id.web.azammukhtar.multithreading.network.liveModel.start.StartLive
import id.web.azammukhtar.multithreading.network.model.fail.FailResponse
import id.web.azammukhtar.multithreading.network.model.pass.PassResponse
import id.web.azammukhtar.multithreading.network.model.pass.Timestamp
import id.web.azammukhtar.multithreading.network.model.position.PositionResponse
import id.web.azammukhtar.multithreading.network.model.start.StartResponse
import kotlinx.coroutines.Dispatchers.IO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import id.web.azammukhtar.multithreading.utils.Utils
import id.web.azammukhtar.multithreading.room.Injection
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.ui.PairActivity
import kotlinx.android.synthetic.main.fragment_home.*
import id.web.azammukhtar.multithreading.utils.Constant
import id.web.azammukhtar.multithreading.utils.Constant.STATUS_FAIL
import java.text.SimpleDateFormat
import java.util.*
import id.web.azammukhtar.multithreading.ui.MainActivity
import id.web.azammukhtar.multithreading.utils.Constant.REPEAT_TEST
import kotlinx.coroutines.NonCancellable.cancel
import timber.log.Timber
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {


    private lateinit var viewModel: AllDataViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var dataModel: ArrayList<DataModel>
    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var mContext: Context

    companion object {
        var DATA_PROCESS: String = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataModel = ArrayList()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated home")
        mContext = context!!

        initRecyclerView()
        initViewModel()
        setWhenDataAddedProcess()

        setTest()
        fabClick()
        val location = "Position : Lat " + DataManager.getLat() + " Long " + DataManager.getLong()
        textLocation.text = location

    }

    private fun setCounter() {
        Utils.logSuccess("setCounter", "process " + dataModel.size)
        val counter = "Total Process : " + dataModel.size
        textProcessCounter.text = counter
    }

    private fun setWhenDataAddedProcess() {
        recyclerAdapter.setOnItemClickListener(object : RecyclerAdapter.OnItemClick {
            override fun onItemFail(data: DataModel) {
//                val mainActivity = activity as MainActivity
//                mainActivity.showHideFragment()
//                viewModel.getDataProcess()
                var status = 0
                var fail = false
                var serverbusy = false

                Timber.d("onItemFail DB Log Fail $data")

                val position = JsonObject()
                position.addProperty("imei", data.imei)
                position.addProperty("serial", data.deviceSerial)
                ApiNetwork(mContext).services
                    .checkPositionLive(position.toString())
                    .enqueue(object : Callback<PositionLive> {
                        override fun onResponse(
                            call: Call<PositionLive>,
                            response: Response<PositionLive>
                        ) {
                            Utils.logSuccess(
                                "startTest, checkPosition",
                                "onSuccess : " + response.code()
                            )

                            if (response.isSuccessful && response.body()!!.status_code != 500) {
                                val deviceTimestamp =
                                    response.body()!!.data.deviceCoordinates.positionTimestamp
                                val serverTimestamp =
                                    response.body()!!.data.timestamp.toCorrectDate()
                                val deviceLatitude =
                                    response.body()!!.data.deviceCoordinates.latitude
                                val deviceLongitude =
                                    response.body()!!.data.deviceCoordinates.longitude
                                if (Utils.getTimeBetween(deviceTimestamp, serverTimestamp)) {
                                    if (Utils.getDistance(deviceLatitude, deviceLongitude)) {
                                        val startPair = JsonObject()
                                        startPair.addProperty("imei", data.imei)
                                        startPair.addProperty("serial", data.deviceSerial)
                                        startPair.addProperty("vin", data.vin)
                                        ApiNetwork(mContext).services
                                            .startPairingLive(startPair.toString())
                                            .enqueue(object : Callback<PassLive> {
                                                override fun onResponse(
                                                    call: Call<PassLive>,
                                                    response: Response<PassLive>
                                                ) {
                                                    Utils.logSuccess(
                                                        "startTest, startPairing",
                                                        "onSuccess : " + response.code()
                                                    )
                                                    if (response.isSuccessful) {
                                                        updateData(
                                                            data,
                                                            false,
                                                            Constant.STATUS_SUCCESS,
                                                            100
                                                        )
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<PassLive>,
                                                    t: Throwable
                                                ) {
                                                    if (!Utils.isOnline(context!!)) {
                                                        Utils.logError(
                                                            "startTest, startPairing",
                                                            "no network",
                                                            t
                                                        )
                                                        updateData(
                                                            data,
                                                            false,
                                                            Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                                            0
                                                        )
                                                    } else {
                                                        Utils.logError(
                                                            "startTest, startPairing",
                                                            "server error",
                                                            t
                                                        )
                                                        updateData(
                                                            data,
                                                            false,
                                                            Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                                            0
                                                        )
                                                    }
                                                }

                                            })
                                    } else {
                                        status = Constant.STATUS_DEFECT_NOT_IN_DISTANCE
                                        DataManager.setStatus(data.id, status)
                                        fail = true
                                    }
                                } else {
                                    status = Constant.STATUS_DEFECT_TIMESTAMP
                                    DataManager.setStatus(data.id, status)
                                    fail = true
                                }
                            } else {
                                status = Constant.STATUS_DEFECT_SERVER_BUSY
                                DataManager.setStatus(data.id, status)
                                serverbusy = true
                            }
                        }

                        override fun onFailure(call: Call<PositionLive>, t: Throwable) {
                            if (!Utils.isOnline(context!!)) {
                                Utils.logError("startTest, checkPosition", "no network", t)
                                updateData(
                                    data,
                                    false,
                                    Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                    0
                                )
                            } else {
                                Utils.logError("startTest, checkPosition", "server error", t)
                                updateData(
                                    data,
                                    false,
                                    Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                    0
                                )
                            }
                        }

                    })

                if (fail) {
                    val failPair = JsonObject()
                    failPair.addProperty("imei", data.imei)
                    failPair.addProperty("serial", data.deviceSerial)
                    ApiNetwork(mContext).services
                        .testFailLive(failPair.toString())
                        .enqueue(object : Callback<FailLive> {
                            override fun onFailure(call: Call<FailLive>, t: Throwable) {
                                if (!Utils.isOnline(context!!)) {
                                    Utils.logError("startTest, testFail", "no network", t)
                                    updateData(
                                        data,
                                        false,
                                        Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                        0
                                    )
                                } else {
                                    Utils.logError("startTest, testFail", "server error", t)
                                    updateData(
                                        data,
                                        false,
                                        Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                        0
                                    )
                                }
                            }

                            override fun onResponse(
                                call: Call<FailLive>,
                                response: Response<FailLive>
                            ) {
                                Utils.logSuccess(
                                    "startTest, testFail",
                                    "onSuccess : " + response.code()
                                )
                                if (response.isSuccessful) {
                                    Timber.d("FAIL RESPONSE  ${DataManager.getStatus(data.id)} STATUS $status")
                                    updateData(data, false, DataManager.getStatus(data.id), 0)
                                }
                            }
                        })
                } else if (serverbusy){
                    updateData(
                        data,
                        false,
                        Constant.STATUS_DEFECT_SERVER_BUSY,
                        0
                    )
                }
            }

            override fun onItemAdded(data: DataModel, holder: RecyclerAdapter.ViewHolder) {
                Utils.logSuccess("setWhenDataAddedProcess", "vin " + data.vin)
                Utils.logSuccess("setWhenDataAddedProcess", "DATA_PROCESS $DATA_PROCESS")
                Utils.logSuccess("setWhenDataAddedProcess", "loading " + DataManager.isTestOn())
                Utils.logSuccess("setWhenDataAddedProcess", "data $data")
                if (data.loading && data.status == 0) {
                    if (data.position == -1) {
                        if (data.vin == DATA_PROCESS) {
                            if (DataManager.isTestOn()) {
                                startPairingLiveWithTest(data)
                            } else {
                                startPairingLiveWithoutTest(data)
                            }
                        } else {
                            if (DataManager.isTestOn()) {
                                startPairingLiveWithTest(data)
                            } else {
                                startPairingLiveWithoutTest(data)
                            }
                        }
                    }
                }
            }

            override fun onItemClick(data: DataModel, holder: RecyclerAdapter.ViewHolder) {
                Utils.logSuccess("Clicked", "Data : $data")
//                viewModel.addData(DataManager.getData(data.id))
//                viewModel.deleteData(data)
//                val mainActivity = activity as MainActivity
//                mainActivity.showHideFragment()
            }
        })
    }

//    private fun startService(dataModel: DataModel) {
//        val serviceIntent = Intent(context, PairService::class.java)
//        serviceIntent.putExtra("DATA_MODEL_KEY", dataModel)
//        ContextCompat.startForegroundService(context!!,serviceIntent)
//    }

    private fun initRecyclerView() {
        recyclerAdapter = RecyclerAdapter(mContext)
        linearLayoutManager = LinearLayoutManager(mContext)
        recyclerViewMain.apply {
            linearLayoutManager.stackFromEnd = true
            linearLayoutManager.reverseLayout = true
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            scrollToPosition(recyclerAdapter.itemCount)
            recycledViewPool.setMaxRecycledViews(0, 20)
        }
    }

    private fun initViewModel() {
        Utils.logSuccess("initViewModel", "try")
        val viewModelFactory = Injection.provideViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AllDataViewModel::class.java)
        viewModel.getDataProcess()
        viewModel.datas.observe(this, Observer {
            dataModel.clear()
            if (it.isEmpty()) {
                textMainNoData.visibility = View.VISIBLE
                Utils.logSuccess("initViewModelHome", "empty $it")
                dataModel.addAll(it)
                recyclerAdapter.setData(it)
                setCounter()
//                val counter = "Total Process : " + 0
//                textProcessCounter.text = counter
            } else {
                if (textMainNoData.visibility == View.VISIBLE)
                    textMainNoData.visibility = View.GONE
                dataModel = ArrayList()
                dataModel.addAll(it)
                var totalDeleted = 0
                for (i in 0 until it.size) {
                    Timber.d("FILTER DATA awal it $i device nya ${it[i].deviceSerial} dan statusnya datamodel ${it[i].status}")
                    val modelku = DataManager.getData(it[i].id)
                    if (modelku.status != Constant.STATUS_PROCESS) {
//                        Timber.d("FILTER DATA awak masuk sini lho it $i ${it[i].deviceSerial} kzl aing")
                        dataModel.removeAt(i - totalDeleted)
                        totalDeleted++
                    }
                }
                Timber.d("FILTER DATA ukuran it : ${it.size} dan ukuran dataModel : ${dataModel.size}")

                if (dataModel.size == 0) {
                    textMainNoData.visibility = View.VISIBLE
                    val index = ArrayList<Int>()
                    for (i in 0 until it.size) {
                        if (it[i].status == Constant.STATUS_PROCESS)
                            index.add(i)
                    }
                    for (i in 0 until index.size) {
                        viewModel.deleteData(it[index[i]])
                    }
                }

                recyclerAdapter.setData(dataModel)


                Utils.logSuccess("initViewModelHome ", "not empty $it")
                setCounter()
            }
        })
    }

//    fun startPairingWithoutTest(dataModel: DataModel) {
//        Utils.logSuccess("startPairingWithoutTest ", "try " + dataModel.vin)
//        CoroutineScope(IO).launch {
//            Utils.logSuccess("startPairingWithoutTest ", "inside " + dataModel.vin)
//            ApiNetwork.services
//                .startInspectionNormal(
//                    dataModel.vin,
//                    dataModel.deviceSerial,
//                    dataModel.testerCompany
//                )
//                .enqueue(object : Callback<StartResponse> {
//                    override fun onFailure(call: Call<StartResponse>, t: Throwable) {
//                        Utils.logError("startPairingWithoutTest, startInspection", "onFailure", t)
//                        if (!Utils.isOnline(context!!)) {
//                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                        } else {
//                            updateData(
//                                dataModel,
//                                false,
//                                Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
//                                0
//                            )
//                        }
//                    }
//
//                    override fun onResponse(
//                        call: Call<StartResponse>,
//                        response: Response<StartResponse>
//                    ) {
//                        Utils.logSuccess(
//                            "startPairingWithoutTest, startInspection",
//                            "onResponse, code : " + response.code()
//                        )
//                        if (response.isSuccessful) {
//                            ApiNetwork.services
//                                .startPairingNormal(dataModel.deviceSerial, dataModel.vin)
//                                .enqueue(object : Callback<PassResponse> {
//                                    override fun onFailure(call: Call<PassResponse>, t: Throwable) {
//                                        Utils.logError(
//                                            "startPairingWithoutTest, startPair",
//                                            "onFailure",
//                                            t
//                                        )
//                                        if (!Utils.isOnline(context!!)) {
//                                            updateData(
//                                                dataModel,
//                                                false,
//                                                Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
//                                                0
//                                            )
//                                        } else {
//                                            updateData(
//                                                dataModel,
//                                                false,
//                                                Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
//                                                0
//                                            )
//                                        }
//                                    }
//
//                                    override fun onResponse(
//                                        call: Call<PassResponse>,
//                                        response: Response<PassResponse>
//                                    ) {
//                                        Utils.logSuccess(
//                                            "startPairingWithoutTest, startPair",
//                                            "onResponse, code : " + response.code()
//                                        )
//                                        if (response.isSuccessful) {
//                                            updateData(
//                                                dataModel,
//                                                false,
//                                                Constant.STATUS_SUCCESS,
//                                                100
//                                            )
//                                        }
//                                    }
//                                })
//                        }
//                    }
//                })
//        }
//    }
//
//    fun startPairingWithTest(dataModel: DataModel) {
//        Utils.logSuccess("startPairingWithTest", "called")
//        CoroutineScope(IO).launch {
//            delay(3000)
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
//                        Utils.logSuccess(
//                            "startPairingWithTest, startInspection",
//                            "onResponse, code : " + response.code()
//                        )
//                        if (response.isSuccessful) {
//                            startTest(dataModel)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<StartResponse>, t: Throwable) {
//                        Utils.logError("startPairingWithTest, startInspection", "onFailure", t)
//                        if (!Utils.isOnline(context!!)) {
//                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
//                            Utils.logError("startTest, checkPosition", "no network", t)
//                        } else {
//                            updateData(
//                                dataModel,
//                                false,
//                                Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
//                                0
//                            )
//                            Utils.logError("startTest, checkPosition", "server error", t)
//                        }
//                    }
//                })
//        }
//    }
//    fun startTest(dataModel: DataModel) {
//        CoroutineScope(IO).launch {
//            var status = 0
//            var done = false
//            repeat(REPEAT_TEST) {
//                delay(55000)
//                ApiNetwork.services
//                    .checkPositionNormal(dataModel.deviceSerial)
//                    .enqueue(object : Callback<PositionResponse> {
//                        override fun onResponse(
//                            call: Call<PositionResponse>,
//                            response: Response<PositionResponse>
//                        ) {
//                            Utils.logSuccess(
//                                "startTest, checkPosition",
//                                "onSuccess : " + response.code()
//                            )
//
//                            if (response.isSuccessful) {
//                                val deviceTimestamp =
//                                    response.body()!!.data.deviceCoordinate.positionTimestamp
//                                val serverTimestamp = response.body()!!.data.timestamp.date
//                                val deviceLatitude =
//                                    response.body()!!.data.deviceCoordinate.latitude
//                                val deviceLongitude =
//                                    response.body()!!.data.deviceCoordinate.longitude
//                                if (Utils.getTimeBetween(deviceTimestamp, serverTimestamp)) {
//                                    if (Utils.getDistance(deviceLatitude, deviceLongitude)) {
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
//                                                    Utils.logSuccess(
//                                                        "startTest, startPairing",
//                                                        "onSuccess : " + response.code()
//                                                    )
//                                                    if (response.isSuccessful) {
//                                                        updateData(
//                                                            dataModel,
//                                                            false,
//                                                            Constant.STATUS_SUCCESS,
//                                                            100
//                                                        )
//                                                        done = true
//                                                        cancel()
//                                                    }
//                                                }
//
//                                                override fun onFailure(
//                                                    call: Call<PassResponse>,
//                                                    t: Throwable
//                                                ) {
//                                                    if (!Utils.isOnline(context!!)) {
//                                                        Utils.logError(
//                                                            "startTest, startPairing",
//                                                            "no network",
//                                                            t
//                                                        )
//                                                        updateData(
//                                                            dataModel,
//                                                            false,
//                                                            Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
//                                                            0
//                                                        )
//                                                    } else {
//                                                        Utils.logError(
//                                                            "startTest, startPairing",
//                                                            "server error",
//                                                            t
//                                                        )
//                                                        updateData(
//                                                            dataModel,
//                                                            false,
//                                                            Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
//                                                            0
//                                                        )
//                                                    }
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
//
//                        override fun onFailure(call: Call<PositionResponse>, t: Throwable) {
//                            if (!Utils.isOnline(context!!)) {
//                                Utils.logError("startTest, checkPosition", "no network", t)
//                                updateData(
//                                    dataModel,
//                                    false,
//                                    Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
//                                    0
//                                )
//                            } else {
//                                Utils.logError("startTest, checkPosition", "server error", t)
//                                updateData(
//                                    dataModel,
//                                    false,
//                                    Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
//                                    0
//                                )
//                            }
//                        }
//
//                    })
//            }
//            if (!done) {
//                ApiNetwork.services
//                    .testFailNormal(dataModel.deviceSerial, dataModel.vin, dataModel.testerCompany)
//                    .enqueue(object : Callback<FailResponse> {
//                        override fun onFailure(call: Call<FailResponse>, t: Throwable) {
//                            if (!Utils.isOnline(context!!)) {
//                                Utils.logError("startTest, testFail", "no network", t)
//                                updateData(
//                                    dataModel,
//                                    false,
//                                    Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
//                                    0
//                                )
//                            } else {
//                                Utils.logError("startTest, testFail", "server error", t)
//                                updateData(
//                                    dataModel,
//                                    false,
//                                    Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
//                                    0
//                                )
//                            }
//                        }
//
//                        override fun onResponse(
//                            call: Call<FailResponse>,
//                            response: Response<FailResponse>
//                        ) {
//                            Utils.logSuccess(
//                                "startTest, testFail",
//                                "onSuccess : " + response.code()
//                            )
//                            if (response.isSuccessful) {
//                                updateData(dataModel, false, status, 0)
//                            }
//                        }
//                    })
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        recyclerViewMain.smoothScrollToPosition(recyclerAdapter.itemCount)
        viewModel.getDataProcess()
    }

    private fun setTest() {
        switchTest.isChecked = DataManager.isTestOn()
        switchTest.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(activity)
        dialog.setCancelable(false)
        if (DataManager.isTestOn()) {
            dialog.setTitle("Turn OFF functional test?")
            dialog.setMessage("Are you sure want to turn OFF functional test?")
            dialog.setPositiveButton("YES") { dialogInterface, i ->
                DataManager.setTest(false)
                switchTest.isChecked = false
                Utils.logSuccess("setTest", DataManager.isTestOn().toString())
                dialogInterface.dismiss()
            }
            dialog.setNegativeButton("NO") { dialogInterface, i ->
                switchTest.isChecked = true
                dialogInterface.dismiss()
            }
            dialog.show()
        } else {
            dialog.setTitle("Turn ON functional test?")
            dialog.setMessage("Are you sure want to turn ON functional test?")
            dialog.setPositiveButton("YES") { dialogInterface, i ->
                DataManager.setTest(true)
                switchTest.isChecked = true
                Utils.logSuccess("setTest", DataManager.isTestOn().toString())
                dialogInterface.dismiss()
            }
            dialog.setNegativeButton("NO") { dialogInterface, i ->
                switchTest.isChecked = false
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }

    private fun fabClick() {
        fabMain.setOnClickListener { startActivity(Intent(activity, PairActivity::class.java)) }
    }

    private fun updateData(dataModelEach: DataModel, loading: Boolean, status: Int, progress: Int) {
        val date = getCurrentDateTime()
        val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

//        DataManager.setStatus(dataModelEach.id, status)

        val mainActivity = activity as MainActivity
        viewModel.deleteData(dataModelEach)

//        mainActivity.showHideFragment()

        dataModelEach.status = status
        dataModelEach.loading = loading
        dataModelEach.progress = progress
        dataModelEach.timeRunning = loading
        dataModelEach.timestamp = "Start : " + dataModelEach.timestamp + " End : " + dateInString.substring(11, 19)
//        recyclerAdapter.notifyItemRemoved(dataModelEach.position)

        DataManager.setData(dataModelEach.id, dataModelEach)

//        recyclerAdapter.notifyItemRemoved(dataModelEach.position)
//        recyclerAdapter.notifyDataSetChanged()
        viewModel.addData(dataModelEach)
//
        mainActivity.showHideFragment()
//        recyclerViewMain.findViewHolderForAdapterPosition(dataModelEach.position)!!.itemView.performClick()
//        CoroutineScope(IO).launch {
//            repeat(100){
//                Timber.d("repeat $it")
//                LocalDatabase.getInstance(mContext).taskDao().updateData(dataModelEach)
//                viewModel.getDataProcess()
//            }
//        }
//        recyclerAdapter.notifyDataSetChanged()
        viewModel.getDataProcess()
//        recyclerViewMain.apply {
//            adapter = recyclerAdapter
//        }


//        recyclerAdapter.notifyDataSetChanged()

        Utils.logSuccess("updateData", "updated $dataModelEach")
    }


    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    override fun onDestroy() {
        super.onDestroy()
        updateFailData()
    }

    private fun updateFailData() {
        Utils.logSuccess("updateFailData", "called")
        if (dataModel.isNotEmpty()) {
            for (item in dataModel) {
                if (item.loading) {
                    updateDataFail(item, false, STATUS_FAIL, 0)
                    Utils.logSuccess("updateFailData", "updated")
                }
            }
        }
    }

    private fun updateDataFail(dataModel: DataModel, loading: Boolean, status: Int, progress: Int) {

        dataModel.status = status
        dataModel.loading = loading
        dataModel.progress = progress
        dataModel.timeRunning = loading
        viewModel.updateData(dataModel)

        Utils.logSuccess("updateData", "updated" + dataModel.status)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            viewModel.getDataProcess()
        } else {
            viewModel.getDataProcess()
        }
    }

    fun String.toCorrectDate() : String {
        return this.replace("T"," ").replace("+", " ").substring(0,19)
    }

    /* LIVE SERVER */
    fun startPairingLiveWithoutTest(dataModel: DataModel) {
        Utils.logSuccess("startPairingWithoutTest ", "try " + dataModel.vin)
        CoroutineScope(IO).launch {
            Utils.logSuccess("startPairingWithoutTest ", "inside " + dataModel.vin)
            val start = JsonObject()
            start.addProperty("imei", dataModel.imei)
            start.addProperty("serial", dataModel.deviceSerial)
            start.addProperty("testerCompany", dataModel.testerCompany)
            start.addProperty("vin", dataModel.vin)
            ApiNetwork(mContext).services
                .startInspectionLive(start.toString())
                .enqueue(object : Callback<StartLive> {
                    override fun onFailure(call: Call<StartLive>, t: Throwable) {
                        Utils.logError("startPairingWithoutTest, startInspection", "onFailure", t)
                        if (!Utils.isOnline(context!!)) {
                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
                        } else {
                            updateData(
                                dataModel,
                                false,
                                Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                0
                            )
                        }
                    }

                    override fun onResponse(
                        call: Call<StartLive>,
                        response: Response<StartLive>
                    ) {
                        Utils.logSuccess(
                            "startPairingWithoutTest, startInspection",
                            "onResponse, code : " + response.code()
                        )
                        if (response.isSuccessful && response.body()!!.status_code == 200) {
                            val startPair = JsonObject()
                            startPair.addProperty("imei", dataModel.imei)
                            startPair.addProperty("serial", dataModel.deviceSerial)
                            startPair.addProperty("vin", dataModel.vin)
                            ApiNetwork(mContext).services
                                .startPairingLive(startPair.toString())
                                .enqueue(object : Callback<PassLive> {
                                    override fun onFailure(call: Call<PassLive>, t: Throwable) {
                                        Utils.logError(
                                            "startPairingWithoutTest, startPair",
                                            "onFailure",
                                            t
                                        )
                                        if (!Utils.isOnline(context!!)) {
                                            updateData(
                                                dataModel,
                                                false,
                                                Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                                0
                                            )
                                        } else {
                                            updateData(
                                                dataModel,
                                                false,
                                                Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                                0
                                            )
                                        }
                                    }

                                    override fun onResponse(
                                        call: Call<PassLive>,
                                        response: Response<PassLive>
                                    ) {
                                        Utils.logSuccess(
                                            "startPairingWithoutTest, startPair",
                                            "onResponse, code : " + response.code()
                                        )
//                                        if (response.isSuccessful) {
                                            updateData(
                                                dataModel,
                                                false,
                                                Constant.STATUS_SUCCESS,
                                                100
                                            )
//                                        }
                                    }
                                })
                        } else if (response.body()!!.status_code == 505){
                            updateData(
                                dataModel,
                                false,
                                Constant.STATUS_SUCCESS_BUT_FAIL,
                                100
                            )
                        }
                    }
                })
        }
    }

    fun startPairingLiveWithTest(dataModel: DataModel) {
        Utils.logSuccess("startPairingWithTest", "called")
        CoroutineScope(IO).launch {
//            delay(3000)
            Utils.logSuccess("startPairingWithTest", "inside called")

            val start = JsonObject()
            start.addProperty("imei", dataModel.imei)
            start.addProperty("serial", dataModel.deviceSerial)
            start.addProperty("testerCompany", dataModel.testerCompany)
            start.addProperty("vin", dataModel.vin)

            ApiNetwork(mContext).services
                .startInspectionLive(start.toString())
                .enqueue(object : Callback<StartLive> {
                    override fun onResponse(
                        call: Call<StartLive>,
                        response: Response<StartLive>
                    ) {
                        Utils.logSuccess(
                            "startPairingWithTest, startInspection",
                            "onResponse, code : " + response.code()
                        )
                        if (response.isSuccessful && response.body()!!.status_code == 200) {
                            startLiveTest(dataModel)
                        } else if (response.body()!!.status_code == 505){
                            updateData(
                                dataModel,
                                false,
                                Constant.STATUS_SUCCESS_BUT_FAIL,
                                100
                            )
                        }
                    }

                    override fun onFailure(call: Call<StartLive>, t: Throwable) {
                        Utils.logError("startPairingWithTest, startInspection", "onFailure", t)
                        if (!Utils.isOnline(context!!)) {
                            updateData(dataModel, false, Constant.STATUS_DEFECT_OPERATOR_SIGNAL, 0)
                            Utils.logError("startTest, checkPosition", "no network", t)
                        } else {
                            updateData(
                                dataModel,
                                false,
                                Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                0
                            )
                            Utils.logError("startTest, checkPosition", "server error", t)
                        }
                    }
                })
        }
    }

    fun startLiveTest(dataModel: DataModel) {
        CoroutineScope(IO).launch {
            var status = 0
            var done = false
            repeat(REPEAT_TEST) {
                delay(50000)

                val position = JsonObject()
                position.addProperty("imei", dataModel.imei)
                position.addProperty("serial", dataModel.deviceSerial)
                ApiNetwork(mContext).services
                    .checkPositionLive(position.toString())
                    .enqueue(object : Callback<PositionLive> {
                        override fun onResponse(
                            call: Call<PositionLive>,
                            response: Response<PositionLive>
                        ) {
                            Utils.logSuccess(
                                "startTest, checkPosition",
                                "onSuccess : " + response.code()
                            )

                            if (response.isSuccessful && response.body()!!.status_code != 500) {
                                val deviceTimestamp =
                                    response.body()!!.data.deviceCoordinates.positionTimestamp
                                val serverTimestamp =
                                    response.body()!!.data.timestamp.toCorrectDate()
                                val deviceLatitude =
                                    response.body()!!.data.deviceCoordinates.latitude
                                val deviceLongitude =
                                    response.body()!!.data.deviceCoordinates.longitude
                                if (Utils.getTimeBetween(deviceTimestamp, serverTimestamp)) {
                                    if (Utils.getDistance(deviceLatitude, deviceLongitude)) {
                                        val startPair = JsonObject()
                                        startPair.addProperty("imei", dataModel.imei)
                                        startPair.addProperty("serial", dataModel.deviceSerial)
                                        startPair.addProperty("vin", dataModel.vin)
                                        ApiNetwork(mContext).services
                                            .startPairingLive(startPair.toString())
                                            .enqueue(object : Callback<PassLive> {
                                                override fun onResponse(
                                                    call: Call<PassLive>,
                                                    response: Response<PassLive>
                                                ) {
                                                    Utils.logSuccess(
                                                        "startTest, startPairing",
                                                        "onSuccess : " + response.code()
                                                    )
                                                    if (response.isSuccessful) {
                                                        updateData(
                                                            dataModel,
                                                            false,
                                                            Constant.STATUS_SUCCESS,
                                                            100
                                                        )
                                                        done = true
                                                        cancel()
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<PassLive>,
                                                    t: Throwable
                                                ) {
                                                    if (!Utils.isOnline(context!!)) {
                                                        Utils.logError(
                                                            "startTest, startPairing",
                                                            "no network",
                                                            t
                                                        )
                                                        updateData(
                                                            dataModel,
                                                            false,
                                                            Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                                            0
                                                        )
                                                    } else {
                                                        Utils.logError(
                                                            "startTest, startPairing",
                                                            "server error",
                                                            t
                                                        )
                                                        updateData(
                                                            dataModel,
                                                            false,
                                                            Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                                            0
                                                        )
                                                    }
                                                }

                                            })
                                    } else {
                                        status = Constant.STATUS_DEFECT_NOT_IN_DISTANCE
                                        DataManager.setStatus(dataModel.id, status)
                                    }
                                } else {
                                    status = Constant.STATUS_DEFECT_TIMESTAMP
                                    DataManager.setStatus(dataModel.id, status)
                                }
                            } else {
                                status = Constant.STATUS_DEFECT_SERVER_BUSY
                                DataManager.setStatus(dataModel.id, status)
                            }
                        }

                        override fun onFailure(call: Call<PositionLive>, t: Throwable) {
                            if (!Utils.isOnline(context!!)) {
                                Utils.logError("startTest, checkPosition", "no network", t)
                                updateData(
                                    dataModel,
                                    false,
                                    Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                    0
                                )
                            } else {
                                Utils.logError("startTest, checkPosition", "server error", t)
                                updateData(
                                    dataModel,
                                    false,
                                    Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                    0
                                )
                            }
                        }

                    })
            }
            if (!done) {
                val failPair = JsonObject()
                failPair.addProperty("imei", dataModel.imei)
                failPair.addProperty("serial", dataModel.deviceSerial)
                ApiNetwork(mContext).services
                    .testFailLive(failPair.toString())
                    .enqueue(object : Callback<FailLive> {
                        override fun onFailure(call: Call<FailLive>, t: Throwable) {
                            if (!Utils.isOnline(context!!)) {
                                Utils.logError("startTest, testFail", "no network", t)
                                updateData(
                                    dataModel,
                                    false,
                                    Constant.STATUS_DEFECT_OPERATOR_SIGNAL,
                                    0
                                )
                            } else {
                                Utils.logError("startTest, testFail", "server error", t)
                                updateData(
                                    dataModel,
                                    false,
                                    Constant.STATUS_DEFECT_NOT_REPLY_FROM_SERVER,
                                    0
                                )
                            }
                        }

                        override fun onResponse(
                            call: Call<FailLive>,
                            response: Response<FailLive>
                        ) {
                            Utils.logSuccess(
                                "startTest, testFail",
                                "onSuccess : " + response.code()
                            )
                            if (response.isSuccessful) {
                                updateData(dataModel, false, status, 0)
                            }
                        }
                    })
            } else {
                updateData(
                    dataModel,
                    false,
                    Constant.STATUS_DEFECT_SERVER_BUSY,
                    0
                )
            }
        }
    }

}
