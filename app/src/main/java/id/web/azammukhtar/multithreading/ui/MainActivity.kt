package id.web.azammukhtar.multithreading.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import id.web.azammukhtar.multithreading.adapter.RecyclerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import id.web.azammukhtar.multithreading.room.DataModel
import android.location.LocationManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.web.azammukhtar.multithreading.ui.fragment.FailFragment
import id.web.azammukhtar.multithreading.ui.fragment.HomeFragment
import id.web.azammukhtar.multithreading.ui.fragment.SuccessFragment
import id.web.azammukhtar.multithreading.utils.BaseActivity
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class MainActivity : BaseActivity() {

    //Fragment
    private val homeFragment = HomeFragment()
    private val successFragment = SuccessFragment()
    private val failFragment = FailFragment()
    private val fragmentManager = supportFragmentManager
    private var active: Fragment = homeFragment
    private lateinit var  view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        locationAndCoarsePermission()
        startLocationUpdates()
        setActionBarTitle("Welcome Admin")

        val time = "2019-11-20T05:04:01.000+0000".replace("T"," ").replace("+", " ").substring(0,19)

        Utils.logSuccess("TEST CHANGE TIME ", time)

        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener)

        fragmentManager.beginTransaction().add(R.id.fragmentContainer, failFragment, "3")
            .hide(failFragment).commit()
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, successFragment, "2")
            .hide(successFragment).commit()
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, homeFragment, "1").commit()

    }

    fun directToHome(){
        view = bottomNavigation.findViewById(R.id.navigation_home)
        view.performClick()
    }


    private val bottomNavigationListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_home -> {
                        fragmentManager.beginTransaction().hide(active).show(homeFragment).commit()
                        active = homeFragment
                        fragmentManager.beginTransaction().hide(active).show(active).commit()
                        startLocationUpdates()
                        return true
                    }

                    R.id.navigation_success -> {
                        fragmentManager.beginTransaction().hide(active).show(successFragment).commit()
                        active = successFragment
                        startLocationUpdates()
                        return true
                    }

                    R.id.navigation_fail -> {
                        fragmentManager.beginTransaction().hide(active).show(failFragment).commit()
                        active = failFragment
                        startLocationUpdates()
                        return true
                    }
                }
                return false
            }
        }

    override fun onResume() {
        super.onResume()
        fragmentManager.beginTransaction().show(active).commit()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fragmentManager.beginTransaction().hide(active).commit()
        startLocationUpdates()
    }

    fun showHideFragment() {
        if (!DataManager.getBoolean(DataManager.PAIR_ACTIVITY_KEY)) {
            fragmentManager.beginTransaction().hide(active).commit()
            fragmentManager.beginTransaction().show(active).commit()
        }
        startLocationUpdates()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.logout) {
            logoutDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Logout")
        dialog.setMessage("Are you sure want to Logout?")
        dialog.setPositiveButton("YES") { dialogInterface, i ->
            DataManager.setLogin(false, "#")
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
            dialogInterface.dismiss()
            createToast("Logout Success")
        }
        dialog.setNegativeButton("NO") { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        dialog.show()
    }
}