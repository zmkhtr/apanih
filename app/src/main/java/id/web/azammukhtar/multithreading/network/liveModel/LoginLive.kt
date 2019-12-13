package id.web.azammukhtar.multithreading.network.liveModel

data class LoginLive(
    val access_token: String,
    val aud: String,
    val expires_in: Int,
    val id_token: String,
    val iss: String,
    val scope: String,
    val token_type: String
)