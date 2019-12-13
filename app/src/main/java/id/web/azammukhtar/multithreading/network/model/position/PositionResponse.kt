package id.web.azammukhtar.multithreading.network.model.position

data class PositionResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)