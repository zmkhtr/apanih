package id.web.azammukhtar.multithreading.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.web.azammukhtar.multithreading.R
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : Activity(), DecoratedBarcodeView.TorchListener {

    private lateinit var capture: CaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        zxing_barcode_scanner.setTorchListener(this)

        checkPermission()
        if (!hasFlash()) {
            switch_flashlight.visibility = View.GONE
        }

        capture = CaptureManager(this, zxing_barcode_scanner)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.decode()

        switchFlashlight()
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return zxing_barcode_scanner.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun switchFlashlight() {
        switch_flashlight.setOnClickListener {
            if (getString(R.string.turn_on_flashlight) == switch_flashlight.text) {
                zxing_barcode_scanner.setTorchOn()
            } else {
                zxing_barcode_scanner.setTorchOff()
            }
        }
    }

    override fun onTorchOn() {
        switch_flashlight.setText(R.string.turn_off_flashlight)
    }

    override fun onTorchOff() {
        switch_flashlight.setText(R.string.turn_on_flashlight)
    }

    private fun checkPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    Toast.makeText(this@ScanActivity, "Camera Permission Allowed", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@ScanActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                }
            })
            .check()
    }
}