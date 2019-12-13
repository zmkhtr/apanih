package id.web.azammukhtar.multithreading.network

import android.app.Application
import android.content.Context
import android.text.TextUtils
import com.chuckerteam.chucker.api.ChuckerInterceptor
import id.web.azammukhtar.multithreading.BuildConfig
import id.web.azammukhtar.multithreading.utils.Constant.BASE_URL
import id.web.azammukhtar.multithreading.utils.DataManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class ApiNetwork(context: Context) {

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val request = original.newBuilder()

                if (DataManager.getToken() == "#"){
                    request
                        .addHeader("accept", "*/*")
                        .addHeader("Accept-Encoding","gzip, deflate")
                        .addHeader("Authorization", "Basic MDAwMDAwMDAtMDAwMC0wMDAwLTAwMDAtMDAwMDAwMDAwMDAwOkFnaG9oNWFpeG9UYWlCNkphaHBhZTBlZQ==")
                        .addHeader("Content-Type","application/x-www-form-urlencoded")
                } else {
                    request
                        .addHeader("accept", "application/json")
                        .addHeader("Authorization",  "Bearer " + DataManager.getToken())
//                        .addHeader("Content-Type","application/json")
                }

                return chain.proceed(request.build())
            }
        })
        .addInterceptor(ChuckerInterceptor(context))
        .retryOnConnectionFailure(true)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS  )
        .build()
//
//    private val request = Request.Builder()
//        .addHeader("Connection","close")
//        .url(BASE_URL)
//        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val services: ApiInterface = retrofit.create(ApiInterface::class.java)

}