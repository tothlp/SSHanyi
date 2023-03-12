package hu.tothlp.sshanyi

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import okio.FileSystem
import okio.buffer
import okio.use

class Add : CliktCommand(help = "Add entries to configuration") {

	private val host: String by option(help = "Custom name for the Host").prompt("Host")
	private val hostName: String by option(help = "Hostname (can be domain or IP address)").prompt("HostName")
	private val user: String? by option(help = "User for SSH connection. Defaults to current user.")
	private val port: Int? by option(help = "Port for SSH connection. Defaults to 22.").int().convert("NUMBER") { it }
	private val config by ConfigOptions()

	override fun run() {
		echo("New config entry:")
		echo("\tHost: $host")
		echo("\tHostName: $hostName")
		user?.let { echo("\tUser: $it") }
		port?.let { echo("\tPort: $it") }
		confirm("A new entry will be added to your ${config.config} file. Continue?", abort = true)
		appendConfig()
	}

	private fun appendConfig() {
		FileSystem.SYSTEM.appendingSink(config.config).buffer().use {
			it.writeUtf8("\n")
			it.writeUtf8("Host $host\n")
			it.writeUtf8("HostName $hostName\n")
			if(user != null) it.writeUtf8("User $user\n")
			if(port != null) it.writeUtf8("Port $port\n")
		}
	}
}