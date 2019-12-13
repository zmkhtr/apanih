package id.web.azammukhtar.multithreading.network.liveModel.fail

data class FailLive(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)