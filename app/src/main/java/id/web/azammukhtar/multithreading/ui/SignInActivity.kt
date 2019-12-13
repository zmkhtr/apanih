package id.web.azammukhtar.multithreading.ui

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.journeyapps.barcodescanner.Util
import id.web.azammukhtar.multithreading.R
import id.web.azammukhtar.multithreading.network.ApiNetwork
import id.web.azammukhtar.multithreading.network.liveModel.LoginLive
import id.web.azammukhtar.multithreading.utils.BaseActivity
import id.web.azammukhtar.multithreading.utils.DataManager
import id.web.azammukhtar.multithreading.utils.Utils
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SignInActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        supportActionBar!!.hide()
        login()
    }

    private fun login() {
        val progressDialog = ProgressDialog(this)
        btnLoginProceed.setOnClickListener {

            val username = edtLoginUsername.text.toString().trim()
            val password = edtLoginPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                createToast("Username and Password cannot be empty")
                progressDialog.dismiss()
            } else {
                progressDialog.setCancelable(false)
                progressDialog.setMessage("Loading...")
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog.progress = 0
                progressDialog.show()
                ApiNetwork(this).services.login("password", username, password)
                    .enqueue(object : Callback<LoginLive> {
                        override fun onResponse(
                            call: Call<LoginLive>,
                            response: Response<LoginLive>
                        ) {
                            if (response.isSuccessful) {
                                DataManager.setLogin(true, response.body()!!.access_token)
                                createToast("Login Success")
                                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                finish()
                                progressDialog.dismiss()
                            } else {
                                createToast("Email or Password Wrong")
                                progressDialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<LoginLive>, t: Throwable) {
                            Utils.logError("Login", " login error ", t)
                            createToast("Server error : $t")
                            progressDialog.dismiss()
                        }

                    })

            }
        }
    }
}
