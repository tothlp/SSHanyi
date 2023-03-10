package hu.tothlp.sshanyi

data class SSHConfig(
    var host: String? = null,
    var hostName: String? = null,
    var user: String? = null,
    var port: Int? = null
)

enum class ConfigName(val value: String) {
    HOST("Host"),
    HOSTNAME("HostName"),
    USER("User"),
    PORT("Port"),
}

fun Int?.toStringOrEmpty(): String = this?.toString() ?: ""