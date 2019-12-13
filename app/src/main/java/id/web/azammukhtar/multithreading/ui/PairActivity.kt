package id.web.azammukhtar.multithreading.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.zxing.integration.android.IntentIntegrator
import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout
import ernestoyaquello.com.verticalstepperform.interfaces.VerticalStepperForm
import kotlinx.android.synthetic.main.activity_pair.*
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.step_scan_barcode.view.*
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.room.DataModel
import id.web.azammukhtar.multithreading.room.Injection
import id.web.azammukhtar.multithreading.room.ViewModelFactory
import id.web.azammukhtar.multithreading.room.viewModel.CreateNewDataViewModel
import id.web.azammukhtar.multithreading.ui.fragment.HomeFragment.Companion.DATA_PROCESS
import id.web.azammukhtar.multithreading.utils.BaseActivity
import id.web.azammukhtar.multithreading.utils.Constant.STATUS_PROCESS
import id.web.azammukhtar.multithreading.utils.Constant.TEST_TIME
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.utils.Utils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import id.web.azammukhtar.multithreading.utils.Constant.VENDOR_ID


class PairActivity : AppCompatActivity(), VerticalStepperForm {

    private lateinit var scanBarcodeLayout : View
    private lateinit var scanQRLayout : View
    private lateinit var scanKey : String
    private var barcodeKey : String = ""
    private var qrCodeKey : String = ""

    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: CreateNewDataViewModel

    private lateinit var radio : RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(id.web.azammukhtar.multithreading.R.layout.activity_pair)

        DataManager.setBoolean(DataManager.PAIR_ACTIVITY_KEY, true)

        val vendor = "Company :  $VENDOR_ID"
        vendorId.text = vendor

        initStepper()
//        getRadioButtonValue()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Pair"

        viewModelFactory = Injection.provideViewModelFactory(this)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CreateNewDataViewModel::class.java)

//        btnPairRandom.setOnClickListener {
//            val randomVIN = (10000000000000000..99999999999999999).random().toString()
//            edtPairVin.text!!.append(randomVIN)
//        }
    }

    private fun initStepper() {
        val steps = arrayOf("Scan VIN vehicle", "Scan SERIAL NUMBER device")
        VerticalStepperFormLayout.Builder.newInstance(this.stepperPair, steps, this, this)
            .primaryColor(resources.getColor(id.web.azammukhtar.multithreading.R.color.colorPrimary))
            .primaryDarkColor(resources.getColor(id.web.azammukhtar.multithreading.R.color.colorPrimary))
            .displayBottomNavigation(false) // It is true by default, so in this case this line is not necessary
            .init()
    }

    override fun onDestroy() {
        super.onDestroy()
        DataManager.setBoolean(DataManager.PAIR_ACTIVITY_KEY, false)
    }

    /* STEPPER */
    override fun createStepContentView(stepNumber: Int): View {
        lateinit var view: View
        when (stepNumber) {
            0 -> view = scanBarcodeLayout()
            1 -> view = scanQRCodeLayout()
        }
        return view
    }

    override fun onStepOpening(stepNumber: Int) {
        when (stepNumber) {
            0 -> checkBarcode()
            1 -> checkQRCode()
//            2 -> stepperPair.setStepAsCompleted(2)
        }
    }

    private fun checkQRCode() {
       if (scanQRLayout.textOpenCamera.text.toString() == qrCodeKey){
            stepperPair.setActiveStepAsCompleted()
        } else if (scanQRLayout.textOpenCamera.text == "Click to open camera") {
           stepperPair.setActiveStepAsUncompleted("Please Scan the SERIAL NUMBER to continue")
       }
    }

    private fun checkBarcode() {
         if (scanQRLayout.textOpenCamera.text.toString() == barcodeKey){
            stepperPair.setActiveStepAsCompleted()
        } else if (scanBarcodeLayout.textOpenCamera.text == "Click to open camera") {
             stepperPair.setActiveStepAsUncompleted("Please Scan the VIN to continue")
         }
    }

    override fun sendData() {

            showDialog()

    }
    /* END STEPPER */

    private fun showDialog(){
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Pairing confirmation")
        alertDialog.setMessage("Are you sure want to start pairing ? \n" + "VIN : "
                + barcodeKey + "\nSERIAL : " + qrCodeKey)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "OK"
        ) { _, _ ->
            Toast.makeText(this@PairActivity, "Pairing Started", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()


            val date = getCurrentDateTime()
            val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

            DATA_PROCESS = barcodeKey
            Timber.d("LOG $barcodeKey" )
            viewModel.addData(DataModel(
                vin = barcodeKey,
                deviceSerial = qrCodeKey,
                imei = "",
                loading = true,
                testerCompany = VENDOR_ID,
                timestamp = dateInString,
                status = STATUS_PROCESS,
//                time = 2700,
                time = TEST_TIME.toLong(),
                timeRunning = true,
                endTime = 0,
                progress = 0,
                position = -1
            ))
            onBackPressed()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "CANCEL"
        ) { _, _ ->
            Toast.makeText(this@PairActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

//    private fun getRadioButtonValue(){
//        radioGroup.setOnCheckedChangeListener { _, checkedId ->
//            val radio: RadioButton = findViewById(checkedId)
//            Toast.makeText(applicationContext," On checked change : ${radio.text}",
//                Toast.LENGTH_SHORT).show()
//        }
//
//        val id: Int = radioGroup.checkedRadioButtonId
//        if (id!=-1){
//            radio = findViewById(id)
//            Toast.makeText(applicationContext,"On button click : ${radio.text}",
//                Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    /* STEPPER LAYOUT */
    @SuppressLint("InflateParams")
    private fun scanBarcodeLayout(): View {
        val layoutInflater = LayoutInflater.from(this)
        scanBarcodeLayout = layoutInflater.inflate(id.web.azammukhtar.multithreading.R.layout.step_scan_barcode, null, false)

        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.captureActivity = ScanActivity::class.java
        intentIntegrator.setPrompt("Scan Barcode")
        scanBarcodeLayout.textOpenCamera.setOnClickListener {
            scanKey = "barcode"
            intentIntegrator.initiateScan()
            Log.d("scanBarcodeLayout cc : ", " Scan key $scanKey")
        }
        return scanBarcodeLayout
    }

    @SuppressLint("InflateParams")
    private fun scanQRCodeLayout(): View {
        val layoutInflater = LayoutInflater.from(this)
        scanQRLayout = layoutInflater.inflate(id.web.azammukhtar.multithreading.R.layout.step_scan_qrcode, null, false)

        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.setPrompt("Scan QR Code")
        intentIntegrator.captureActivity = ScanActivity::class.java

        scanQRLayout.textOpenCamera.setOnClickListener {
            scanKey = "QRCode"
            intentIntegrator.initiateScan()
            Log.d("scanQRCodeLayout cc : ", " Scan key $scanKey")
        }
        return scanQRLayout
    }

    /* END STEPPER LAYOUT*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                if (scanKey == "barcode") {
                    val scannedData = "Data = " + result.contents
                    barcodeKey = result.contents
                    scanBarcodeLayout.textOpenCamera.text = scannedData
                    scanBarcodeLayout.textClickAgain.visibility = View.VISIBLE
                    if(barcodeKey.length != 17){
                        Timber.d("vin not 17 $barcodeKey")
                        createDialog("VIN IS NOT 17 DIGIT", "Do you still want to continue?")
                    } else {
                        Timber.d("vin 17 $barcodeKey")
                        stepperPair.setActiveStepAsCompleted()
                    }
                } else if (scanKey == "QRCode") {
                    val scannedData = "Data = " + result.contents
                    qrCodeKey = result.contents
                    scanQRLayout.textOpenCamera.text = scannedData
                    scanQRLayout.textClickAgain.visibility = View.VISIBLE
                    if(!qrCodeKey.contains("_")){
                        Timber.d("serial not $scanKey")
                        createDialog("SERIAL NOT CONTAINS UNDERSCORE", "Do you still want to continue?")
                    } else {
                        Timber.d("serial $scanKey")
                        stepperPair.setActiveStepAsCompleted()
                    }
                }
                Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
                Log.d("Scanned : ",  result.contents + " Scan key " + scanKey)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun createDialog(title: String, message: String){
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setCancelable(false)
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK"){ dialog, i ->
            dialog.dismiss()
            stepperPair.setActiveStepAsCompleted()
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL"){ dialog, i ->
            dialog.dismiss()
            stepperPair.setActiveStepAsUncompleted(title)
        }
            alertDialog.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
