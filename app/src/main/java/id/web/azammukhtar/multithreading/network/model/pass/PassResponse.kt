package id.web.azammukhtar.multithreading.network.model.pass

data class PassResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)