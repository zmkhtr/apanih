package id.web.azammukhtar.multithreading.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.adapter.FailAdapter
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.viewModel.AllDataViewModel
import id.web.azammukhtar.multithreading.utils.Utils
import id.web.azammukhtar.multithreading.room.Injection
import id.web.azammukhtar.multithreading.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_fail.*
import id.web.azammukhtar.multithreading.utils.Constant
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.utils.Constant.TEST_TIME


class FailFragment : Fragment() {

    private lateinit var viewModel: AllDataViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var dataModel: List<DataModel>
    private lateinit var recyclerAdapter: FailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerAdapter = FailAdapter(context!!)
        linearLayoutManager = LinearLayoutManager(context)

        initRecyclerView()
        initViewModel()
        setOnClick()
    }

    private fun setCounter(){
            val counter = "Total Fail : " + dataModel.size
            textFailCounter.text = counter
    }


    private fun initRecyclerView() {
        recyclerViewFail.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            scrollToPosition(recyclerAdapter.itemCount - 1)
        }
    }

    private fun initViewModel() {
        Utils.logSuccess("initViewModel", "try")
        val viewModelFactory = Injection.provideViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AllDataViewModel::class.java)
        viewModel.getDataDefect()
        viewModel.datas.observe(this, Observer {
            if (it.isEmpty()) {
                textFailNoData.visibility = View.VISIBLE
                Utils.logSuccess("initViewModel", "empty")
                recyclerAdapter.setData(it)
                dataModel = it
                setCounter()
            } else {
                if (textFailNoData.visibility == View.VISIBLE)
                    textFailNoData.visibility = View.GONE
                Utils.logSuccess("initViewModel", "not empty")
                recyclerAdapter.setData(it)
                dataModel = it
                setCounter()
            }
        })
    }

    private fun setOnClick(){
        recyclerAdapter.setOnItemClickListener(object : FailAdapter.OnItemClick{
            override fun onItemClick(data: DataModel, holder: FailAdapter.ViewHolder) {
                val dialog = AlertDialog.Builder(activity)
                    dialog.setTitle("Choose action")
                    dialog.setMessage("Do you want delete the data?")
                    dialog.setPositiveButton("RETRY"){ dialogInterface, i ->
//                        viewModel.deleteData(data)
                        data.position = -1
                        data.loading = true
                        data.status = Constant.STATUS_PROCESS
                        data.timeRunning = true
                        data.time = TEST_TIME.toLong()
                        data.endTime = 0
                        data.progress = 0
                        viewModel.updateData(data)

                        DataManager.setData(data.id, data)

                        val mainActivity = activity as MainActivity
                        mainActivity.showHideFragment()
                        mainActivity.directToHome()
                        dialogInterface.dismiss()
                    }
                    dialog.setNegativeButton("DELETE"){dialogInterface, i ->
                        viewModel.deleteData(data)
                        DataManager.deleteDataByKey(data.id)

                        val mainActivity = activity as MainActivity
                        mainActivity.showHideFragment()
                        dialogInterface.dismiss()
                    }
                dialog.setNeutralButton("CANCEL"){dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                    dialog.show()
            }
        })
    }
    override fun onResume() {
        super.onResume()
//        recyclerViewFail.smoothScrollToPosition(recyclerAdapter.itemCount)
        viewModel.getDataDefect()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            viewModel.getDataDefect()
        } else {
            viewModel.getDataDefect()
        }
    }
}
