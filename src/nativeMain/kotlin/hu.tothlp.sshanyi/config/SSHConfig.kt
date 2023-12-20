package hu.tothlp.sshanyi.config

/**
 * DTO for storing configuration entries.
 */
data class SSHConfig(
    var host: String? = null,
    var hostName: String? = null,
    var user: String? = null,
    var port: Int? = null
)

/**
 * Used to reference config keys easily and coherently.
 */
enum class ConfigName(val value: String) {
    HOST("Host"),
    HOSTNAME("HostName"),
    USER("User"),
    PORT("Port"),
}

/**
 * Returns the String representation of the receiver Int, or empty String if conversion is not possible.
 */
fun Int?.toStringOrEmpty(): String = this?.toString().orEmpty()