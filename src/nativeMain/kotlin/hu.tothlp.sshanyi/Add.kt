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

	private val config by ConfigOptions()
	private val entryOptions by EntryOptions()

	override fun run() {
		echo("New config entry:")
		echo("\tHost: ${entryOptions.host}")
		echo("\tHostName: ${entryOptions.hostName}")
		entryOptions.user?.let { echo("\tUser: $it") }
		entryOptions.port?.let { echo("\tPort: $it") }
		confirm("A new entry will be added to your ${config.config} file. Continue?", abort = true)
		appendConfig()
		echo("Entry added. Now you can access your server with: ssh ${entryOptions.host}")
	}

	private fun appendConfig() {
		FileSystem.SYSTEM.appendingSink(config.config).buffer().use {
			it.writeUtf8("\n")
			it.writeUtf8("Host ${entryOptions.host}\n")
			it.writeUtf8("HostName ${entryOptions.hostName}\n")
			if(entryOptions.user != null) it.writeUtf8("User ${entryOptions.user}\n")
			if(entryOptions.port != null) it.writeUtf8("Port ${entryOptions.port}\n")
		}
	}
}