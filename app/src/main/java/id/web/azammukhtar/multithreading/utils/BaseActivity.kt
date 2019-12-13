package id.web.azammukhtar.multithreading.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import id.web.azammukhtar.multithreading.R
import kotlinx.android.synthetic.main.fragment_home.*


open class BaseActivity : AppCompatActivity() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var mLocationRequest: LocationRequest
    private var isLogNotPrinted = true

    companion object {
        const val INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 1000
    }


//    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        return super.onCreateView(name, context, attrs)
//    }

    fun setActionBarTitle(title: String){
        supportActionBar!!.title = title
    }

    fun locationAndCoarsePermission(){
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION
//                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        createToast(getString(R.string.permission_granted))
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        createToast(getString(R.string.permission_denied))
                        buildAlertMessageNoGps()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            })
            .withErrorListener {
                createToast("Permission error, : $it")
            }
            .onSameThread()
            .check()
    }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.cancel()
                startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11
                )
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    fun createToast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    fun startLocationUpdates() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
        mLocationRequest = LocationRequest()
        // Create the location request to start receiving updates
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // new Google API SDK v11 uses getFusedLocationProviderClient(this)

        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // do work here
            locationResult.lastLocation
            locationResult.lastLocation

            DataManager.setLat(locationResult.lastLocation.latitude)
            DataManager.setLong(locationResult.lastLocation.longitude)
            val location = "Position : Lat " + DataManager.getLat() + " Long " + DataManager.getLong()
            textLocation.text = location
            if (isLogNotPrinted) {
                Utils.logSuccess(
                    "onLocationResult",
                    "POSITION Lat " + locationResult.lastLocation.latitude + " Long " + locationResult.lastLocation.longitude
                )
                isLogNotPrinted = false
            }
        }
    }

//    fun getDistance(latitude: Double, longitude: Double): Float {
//        (ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED)
//
//
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//
//        val loc1 = Location("")
//        loc1.latitude = location!!.latitude
//        loc1.longitude = location.longitude
//
//
//        Utils.logSuccess("getDistance", "Device loc " + " Lat  " + loc1.latitude + " Long " + loc1.longitude)
//
//        val loc2 = Location("")
//        loc2.latitude = latitude
//        loc2.longitude = longitude
//
//        Utils.logSuccess("getDistance", "Server loc " + " Lat  " + loc2.latitude + " Long " + loc2.longitude)
//        Utils.logSuccess("getDistance", "Distance loc" + " " + loc1.distanceTo(loc2) + " Meter ")
//
//        return loc1.distanceTo(loc2)
//    }

//    fun updateData(dataModel: DataModel, loading: Boolean, status: Int, progress: Int) {
//        Utils.logSuccess("updateData", "updated")
//        dataModel.status = status
//        dataModel.loading = loading
//        dataModel.progress = progress
//        dataModel.timeRunning = loading
//        viewModel.updateData(dataModel)
//        viewModel.allData()
//    }
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}