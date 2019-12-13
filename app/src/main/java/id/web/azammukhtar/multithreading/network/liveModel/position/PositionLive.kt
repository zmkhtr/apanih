package id.web.azammukhtar.multithreading.network.liveModel.position

data class PositionLive(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)