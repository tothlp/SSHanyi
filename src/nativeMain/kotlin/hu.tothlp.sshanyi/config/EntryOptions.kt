package hu.tothlp.sshanyi.config

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int

/**
 * Setting values for the new configuration entry.
 *
 * @property host Custom name for the Host, uses [prompt].
 * @property hostName   Domain or IP address, uses [prompt].
 * @property user  User for SSH connection
 * @property port  Port for SSH connection
 */
class EntryOptions : OptionGroup("Server entry options") {
	val host: String by option(help = "Custom name for the Host").prompt("Host")
	val hostName: String by option(help = "Hostname (can be domain or IP address)").prompt("HostName")
	val user: String? by option(help = "User for SSH connection. Defaults to current user.")
	val port: Int? by option(help = "Port for SSH connection. Defaults to 22.").int().convert("NUMBER") { it }
}
