package id.web.azammukhtar.multithreading.utils

object Constant {
    const val ONE_MINUTES = 60000L
    const val FORTY_FIVE_TIMES = 45

//    const val BASE_URL = "http://viki.blogbeto.com/" //local server bang roberto
//    const val BASE_URL = "http://hino-sandbox-api.l72.logique.co.id/" //local server logique
//    const val BASE_URL = "https://beta.gazelletelematics.com/" //beta live server
    const val BASE_URL = "https://hino.gazellecomputing.com/" //live server

    const val CHANNEL_ID = "serviceChannel"

    const val STATUS_PROCESS = 0
    const val STATUS_SUCCESS = 1

    const val STATUS_DEFECT_TIMESTAMP = 2
    const val STATUS_DEFECT_NOT_IN_DISTANCE = 3
    const val STATUS_DEFECT_NOT_REPLY_FROM_SERVER = 4
    const val STATUS_DEFECT_OPERATOR_SIGNAL = 5
    const val STATUS_DEFECT_NO_GPS = 6
    const val STATUS_FAIL = 7
    const val STATUS_DEFECT_SERVER_BUSY = 8
    const val STATUS_SUCCESS_BUT_FAIL = 9


    const val SPLASH_TIME_OUT = 1000L

    const val VENDOR_ID = "Hino" //change company name here

    const val TEST_TIME = 600 // 300 = 5 minutes , 600 = 10 minutes , 2700 = 45 minutes
    const val END_TIME = "00:10:00" // "00:05:00" , "00:10:00" , "00:45:00"
    const val REPEAT_TEST = 10 // 5 = 5 minutes , 10 = 10 minutes , 45 = 45 minutes

//    ganti defect dengan validasi :
//    - Timestamp tidak terbaru
//    - Not in distance.
//    - Not reply from server.
//    - Operator sinyal (gara2 gada internet).
//    - GPS harus di nyalakan
}