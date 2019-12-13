package id.web.azammukhtar.multithreading.network.model.start

data class StartResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)