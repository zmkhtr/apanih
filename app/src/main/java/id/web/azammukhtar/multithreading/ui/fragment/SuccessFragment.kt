package id.web.azammukhtar.multithreading.ui.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_success.*
import id.web.azammukhtar.multithreading.adapter.SuccessAdapter
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.viewModel.AllDataViewModel
import id.web.azammukhtar.multithreading.utils.Utils
import id.web.azammukhtar.multithreading.room.Injection
import timber.log.Timber
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.utils.Constant

/**
 * A simple [Fragment] subclass.
 */
class SuccessFragment : Fragment() {


    private lateinit var viewModel: AllDataViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var dataModel: ArrayList<DataModel>
    private lateinit var recyclerAdapter: SuccessAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataModel = ArrayList()
        return inflater.inflate(R.layout.fragment_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerAdapter = SuccessAdapter(context!!)
        linearLayoutManager = LinearLayoutManager(context)

        initRecyclerView()
        initViewModel()
        setOnClick()
    }

    private fun setCounter(){
        val counter = "Total Success : " + dataModel.size
        textSuccessCounter.text = counter
    }

    private fun initRecyclerView() {
        recyclerViewSuccess.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
            scrollToPosition(recyclerAdapter.itemCount - 1)
        }
    }

    private fun setOnClick(){
        recyclerAdapter.setOnItemClickListener(object : SuccessAdapter.OnItemClick{
            override fun onItemClick(data: DataModel, holder: SuccessAdapter.ViewHolder) {
                Utils.logSuccess("Clicked", "Data : $data")
            }
        })
    }

    private fun initViewModel() {
        Utils.logSuccess("initViewModel", "try")
        val viewModelFactory = Injection.provideViewModelFactory(activity!!.applicationContext)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AllDataViewModel::class.java)
        viewModel.getDataSuccess()
        viewModel.datas.observe(this, Observer {
            dataModel.clear()
            if (it.isEmpty()) {
                textSuccessNoData.visibility = View.VISIBLE
                Utils.logSuccess("initViewModel", "empty")
                dataModel.addAll(it)
                recyclerAdapter.setData(it)

                setCounter()
            } else {
                if (textSuccessNoData.visibility == View.VISIBLE)
                    textSuccessNoData.visibility = View.GONE
                Utils.logSuccess("initViewModel", "not empty")
                dataModel.addAll(it)
                var totalDeleted = 0
                for (i in 0 until it.size) {
                    val modelku = DataManager.getData(it[i].id)
                    if (modelku.status != Constant.STATUS_SUCCESS) {
//                        Timber.d("FILTER DATA awak masuk sini lho it $i ${it[i].deviceSerial} kzl aing")
                        dataModel.removeAt(i - totalDeleted)
                        totalDeleted++
                    }
                }

                if (dataModel.size == 0) {

                    textSuccessNoData.visibility = View.VISIBLE
                }

                recyclerAdapter.setData(it)

                setCounter()
            }
        })
    }

    override fun onResume() {
        super.onResume()
//        recyclerViewSuccess.smoothScrollToPosition(recyclerAdapter.itemCount)
        viewModel.getDataSuccess()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden){
            viewModel.getDataSuccess()
        } else {
            viewModel.getDataSuccess()
        }
    }
}
