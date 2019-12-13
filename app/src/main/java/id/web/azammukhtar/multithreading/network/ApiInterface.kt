package id.web.azammukhtar.multithreading.network

import id.web.azammukhtar.multithreading.network.liveModel.LoginLive
import id.web.azammukhtar.multithreading.network.liveModel.fail.FailLive
import id.web.azammukhtar.multithreading.network.liveModel.pass.PassLive
import id.web.azammukhtar.multithreading.network.liveModel.position.PositionLive
import id.web.azammukhtar.multithreading.network.liveModel.start.StartLive
import id.web.azammukhtar.multithreading.network.model.fail.FailResponse
import id.web.azammukhtar.multithreading.network.model.pass.PassResponse
import id.web.azammukhtar.multithreading.network.model.position.PositionResponse
import id.web.azammukhtar.multithreading.network.model.start.StartResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("/device/test/test/start")
    suspend fun startInspection(@Field("vin") vin: String,
                                @Field("deviceSerial") deviceSerial: String,
                                @Field("testerCompany") testerCompany: String): StartResponse

    @FormUrlEncoded
    @POST("/device/test/test/position")
    suspend fun checkPosition(@Field("deviceSerial") deviceSerial: String)  : PositionResponse


    @FormUrlEncoded
    @POST("/device/test/test/pass")
    suspend fun startPairing(@Field("deviceSerial") deviceSerial: String,
                             @Field("vin") vin: String) : PassResponse


    @FormUrlEncoded
    @POST("/device/test/test/fail")
    suspend fun testFail(@Field("deviceSerial") deviceSerial: String) : FailResponse

    @FormUrlEncoded
    @POST("/device/test/test/start")
    fun startInspectionNormal(@Field("vin") vin: String,
                          @Field("deviceSerial") deviceSerial: String,
                          @Field("testerCompany") testerCompany: String): Call<StartResponse>

    @FormUrlEncoded
    @POST("/device/test/test/position")
    fun checkPositionNormal(@Field("deviceSerial") deviceSerial: String)  : Call<PositionResponse>


    @FormUrlEncoded
    @POST("/device/test/test/pass")
    fun startPairingNormal(@Field("deviceSerial") deviceSerial: String,
                             @Field("vin") vin: String) : Call<PassResponse>


    @FormUrlEncoded
    @POST("/device/test/test/fail")
    fun testFailNormal(@Field("deviceSerial") deviceSerial: String, @Field("vin") vin: String, @Field("testerCompany") testerCompany: String) : Call<FailResponse>


    //LIVE
    @Headers("Content-Type: application/json")
    @POST("api/device/test/start")
    fun startInspectionLive(@Body start: String) : Call<StartLive>
//    fun startInspectionLive(@Field("imei") imei: String,
//                            @Field("serial") deviceSerial: String,
//                            @Field("testerCompany") testerCompany: String,
//                            @Field("vin") vin: String): Call<StartLive>

    @Headers("Content-Type: application/json")
    @POST("api/device/test/position")
    fun checkPositionLive(@Body position: String) : Call<PositionLive>
//    fun checkPositionLive(@Field("imei") imei: String,
//                          @Field("serial") deviceSerial: String)  : Call<PositionLive>


    @Headers("Content-Type: application/json")
    @POST("api/device/test/pass")
    fun startPairingLive(@Body pass: String) : Call<PassLive>
//    fun startPairingLive(@Field("imei") imei: String,
//                         @Field("serial") deviceSerial: String,
//                         @Field("vin") vin: String) : Call<PassLive>

    @Headers("Content-Type: application/json")
    @POST("api/device/test/fail")
    fun testFailLive(@Body pass: String) : Call<FailLive>
//    fun testFailLive(@Field("imei") imei: String,
//                     @Field("serial") deviceSerial: String) : Call<FailLive>

    @FormUrlEncoded
    @POST("oidc/oauth2/token")
    fun login(@Field("grant_type") grant_type: String,
              @Field("username") username: String,
              @Field("password") password: String) : Call <LoginLive>

}