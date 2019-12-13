package id.web.azammukhtar.multithreading.network.model.fail

data class FailResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)