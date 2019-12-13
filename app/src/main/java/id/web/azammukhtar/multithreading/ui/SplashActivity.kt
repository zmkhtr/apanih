package id.web.azammukhtar.multithreading.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.utils.Constant.SPLASH_TIME_OUT
import id.web.azammukhtar.multithreading.utils.DataManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar!!.hide()

        Handler().postDelayed({
            if (DataManager.isLoggedIn())
                startActivity(Intent(this,MainActivity::class.java))
            else
                startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}
